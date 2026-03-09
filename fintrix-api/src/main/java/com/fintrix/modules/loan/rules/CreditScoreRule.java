

// ================================================================
// FILE 2: CreditScoreRule.java
// ================================================================
package com.fintrix.modules.loan.rules;

import com.fintrix.modules.financialprofile.domain.FinancialProfile;
import com.fintrix.modules.loan.domain.Lender;
import com.fintrix.modules.loan.dto.LoanEligibilityRequest;
import org.springframework.stereotype.Component;

/**
 * CreditScoreRule
 *
 * Checks if user's CIBIL score meets lender's minimum requirement.
 *
 * Score impact logic:
 *   Score exactly at minimum        → 15 points (barely eligible)
 *   Score 50+ above minimum         → 25 points (comfortably eligible)
 *   Score 100+ above minimum        → 35 points (best rates likely)
 *
 * Real-world insight:
 *   Banks don't just check minimum score.
 *   A score of 750 vs 780 at HDFC → different interest rate.
 *   We model this with score impact bands.
 *
 * Hard reject:
 *   Score below lender minimum → 0 points, explain why.
 *   We also warn if score is unknown (null).
 */
@Component
public class CreditScoreRule implements LoanEligibilityRule {

    @Override
    public RuleResult evaluate(
            FinancialProfile profile,
            Lender lender,
            LoanEligibilityRequest request) {

        Integer userScore   = profile.getCreditScore();
        int     minRequired = lender.getMinCreditScore();

        // Unknown score — cannot assess, assume borderline
        if (userScore == null) {
            return RuleResult.partial(10,
                    "Credit score not provided — lender requires " + minRequired,
                    "Check your CIBIL score free at https://www.cibil.com " +
                    "to get accurate eligibility results.");
        }

        // Hard fail — below minimum
        if (userScore < minRequired) {
            int gap = minRequired - userScore;
            return RuleResult.fail(
                    String.format(
                        "Your credit score %d is below %s's minimum of %d",
                        userScore, lender.getName(), minRequired),
                    String.format(
                        "You need to improve your score by %d points. " +
                        "Pay all EMIs on time and reduce credit utilization " +
                        "below 30%%. This typically takes 3–6 months.", gap)
            );
        }

        // Pass — compute score impact based on margin
        int margin = userScore - minRequired;
        if (margin >= 100) return RuleResult.pass(35);
        if (margin >= 50)  return RuleResult.pass(25);
        return RuleResult.pass(15);
    }
}

