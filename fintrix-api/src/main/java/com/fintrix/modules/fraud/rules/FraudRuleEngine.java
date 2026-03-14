package com.fintrix.modules.fraud.rules;

import com.fintrix.modules.fraud.domain.AlertSeverity;
import com.fintrix.modules.fraud.domain.FraudKeyword;
import com.fintrix.modules.fraud.domain.RegulatedEntity;
import com.fintrix.modules.fraud.dto.FraudCheckRequest;
import com.fintrix.modules.fraud.dto.FraudCheckResponse;
import com.fintrix.modules.fraud.repository.FraudKeywordRepository;
import com.fintrix.modules.fraud.repository.RegulatedEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * FraudRuleEngine — corrected severity logic
 *
 * ── Decision tree ─────────────────────────────────────────────────
 *
 *  1. Keyword scan (DB fraud_keywords table)
 *     Any CRITICAL keyword matched  → severity = CRITICAL, isSafe = false
 *     Any HIGH keyword matched      → severity = HIGH,     isSafe = false
 *     Any MEDIUM keyword matched    → severity = MEDIUM,   isSafe = false
 *     Any LOW keyword matched       → severity = LOW,      isSafe = false
 *
 *  2. Registry lookup (DB regulated_entities table)
 *     Found + ACTIVE                → severity = SAFE,       isSafe = true
 *                                     (overridden if keyword also matched)
 *     Found + CANCELLED/SUSPENDED   → severity = HIGH,       isSafe = false
 *     NOT FOUND at all              → severity = UNVERIFIED, isSafe = false
 *
 *  3. Keyword match beats registry:
 *     If entity IS in registry but name also matches a CRITICAL keyword
 *     → severity = CRITICAL, isSafe = false
 *     (Protects against registered companies running illegal sub-schemes)
 *
 * ── Severity label mapping ────────────────────────────────────────
 *  SAFE        → "✅ VERIFIED"
 *  UNVERIFIED  → "⚠️ UNVERIFIED"
 *  LOW         → "🔵 LOW CONCERN"
 *  MEDIUM      → "🟡 MEDIUM RISK"
 *  HIGH        → "🔴 HIGH RISK"
 *  CRITICAL    → "⛔ CRITICAL RISK"
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FraudRuleEngine {

    private final FraudKeywordRepository    keywordRepository;
    private final RegulatedEntityRepository entityRepository;

    public FraudCheckResponse evaluate(FraudCheckRequest request) {

        log.info("Fraud check: '{}' type: {}",
                request.getEntityName(), request.getEntityType());

        List<String> redFlags    = new ArrayList<>();
        List<String> safetyTips = new ArrayList<>();

        // ── Phase 1: Keyword scan ──────────────────────────────
        List<FraudKeyword> matched =
                keywordRepository.findMatchingKeywords(
                        request.getEntityName());

        int maxKeywordSeverity = 0;
        for (FraudKeyword kw : matched) {
            int score = severityScore(kw.getRiskLevel());
            maxKeywordSeverity = Math.max(maxKeywordSeverity, score);
            redFlags.add(String.format(
                    "🚩 Name matches fraud pattern '%s' [%s — %s]: %s",
                    kw.getKeyword(),
                    kw.getFraudType(),
                    kw.getRiskLevel(),
                    kw.getDescription()));
        }

        // ── Phase 2: Registry lookup ───────────────────────────
        List<RegulatedEntity> entities =
                entityRepository.searchByName(request.getEntityName());

        Boolean isSebiRegistered  = null;
        Boolean isRbiRegistered   = null;
        String  registrationNumber = null;
        String  regulatorName      = null;
        boolean foundActiveInReg   = false;
        boolean foundCancelledInReg = false;

        for (RegulatedEntity entity : entities) {
            boolean active = "ACTIVE".equalsIgnoreCase(
                    entity.getLicenseStatus());

            if (active) {
                foundActiveInReg   = true;
                registrationNumber = entity.getRegistrationNumber();
                regulatorName      = entity.getRegulator();
            } else {
                foundCancelledInReg = true;
                redFlags.add(String.format(
                        "⛔ Found in %s registry with status: %s " +
                        "(Reg No: %s) — licence is NOT active",
                        entity.getRegulator(),
                        entity.getLicenseStatus(),
                        entity.getRegistrationNumber()));
            }

            if ("SEBI".equals(entity.getRegulator())) isSebiRegistered = active;
            if ("RBI".equals(entity.getRegulator()))  isRbiRegistered  = active;
        }

        // ── Phase 3: Determine final severity ─────────────────
        AlertSeverity severity;
        boolean       isSafe;

        if (maxKeywordSeverity >= 4) {
            // CRITICAL keyword hit — overrides everything
            severity = AlertSeverity.CRITICAL;
            isSafe   = false;
        } else if (maxKeywordSeverity == 3 || foundCancelledInReg) {
            severity = AlertSeverity.HIGH;
            isSafe   = false;
        } else if (maxKeywordSeverity == 2) {
            severity = AlertSeverity.MEDIUM;
            isSafe   = false;
        } else if (maxKeywordSeverity == 1) {
            severity = AlertSeverity.LOW;
            isSafe   = false;
        } else if (foundActiveInReg) {
            // No bad keywords + found active in registry → SAFE
            severity = AlertSeverity.SAFE;
            isSafe   = true;
            safetyTips.add("✅ Found in " + regulatorName +
                    " registry as ACTIVE (Reg: " + registrationNumber +
                    "). Always verify on official regulator website.");
        } else {
            // No keywords, not in registry → UNVERIFIED (not LOW RISK!)
            severity = AlertSeverity.UNVERIFIED;
            isSafe   = false;
            redFlags.add("Entity not found in Fintrix regulated database " +
                    "(" + entities.size() + " records searched).");
            safetyTips.add("This entity could not be verified. " +
                    "Check the official regulator website before investing or borrowing.");
        }

        // ── Build safety tips ──────────────────────────────────
        String regulatorUrl = buildRegulatorUrl(request);
        safetyTips.add("Verify independently at: " + regulatorUrl);

        if (severity == AlertSeverity.CRITICAL
                || severity == AlertSeverity.HIGH) {
            safetyTips.add("Do NOT pay any advance fee. " +
                    "Report suspicious entities at https://sachet.rbi.org.in");
        }

        return FraudCheckResponse.builder()
                .entityName(request.getEntityName())
                .entityType(request.getEntityType())
                .isSafe(isSafe)
                .severity(severity)
                .severityLabel(buildSeverityLabel(severity))
                .isSebiRegistered(isSebiRegistered)
                .isRbiRegistered(isRbiRegistered)
                .registrationNumber(registrationNumber)
                .regulatorName(regulatorName)
                .redFlags(redFlags)
                .safetyTips(safetyTips)
                .verdict(buildVerdict(severity, request.getEntityName(),
                        foundActiveInReg, registrationNumber))
                .regulatorCheckUrl(regulatorUrl)
                .build();
    }

    // ── Score mapping ─────────────────────────────────────────────
    private int severityScore(String riskLevel) {
        return switch (riskLevel.toUpperCase()) {
            case "CRITICAL" -> 4;
            case "HIGH"     -> 3;
            case "MEDIUM"   -> 2;
            case "LOW"      -> 1;
            default         -> 0;
        };
    }

    // ── Human-readable badge label ────────────────────────────────
    private String buildSeverityLabel(AlertSeverity severity) {
        return switch (severity) {
            case SAFE        -> "✅ VERIFIED";
            case UNVERIFIED  -> "⚠️ UNVERIFIED";
            case LOW         -> "🔵 LOW CONCERN";
            case MEDIUM      -> "🟡 MEDIUM RISK";
            case HIGH        -> "🔴 HIGH RISK";
            case CRITICAL    -> "⛔ CRITICAL RISK";
        };
    }

    // ── Verdict one-liner ─────────────────────────────────────────
    private String buildVerdict(AlertSeverity severity, String name,
                                 boolean foundActive, String regNo) {
        return switch (severity) {
            case SAFE ->
                "✅ '" + name + "' is registered and ACTIVE" +
                (regNo != null ? " (Reg: " + regNo + ")" : "") +
                ". Always verify directly on official regulator website.";
            case UNVERIFIED ->
                "⚠️ '" + name + "' was NOT found in any regulator registry. " +
                "This does not confirm fraud, but you must verify independently " +
                "before investing or transacting.";
            case LOW ->
                "🔵 '" + name + "' has a minor concern. Proceed with caution " +
                "and verify independently.";
            case MEDIUM ->
                "🟡 '" + name + "' shows suspicious signals. " +
                "Do not invest until independently verified.";
            case HIGH ->
                "🔴 '" + name + "' is HIGH RISK. Either the licence is inactive " +
                "or the name matches high-risk fraud patterns. Do not proceed.";
            case CRITICAL ->
                "⛔ '" + name + "' matches KNOWN FRAUD PATTERNS. " +
                "Do NOT invest or pay any money. Report to sachet.rbi.org.in";
        };
    }

    // ── Regulator URL based on entity type ───────────────────────
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