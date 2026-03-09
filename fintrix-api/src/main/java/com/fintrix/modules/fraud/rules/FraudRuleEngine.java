
// ================================================================
// FILE 7: FraudRuleEngine.java
// ================================================================
package com.fintrix.modules.fraud.rules;

import com.fintrix.modules.fraud.domain.AlertSeverity;
import com.fintrix.modules.fraud.dto.FraudCheckRequest;
import com.fintrix.modules.fraud.dto.FraudCheckResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * FraudRuleEngine
 *
 * Runs all fraud rules and produces a FraudCheckResponse.
 *
 * Severity scoring:
 *  0       → SAFE
 *  1       → LOW  (verify but likely ok)
 *  2       → MEDIUM (strong suspicion)
 *  3       → HIGH (do not proceed)
 *  4       → CRITICAL (known scam pattern)
 *
 * Max severity wins — if any rule returns CRITICAL → CRITICAL.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FraudRuleEngine {

    private final SebiRegistrationRule sebiRule;
    private final RbiNbfcRule          rbiRule;

    public FraudCheckResponse evaluate(FraudCheckRequest request) {

        log.info("Fraud check for: {} type: {}",
                request.getEntityName(), request.getEntityType());

        List<String> redFlags    = new ArrayList<>();
        List<String> safetyTips = new ArrayList<>();
        int          maxSeverity = 0;

        // Run all rules
        for (FraudRule rule : List.of(sebiRule, rbiRule)) {
            FraudRule.FraudRuleResult result = rule.evaluate(request);
            if (result.flagged()) {
                if (result.reason() != null) redFlags.add(result.reason());
                if (result.tip()    != null) safetyTips.add(result.tip());
                maxSeverity = Math.max(maxSeverity, result.severityScore());
            }
        }

        AlertSeverity severity  = mapSeverity(maxSeverity);
        boolean       isSafe    = maxSeverity <= 1 && redFlags.stream()
                .noneMatch(r -> r.contains("CRITICAL") || r.contains("illegal"));

        // Always add general safety tips
        safetyTips.add("Always verify financial entities on " +
                "official regulator websites before investing or borrowing.");

        String regulatorUrl = buildRegulatorUrl(request);

        return FraudCheckResponse.builder()
                .entityName(request.getEntityName())
                .entityType(request.getEntityType())
                .isSafe(isSafe)
                .severity(severity)
                .isSebiRegistered(null)   // null = not verified yet
                .isRbiRegistered(null)
                .redFlags(redFlags)
                .safetyTips(safetyTips)
                .verdict(buildVerdict(isSafe, severity,
                        request.getEntityName()))
                .regulatorCheckUrl(regulatorUrl)
                .build();
    }

    private AlertSeverity mapSeverity(int score) {
        return switch (score) {
            case 0  -> AlertSeverity.LOW;
            case 1  -> AlertSeverity.LOW;
            case 2  -> AlertSeverity.MEDIUM;
            case 3  -> AlertSeverity.HIGH;
            default -> AlertSeverity.CRITICAL;
        };
    }

    private String buildVerdict(boolean isSafe, AlertSeverity severity,
                                 String name) {
        return switch (severity) {
            case CRITICAL -> "⛔ HIGH RISK: '" + name + "' matches known " +
                             "fraud patterns. Do NOT invest or pay any money.";
            case HIGH     -> "⚠️ SUSPICIOUS: Proceed with extreme caution. " +
                             "Verify independently before any transaction.";
            case MEDIUM   -> "🔶 UNVERIFIED: Could not confirm legitimacy. " +
                             "Verify on regulator website before proceeding.";
            default       -> "ℹ️ Always verify financial entities independently " +
                             "regardless of how they present themselves.";
        };
    }

    private String buildRegulatorUrl(FraudCheckRequest request) {
        return switch (request.getEntityType()) {
            case INVESTMENT_SCHEME, BROKER ->
                "https://www.sebi.gov.in/sebiweb/other/OtherAction.do?doRecognisedFca=yes";
            case LENDER ->
                "https://www.rbi.org.in/Scripts/bs_viewcontent.aspx?Id=2009";
            case INSURANCE_COMPANY ->
                "https://www.irdai.gov.in/ADMINCMS/cms/frmGeneral_Layout.aspx?page=PageNo246";
            default ->
                "https://sachet.rbi.org.in";
        };
    }
}