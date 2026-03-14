package com.fintrix.modules.creditcard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CardRecommendationResponse {

    private List<CardResult> recommendedCards;
    private List<CardResult> otherEligibleCards;
    private List<CardResult> futureCards;
    private String overallTip;
    private String multipleCardWarning;

}