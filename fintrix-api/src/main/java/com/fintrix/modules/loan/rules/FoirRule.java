
// ================================================================
// FILE 3: FoirRule.java
// ================================================================
package com.fintrix.modules.loan.rules;

import com.fintrix.modules.financialprofile.domain.FinancialProfile;
import com.fintrix.modules.loan.domain.Lender;
import com.fintrix.modules.loan.dto.LoanEligibilityRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * FoirRule — Fixed Obligation to Income Ratio Rule
 *
 * FOIR = (All existing EMIs + New proposed EMI) / Monthly Income × 100
 *
 * CRITICAL: Most loan rejections in India happen because of FOIR.
 * Banks use FOIR to check if you can afford the new loan.
 *
 * Example:
 *   Monthly Income:     ₹50,000
 *   Existing EMIs:      ₹8,000
 *   New Loan EMI:       ₹5,000  (estimated)
 *   Total obligations:  ₹13,000
 *   FOIR:              (13,000 / 50,000) × 100 = 26%  ✅ good
 *
 *   If FOIR becomes 55% after new loan → most banks reject.
 *
 * This rule computes projected FOIR AFTER the new loan
 * and checks against lender's maxFoir threshold.
 */
@Component
public class FoirRule implements LoanEligibilityRule {

    @Override
    public RuleResult evaluate(
            FinancialProfile profile,
            Lender lender,
            LoanEligibilityRequest request) {

        BigDecimal income      = profile.getMonthlyIncome();
        BigDecimal existingEmi = nullSafe(profile.getExistingEmiTotal());
        BigDecimal lenderMaxFoir = lender.getMaxFoir()
                .multiply(BigDecimal.valueOf(100)); // convert 0.50 → 50

        // Estimate EMI for the requested loan at lender's mid rate
        BigDecimal estimatedNewEmi = estimateEmi(
                request.getRequestedAmount(),
                lender.getMinInterestRate()
                        .add(lender.getMaxInterestRate())
                        .divide(BigDecimal.valueOf(2),
                                2, RoundingMode.HALF_UP),
                request.getTenureMonths()
        );

        // Projected FOIR after new loan
        BigDecimal projectedFoir = BigDecimal.ZERO;
        if (income.compareTo(BigDecimal.ZERO) > 0) {
            projectedFoir = existingEmi
                    .add(estimatedNewEmi)
                    .divide(income, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        // Hard fail
        if (projectedFoir.compareTo(lenderMaxFoir) > 0) {
            return RuleResult.fail(
                    String.format(
                        "Projected FOIR %.1f%% exceeds %s's limit of %.0f%%",
                        projectedFoir, lender.getName(), lenderMaxFoir),
                    String.format(
                        "Close existing loans or reduce requested amount " +
                        "to bring FOIR below %.0f%%. Current obligations " +
                        "are ₹%.0f/month.", lenderMaxFoir, existingEmi)
            );
        }

        // Pass — score based on how comfortable the FOIR is
        BigDecimal foirRatio = projectedFoir
                .divide(lenderMaxFoir, 4, RoundingMode.HALF_UP);

        if (foirRatio.compareTo(BigDecimal.valueOf(0.7)) <= 0)
            return RuleResult.pass(35);   // FOIR < 70% of max → very healthy
        if (foirRatio.compareTo(BigDecimal.valueOf(0.85)) <= 0)
            return RuleResult.pass(25);   // FOIR 70-85% of max → acceptable
        return RuleResult.partial(15,
                String.format("FOIR %.1f%% is close to lender limit %.0f%%",
                        projectedFoir, lenderMaxFoir),
                "Consider reducing the loan amount or tenure " +
                "to lower your projected EMI burden.");
    }

    /**
     * Standard EMI Formula:
     * EMI = P × r × (1+r)^n / ((1+r)^n - 1)
     *
     * P = principal (loan amount)
     * r = monthly interest rate (annual rate / 12 / 100)
     * n = tenure in months
     *
     * This is the same formula used by every Indian bank.
     */
    public static BigDecimal estimateEmi(
            BigDecimal principal,
            BigDecimal annualRatePercent,
            int        tenureMonths) {

        if (principal == null || principal.compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ZERO;

        // Monthly rate: r = annualRate / 12 / 100
        double r = annualRatePercent.doubleValue() / 12.0 / 100.0;
        double p = principal.doubleValue();
        int    n = tenureMonths;

        if (r == 0) {
            // Zero-interest loan (rare — consumer durable schemes)
            return principal.divide(
                    BigDecimal.valueOf(n), 2, RoundingMode.HALF_UP);
        }

        // EMI = P × r × (1+r)^n / ((1+r)^n - 1)
        double onePlusR_n = Math.pow(1 + r, n);
        double emi        = p * r * onePlusR_n / (onePlusR_n - 1);

        return BigDecimal.valueOf(emi)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal nullSafe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}

