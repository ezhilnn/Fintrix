package com.fintrix.modules.fraud.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class KeywordScanResponse {

    private String       overallRisk;         // SAFE / LOW / MEDIUM / HIGH / CRITICAL
    private Boolean      isSafe;
    private Integer      totalMatchesFound;
    private String       verdict;
    private String       contentTypeLabel;     // "WhatsApp Message", "SMS" etc.

    private List<KeywordMatch> matches;

    private List<String> safetyActions;
    private String       reportUrl;
    private String       scannedTextPreview;

    @Getter
    @Builder
    public static class KeywordMatch {
        private String keyword;
        private String riskLevel;
        private String fraudType;
        private String explanation;
        private String matchedPhrase;
        private String matchType;    // "EXACT" or "FUZZY" — shown in UI
    }
}