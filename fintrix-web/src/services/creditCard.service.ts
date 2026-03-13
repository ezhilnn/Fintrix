// ================================================================
// creditCard.service.ts
//
// Mirrors CreditCardController.java — /api/v1/credit-cards
//
//   POST /api/v1/credit-cards/recommendations → getRecommendations()
//
// Important from controller:
//   @RequestBody(required = false) — request body is OPTIONAL.
//   If null, backend uses an empty CardRecommendationRequest()
//   and returns best overall match based on stored financial profile.
//
// Response structure (CardRecommendationResponse):
//   recommendedCards   → eligible + best preference match
//   otherEligibleCards → eligible but not preference match
//   futureCards        → not yet eligible — shows what to aim for
//   overallTip         → general advice string
//   multipleCardWarning → warning if user already has many cards
// ================================================================

import { post } from './api.client';
import { API } from '../utils/constants';
import type {
  CardRecommendationRequest,
  CardRecommendationResponse,
} from '../types/api.types';

const CreditCardService = {

  // ── POST /api/v1/credit-cards/recommendations ────────────────
  // Pass null or empty object to get default recommendations.
  getRecommendations(
    request?: CardRecommendationRequest,
  ): Promise<CardRecommendationResponse> {
    return post<CardRecommendationResponse>(
      API.CARD_RECOMMENDATIONS,
      request ?? {},
    );
  },
};

export default CreditCardService;