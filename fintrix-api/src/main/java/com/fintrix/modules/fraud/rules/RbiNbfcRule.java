
// ================================================================
// FILE 6: RbiNbfcRule.java
// ================================================================
package com.fintrix.modules.fraud.rules;

import com.fintrix.modules.fraud.domain.EntityType;
import com.fintrix.modules.fraud.dto.FraudCheckRequest;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * RbiNbfcRule
 *
 * RBI (Reserve Bank of India) regulates:
 *  - Banks
 *  - NBFCs (Non-Banking Financial Companies)
 *  - Microfinance institutions
 *  - Payment aggregators
 *
 * Red flags for lenders:
 *  1. Lender asks for upfront fee before disbursing loan
 *     → Illegal. RBI prohibits advance fee for loans.
 *  2. Lender claims to be RBI approved
 *     → RBI does not approve loans, only registers NBFCs.
 *  3. Instant loan apps with no physical address
 *     → High risk of illegal collection practices.
 *
 * Real-world note:
 *  In production, verify NBFC registration at:
 *  https://www.rbi.org.in/Scripts/bs_viewcontent.aspx?Id=2009
 *
 *  RBI Sachet portal for illegal deposit schemes:
 *  https://sachet.rbi.org.in
 */
@Component
public class RbiNbfcRule implements FraudRule {

    private static final Set<String> LOAN_FRAUD_KEYWORDS = Set.of(
            "advance fee", "processing fee advance",
            "registration fee", "insurance fee before loan",
            "rbi approved loan", "rbi certified",
            "instant loan no documents", "no cibil check loan",
            "loan guaranteed approval", "loan without kyc"
    );

    @Override
    public FraudRuleResult evaluate(FraudCheckRequest request) {

        String nameLower = request.getEntityName().toLowerCase();

        for (String keyword : LOAN_FRAUD_KEYWORDS) {
            if (nameLower.contains(keyword)) {
                return FraudRuleResult.flag(
                        "Entity name matches known loan fraud pattern: '" +
                        keyword + "'",
                        "RBI strictly prohibits collecting any fee before " +
                        "loan disbursement. Never pay any advance fee. " +
                        "Report to RBI Sachet: https://sachet.rbi.org.in",
                        4 // CRITICAL
                );
            }
        }

        // General lender warning
        if (request.getEntityType() == EntityType.LENDER) {
            return FraudRuleResult.flag(
                    "Verify this lender is RBI registered or a licensed NBFC.",
                    "Check NBFC list at rbi.org.in before taking any loan. " +
                    "Only deal with RBI-registered entities.",
                    1 // LOW — informational
            );
        }

        return FraudRuleResult.clean();
    }
}

