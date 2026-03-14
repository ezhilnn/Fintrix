package com.fintrix.modules.fraud.dto;

import com.fintrix.modules.fraud.domain.AlertSeverity;
import com.fintrix.modules.fraud.domain.EntityType;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class FraudCheckResponse {

    private String        entityName;
    private EntityType    entityType;

    // Clear overall verdict
    private Boolean       isSafe;               // true ONLY if in active registry + no flags
    private AlertSeverity severity;             // SAFE / UNVERIFIED / LOW / MEDIUM / HIGH / CRITICAL

    // Replaces the misleading "LOW RISK" badge with an accurate label
    // e.g. "✅ VERIFIED", "⚠️ UNVERIFIED", "🚩 CRITICAL RISK"
    private String        severityLabel;

    // Registry status — null means not checked / unknown
    private Boolean       isSebiRegistered;
    private Boolean       isRbiRegistered;
    private String        registrationNumber;   // shown if found in registry
    private String        regulatorName;        // "SEBI" / "RBI" / "IRDAI"

    // Detailed findings
    private List<String>  redFlags;
    private List<String>  safetyTips;

    // Plain-language one-liner for UI
    private String        verdict;

    // Link for user to independently verify
    private String        regulatorCheckUrl;
}