// ================================================================
// FILE 2: CardRecommendationRequest.java + CardRecommendationResponse
// ================================================================
package com.fintrix.modules.creditcard.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * CardRecommendationRequest
 *
 * Optional filters — user can specify what they want.
 * If null, engine recommends best overall match.
 */
@Getter
@Setter
public class CardRecommendationRequest {
    private String preferredRewardType;   // CASHBACK / TRAVEL / FUEL
    private String topSpendingCategory;   // FOOD / SHOPPING / TRAVEL
    private Boolean preferNoAnnualFee;    // true = lifetime free only
}
