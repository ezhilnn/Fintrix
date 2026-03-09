
// ================================================================
// FILE 5: CardUtilizationRule.java
// Penalises high existing credit utilization
// ================================================================
package com.fintrix.modules.creditcard.rules;

import com.fintrix.modules.creditcard.domain.CreditCard;
import com.fintrix.modules.creditcard.dto.CardRecommendationRequest;
import com.fintrix.modules.financialprofile.domain.FinancialProfile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * CardUtilizationRule
 *
 * Banks look at existing credit card utilization
 * to judge if the user is credit-hungry.
 *
 * A person using 90% of their existing credit limit
 * and asking for a NEW card signals financial stress.
 *
 * This is NOT a hard reject rule.
 * It is a soft signal that reduces match score.
 *
 * Utilization bands:
 *  0–30%   → pass  +20 (responsible usage)
 *  30–50%  → pass  +10 (acceptable)
 *  50–75%  → partial +5 (with warning)
 *  75%+    → partial +0 (strong warning — likely to be rejected)
 */
@Component
public class CardUtilizationRule implements CardEligibilityRule {

    @Override
    public RuleResult evaluate(
            FinancialProfile profile,
            CreditCard card,
            CardRecommendationRequest request) {

        BigDecimal utilization = profile.getCurrentCreditUtilization();
        Integer    numCards    = profile.getNumberOfCreditCards();

        // No existing cards — neutral, no penalty
        if (numCards == null || numCards == 0) {
            return RuleResult.pass(10);
        }

        // Utilization not entered
        if (utilization == null) {
            return RuleResult.partial(5,
                    "Credit utilization not provided",
                    "Enter your current credit card utilization " +
                    "for accurate card matching.");
        }

        double u = utilization.doubleValue();

        if (u <= 30) {
            return RuleResult.pass(20); // responsible usage — positive signal
        }

        if (u <= 50) {
            return RuleResult.pass(10); // acceptable
        }

        if (u <= 75) {
            return RuleResult.partial(5,
                    String.format("High utilization %.0f%% may concern lenders", u),
                    "Pay down your existing credit card balance before " +
                    "applying for a new card. Aim for below 30%.");
        }

        // Above 75% — serious concern
        return RuleResult.partial(0,
                String.format(
                    "Very high utilization %.0f%% — lenders may reject " +
                    "new card application", u),
                "Your credit utilization is very high. Applying for a " +
                "new card now may be rejected and will create a hard " +
                "CIBIL inquiry. Reduce utilization first.");
    }
}