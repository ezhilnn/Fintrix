
// ================================================================
// FILE 3: CardIncomeRule.java
// Checks monthly income against card's minimum requirement
// ================================================================
package com.fintrix.modules.creditcard.rules;

import com.fintrix.modules.creditcard.domain.CreditCard;
import com.fintrix.modules.creditcard.dto.CardRecommendationRequest;
import com.fintrix.modules.financialprofile.domain.FinancialProfile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * CardIncomeRule
 *
 * Banks use income to determine credit limit and repayment capacity.
 *
 * Indian credit card income norms:
 *  Entry-level (Kotak 811)     → ₹0   (student/first job)
 *  Standard (HDFC Millennia)   → ₹25,000/month
 *  Travel (HDFC Regalia)       → ₹1,00,000/month
 *  Premium (Axis Magnus)       → ₹1,50,000/month
 *
 * Score impact:
 *  Income >= 2x minimum → 25 points (high comfort margin)
 *  Income >= 1.5x        → 15 points (comfortable)
 *  Income >= 1x          → 8  points (just eligible)
 */
@Component
public class CardIncomeRule implements CardEligibilityRule {

    @Override
    public RuleResult evaluate(
            FinancialProfile profile,
            CreditCard card,
            CardRecommendationRequest request) {

        BigDecimal userIncome  = profile.getMonthlyIncome();
        BigDecimal minRequired = card.getMinMonthlyIncome();

        if (userIncome == null) {
            return RuleResult.fail(
                    "Monthly income data missing",
                    "Update your financial profile with " +
                    "your current monthly income.");
        }

        // Special case: card has no income requirement (e.g. Kotak 811)
        if (minRequired.compareTo(BigDecimal.ZERO) == 0) {
            return RuleResult.pass(15);
        }

        // Hard fail — below minimum income
        if (userIncome.compareTo(minRequired) < 0) {
            return RuleResult.fail(
                    String.format(
                        "%s requires minimum income of ₹%.0f/month. " +
                        "Your income: ₹%.0f/month",
                        card.getCardName(),
                        minRequired,
                        userIncome),
                    String.format(
                        "This card requires ₹%.0f/month income. " +
                        "Consider entry-level cards available at " +
                        "your current income level.",
                        minRequired)
            );
        }

        // Pass — compute income comfort ratio
        double incomeRatio = userIncome
                .divide(minRequired, 4, RoundingMode.HALF_UP)
                .doubleValue();

        if (incomeRatio >= 2.0) return RuleResult.pass(25);
        if (incomeRatio >= 1.5) return RuleResult.pass(15);
        return RuleResult.pass(8);
    }
}

