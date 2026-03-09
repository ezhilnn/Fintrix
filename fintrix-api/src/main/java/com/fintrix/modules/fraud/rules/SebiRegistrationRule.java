
// ================================================================
// FILE 5: SebiRegistrationRule.java
// ================================================================
package com.fintrix.modules.fraud.rules;

import com.fintrix.modules.fraud.domain.EntityType;
import com.fintrix.modules.fraud.dto.FraudCheckRequest;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * SebiRegistrationRule
 *
 * SEBI (Securities and Exchange Board of India) regulates:
 *  - Stock brokers
 *  - Mutual fund distributors
 *  - Investment advisors
 *  - Portfolio managers
 *  - Research analysts
 *
 * Red flags we check:
 *  1. Entity claims to offer "guaranteed returns" on investments
 *     → Illegal in India. No investment can guarantee returns.
 *  2. Entity name contains known scam keywords
 *  3. Entity type is investment but claims no SEBI registration needed
 *
 * Real-world note:
 *  In production, integrate with SEBI's public API:
 *  https://www.sebi.gov.in/sebiweb/other/OtherAction.do?doRecognisedFca=yes
 *
 *  For MVP: rule-based keyword detection + user education.
 *
 * Known scam patterns in India (2023-2024):
 *  - "Guaranteed 30% monthly returns"
 *  - "SEBI approved" (SEBI never approves schemes, only registers entities)
 *  - WhatsApp/Telegram investment groups
 *  - "Double your money in 90 days"
 */
@Component
public class SebiRegistrationRule implements FraudRule {

    // Keywords strongly associated with illegal schemes
    private static final Set<String> HIGH_RISK_KEYWORDS = Set.of(
            "guaranteed return", "guaranteed profit", "risk free",
            "double your money", "triple your money", "100% returns",
            "whatsapp trading", "telegram trading", "ponzi",
            "mlm investment", "multi level", "chain scheme",
            "assured return", "fixed return"
    );

    // Entity types that MUST be SEBI registered
    private static final Set<EntityType> SEBI_REGULATED_TYPES = Set.of(
            EntityType.INVESTMENT_SCHEME,
            EntityType.BROKER
    );

    @Override
    public FraudRuleResult evaluate(FraudCheckRequest request) {

        String nameLower = request.getEntityName().toLowerCase();

        // Check for high-risk keywords in entity name
        for (String keyword : HIGH_RISK_KEYWORDS) {
            if (nameLower.contains(keyword)) {
                return FraudRuleResult.flag(
                        "Entity name contains high-risk phrase: '" +
                        keyword + "' — associated with illegal investment schemes",
                        "No legitimate investment can guarantee fixed returns. " +
                        "This is prohibited by SEBI. Verify on sebi.gov.in " +
                        "before investing a single rupee.",
                        4 // CRITICAL
                );
            }
        }

        // Investment/broker entities — warn about verification
        if (SEBI_REGULATED_TYPES.contains(request.getEntityType())) {
            return FraudRuleResult.flag(
                    "Investment entities must be SEBI registered. " +
                    "Always verify registration before investing.",
                    "Check SEBI registration at: " +
                    "https://www.sebi.gov.in/sebiweb/other/OtherAction.do" +
                    "?doRecognisedFca=yes",
                    1 // LOW — informational, not confirmed fraud
            );
        }

        return FraudRuleResult.clean();
    }
}

