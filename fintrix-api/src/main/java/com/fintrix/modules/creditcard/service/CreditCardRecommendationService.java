// ================================================================
// FILE 4: CreditCardRecommendationService + Impl + Controller
// ================================================================
package com.fintrix.modules.creditcard.service;

import com.fintrix.modules.creditcard.dto.CardRecommendationRequest;
import com.fintrix.modules.creditcard.dto.CardRecommendationResponse;

public interface CreditCardRecommendationService {
    CardRecommendationResponse getRecommendations(
            String userId, CardRecommendationRequest request);
}
