// ────────────────────────────────────────────────────────────────
package com.fintrix.modules.creditcard.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CardRecommendationResponse {
    private List<CardResult> recommendedCards;   // eligible, best match first
    private List<CardResult> otherEligibleCards; // eligible but not top match
    private List<CardResult> futureCards;        // not yet eligible — shows goal
    private String           overallTip;
    private String           multipleCardWarning;
}
