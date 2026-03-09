// ================================================================
// FILE 1: CardEligibilityRule.java — Rule Interface
// Same Strategy Pattern used in Loan module
// ================================================================
package com.fintrix.modules.creditcard.rules;

import com.fintrix.modules.creditcard.domain.CreditCard;
import com.fintrix.modules.creditcard.dto.CardRecommendationRequest;
import com.fintrix.modules.financialprofile.domain.FinancialProfile;

/**
 * CardEligibilityRule — Strategy Interface
 *
 * Every card eligibility check implements this interface.
 * CardRecommendationEngine runs all rules and aggregates results.
 *
 * Why same pattern as LoanEligibilityRule?
 *  Consistency across modules — any developer reading the
 *  loan module already understands how card rules work.
 *  This is called "Principle of Least Surprise" in software design.
 *
 * Rules implemented:
 *  1. CardCreditScoreRule   → CIBIL score vs card minimum
 *  2. CardIncomeRule        → monthly income vs card minimum
 *  3. CardEmploymentRule    → employment type accepted by card
 *  4. CardUtilizationRule   → existing utilization health check
 *
 * RuleResult fields:
 *  passed      → hard pass or fail
 *  scoreImpact → adds to match score if passed
 *  reason      → shown to user if failed
 *  tip         → how to become eligible
 */
public interface CardEligibilityRule {

    RuleResult evaluate(
            FinancialProfile       profile,
            CreditCard             card,
            CardRecommendationRequest request
    );

    record RuleResult(
            boolean passed,
            int     scoreImpact,
            String  reason,
            String  tip
    ) {
        static RuleResult pass(int scoreImpact) {
            return new RuleResult(true, scoreImpact, null, null);
        }

        static RuleResult fail(String reason, String tip) {
            return new RuleResult(false, 0, reason, tip);
        }

        static RuleResult partial(int scoreImpact,
                                   String reason, String tip) {
            return new RuleResult(true, scoreImpact, reason, tip);
        }
    }
}

