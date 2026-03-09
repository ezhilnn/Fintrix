
// ================================================================
// FILE 2: CardCreditScoreRule.java
// Checks CIBIL score against card's minimum requirement
// ================================================================
package com.fintrix.modules.creditcard.rules;

import com.fintrix.modules.creditcard.domain.CreditCard;
import com.fintrix.modules.creditcard.dto.CardRecommendationRequest;
import com.fintrix.modules.financialprofile.domain.FinancialProfile;
import org.springframework.stereotype.Component;

/**
 * CardCreditScoreRule
 *
 * Credit score is the primary gate for credit card eligibility.
 *
 * Indian bank thresholds (from seed data):
 *  Entry-level cards (Kotak 811)     → 600 minimum
 *  Standard cards (HDFC Millennia)   → 700 minimum
 *  Premium cards (Axis Magnus)       → 780 minimum
 *
 * Score impact:
 *  Margin >= 100  → 30 points (well above minimum)
 *  Margin >= 50   → 20 points (comfortably above)
 *  Margin >= 0    → 10 points (just eligible)
 *  Below minimum  → hard fail
 *
 * Unknown score:
 *  User hasn't entered credit score → partial pass with low score.
 *  We don't hard reject — user may have a good score.
 *  But we warn them to check first.
 */
@Component
public class CardCreditScoreRule implements CardEligibilityRule {

    @Override
    public RuleResult evaluate(
            FinancialProfile profile,
            CreditCard card,
            CardRecommendationRequest request) {

        Integer userScore   = profile.getCreditScore();
        int     minRequired = card.getMinCreditScore();

        // Score not provided
        if (userScore == null) {
            return RuleResult.partial(5,
                    "Credit score unknown — " + card.getBankName()
                            + " requires minimum " + minRequired,
                    "Check your free CIBIL score at cibil.com " +
                    "before applying. Applying without knowing your " +
                    "score risks rejection and CIBIL inquiry.");
        }

        // Hard fail — below minimum
        if (userScore < minRequired) {
            int gap = minRequired - userScore;
            return RuleResult.fail(
                    String.format(
                        "Your CIBIL score %d is below %s's " +
                        "minimum of %d for %s",
                        userScore, card.getBankName(),
                        minRequired, card.getCardName()),
                    String.format(
                        "You need %d more points. Pay all EMIs on time, " +
                        "keep utilization below 30%%, and avoid new loan " +
                        "applications for 3–6 months.", gap)
            );
        }

        // Pass — score impact based on how comfortably above minimum
        int margin = userScore - minRequired;
        if (margin >= 100) return RuleResult.pass(30);
        if (margin >= 50)  return RuleResult.pass(20);
        return RuleResult.pass(10);
    }
}

