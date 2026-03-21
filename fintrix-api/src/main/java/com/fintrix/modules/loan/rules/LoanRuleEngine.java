package com.fintrix.modules.loan.rules;

import com.fintrix.modules.financialprofile.domain.FinancialProfile;
import com.fintrix.modules.loan.domain.Lender;
import com.fintrix.modules.loan.dto.LoanEligibilityRequest;
import com.fintrix.modules.loan.dto.LenderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * LoanRuleEngine
 *
 * Orchestrates all loan eligibility rules and produces
 * a final LenderResult with approval probability.
 *
 * How approval probability is computed:
 *
 *  Each rule contributes a "scoreImpact" (0–35 points).
 *  We have 3 rules: CreditScoreRule, FoirRule, EmploymentRule.
 *  Max possible score = 35 + 35 + 20 = 90 points.
 *
 *  approvalProbability = (totalScore / maxScore) × 100
 *
 *  If ANY rule hard-fails (passed=false):
 *    → that lender goes into ineligibleLenders list
 *    → but we still show WHY and HOW to fix it
 *    → this is the KEY educational value of Fintrix
 *
 *  This is NOT machine learning.
 *  This is a rule-based scoring system — deterministic,
 *  explainable, and auditable. Perfect for fintech.
 *
 *  Why not ML here?
 *   - We don't have actual approval/rejection data
 *   - Rules are transparent to users (explainable AI principle)
 *   - Rules can be updated without retraining models
 *   - Regulatory compliance: decisions must be explainable
 *
 * Design Pattern: Chain of Responsibility + Strategy
 *  Each rule is independent (Strategy).
 *  Engine chains them sequentially (Chain of Responsibility).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoanRuleEngine {

    private final CreditScoreRule creditScoreRule;
    private final FoirRule        foirRule;
    private final EmploymentRule  employmentRule;

    // Max possible score across all rules
    private static final int MAX_SCORE = 90;

    // Threshold to be considered "eligible"
    private static final int ELIGIBLE_THRESHOLD = 60;

    /**
     * Evaluate one lender against user's financial profile.
     * Returns a LenderResult with probability and explanations.
     */
    public LenderResult evaluate(
            FinancialProfile       profile,
            Lender                 lender,
            LoanEligibilityRequest request) {

        log.debug("Evaluating lender: {} for userId: {}",
                lender.getName(), profile.getUserId());

        List<String> failureReasons  = new ArrayList<>();
        List<String> improvementTips = new ArrayList<>();
        int          totalScore      = 0;
        boolean      hardFailed      = false;

        // ── Run all rules ─────────────────────────────────────
        List<LoanEligibilityRule> rules = List.of(
                creditScoreRule,
                foirRule,
                employmentRule
        );

        for (LoanEligibilityRule rule : rules) {
            LoanEligibilityRule.RuleResult result =
                    rule.evaluate(profile, lender, request);

            totalScore += result.scoreImpact();

            if (!result.passed()) {
                hardFailed = true;
                if (result.reason() != null)
                    failureReasons.add(result.reason());
                if (result.tip() != null)
                    improvementTips.add(result.tip());
            } else {
                // Partial pass may still have tips
                if (result.tip() != null)
                    improvementTips.add(result.tip());
            }
        }

        // ── Compute approval probability ──────────────────────
        int approvalProbability = hardFailed
                ? computePartialProbability(totalScore)
                : computeFullProbability(totalScore);

        // ── Compute EMI and total interest ────────────────────
        BigDecimal midRate = lender.getMinInterestRate()
                .add(lender.getMaxInterestRate())
                .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

        BigDecimal estimatedEmi = FoirRule.estimateEmi(
                request.getRequestedAmount(),
                midRate,
                request.getTenureMonths()
        );

        BigDecimal totalPayable = estimatedEmi
                .multiply(BigDecimal.valueOf(request.getTenureMonths()))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalInterest = totalPayable
                .subtract(request.getRequestedAmount())
                .setScale(2, RoundingMode.HALF_UP);

        return LenderResult.builder()
                .lenderId(lender.getId())
                .lenderName(lender.getName())
                .logoUrl(lender.getLogoUrl())
                .applyUrl(lender.getApplyUrl())
                .approvalProbability(approvalProbability)
                .isEligible(approvalProbability >= ELIGIBLE_THRESHOLD
                        && !hardFailed)
                .minInterestRate(lender.getMinInterestRate())
                .maxInterestRate(lender.getMaxInterestRate())
                .processingFeePercent(lender.getProcessingFeePercent())
                .estimatedEmi(estimatedEmi)
                .totalInterestPayable(totalInterest)
                .failureReasons(failureReasons)
                .improvementTips(improvementTips)
                .build();
    }

    // ── Full probability when all rules pass ──────────────────
    private int computeFullProbability(int totalScore) {
        // Map score to 60–98 range (never 100 — real banks have other checks)
        double raw = (double) totalScore / MAX_SCORE;
        int    prob = (int) (60 + raw * 38);
        return Math.min(prob, 98);
    }

    // ── Partial probability when hard fail occurred ───────────
    private int computePartialProbability(int totalScore) {
        // Map score to 5–55 range — not eligible but show partial score
        double raw = (double) totalScore / MAX_SCORE;
        int    prob = (int) (5 + raw * 50);
        return Math.min(prob, 55);
    }
}