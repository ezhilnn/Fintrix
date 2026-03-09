
// ================================================================
// FILE 4: CardEmploymentRule.java
// Checks if user's employment type is accepted for the card
// ================================================================
package com.fintrix.modules.creditcard.rules;

import com.fintrix.modules.creditcard.domain.CreditCard;
import com.fintrix.modules.creditcard.dto.CardRecommendationRequest;
import com.fintrix.modules.financialprofile.domain.FinancialProfile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * CardEmploymentRule
 *
 * Some cards are restricted to salaried employees only.
 * Some accept self-employed with additional documentation.
 * Premium cards often require salaried + specific income proofs.
 *
 * Real-world example:
 *  HDFC Regalia → SALARIED, SELF_EMPLOYED
 *  SBI BPCL     → SALARIED, GOVERNMENT
 *  Kotak 811    → all employment types including STUDENT
 */
@Component
public class CardEmploymentRule implements CardEligibilityRule {

    @Override
    public RuleResult evaluate(
            FinancialProfile profile,
            CreditCard card,
            CardRecommendationRequest request) {

        if (profile.getEmploymentType() == null) {
            return RuleResult.partial(5,
                    "Employment type not set in your profile",
                    "Update your financial profile with " +
                    "your employment details for accurate recommendations.");
        }

        String allowedCsv = card.getAllowedEmploymentTypes();

        // Card accepts all employment types
        if (allowedCsv == null || allowedCsv.isBlank()) {
            return RuleResult.pass(15);
        }

        List<String> allowedTypes = Arrays.asList(allowedCsv.split(","));
        String userEmployment = profile.getEmploymentType().name();

        if (!allowedTypes.contains(userEmployment)) {
            return RuleResult.fail(
                    String.format(
                        "%s is not available for %s applicants",
                        card.getCardName(),
                        userEmployment.toLowerCase()
                                      .replace("_", " ")),
                    "Look for cards that accept your employment type. " +
                    "Many banks offer variants of their cards for " +
                    "self-employed and business owners."
            );
        }

        // Bonus for stable employment types
        if (userEmployment.equals("GOVERNMENT")
                || userEmployment.equals("PSU")) {
            return RuleResult.pass(20); // banks love government employees
        }

        return RuleResult.pass(15);
    }
}

