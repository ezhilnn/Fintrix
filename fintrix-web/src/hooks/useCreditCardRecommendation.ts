// ================================================================
// useCreditCardRecommendation.ts
//
// Hook for CreditCardPage.
//
// POST /api/v1/credit-cards/recommendations
//
// Request is optional — if empty, backend uses stored
// financial profile preferences to recommend best cards.
//
// Optional filters:
//   preferredRewardType  — "CASHBACK" | "TRAVEL" | "FUEL"
//   topSpendingCategory  — "FOOD" | "SHOPPING" | "TRAVEL"
//   preferNoAnnualFee    — boolean
//
// Response has 3 lists:
//   recommendedCards   → eligible + best preference match (show first)
//   otherEligibleCards → eligible but not top preference match
//   futureCards        → not yet eligible — motivational goal cards
//
// Auto-fetches on mount with no filters (default recommendations).
// Re-fetch with filters when user adjusts preferences.
// ================================================================

import { useState, useEffect, useCallback } from 'react';
import CreditCardService from '../services/creditCard.service';
import type { CardRecommendationRequest, CardRecommendationResponse } from '../types/api.types';
import type { ApiError } from '../types/api.types';

const useCreditCardRecommendation = () => {
  const [result,      setResult]      = useState<CardRecommendationResponse | null>(null);
  const [isFetching,  setIsFetching]  = useState(false);
  const [error,       setError]       = useState<string | null>(null);

  // ── fetch recommendations ────────────────────────────────────
  const fetchRecommendations = useCallback(async (
    filters?: CardRecommendationRequest,
  ) => {
    setIsFetching(true);
    setError(null);
    try {
      const response = await CreditCardService.getRecommendations(filters);
      setResult(response);
    } catch (err) {
      const apiErr = err as ApiError;
      setError(apiErr.message);
    } finally {
      setIsFetching(false);
    }
  }, []);

  // Auto-fetch default recommendations on mount
  useEffect(() => {
    fetchRecommendations();
  }, [fetchRecommendations]);

  return {
    result,
    isFetching,
    error,
    hasResult:          !!result,
    recommendedCount:   result?.recommendedCards.length   ?? 0,
    otherCount:         result?.otherEligibleCards.length ?? 0,
    futureCount:        result?.futureCards.length        ?? 0,
    overallTip:         result?.overallTip,
    multipleCardWarning: result?.multipleCardWarning,
    // Re-fetch with new filters when user changes preferences
    applyFilters: fetchRecommendations,
    refetch:      () => fetchRecommendations(),
  };
};

export default useCreditCardRecommendation;