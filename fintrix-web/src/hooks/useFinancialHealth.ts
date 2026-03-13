// ================================================================
// useFinancialHealth.ts
//
// Hook for the financial health score section.
// Fetches the latest saved score on mount.
// Exposes recompute() for the "Refresh Score" button.
//
// GET  /api/v1/financial-health         → latest saved score
// POST /api/v1/financial-health/compute → fresh computation
// ================================================================

import { useState, useEffect, useCallback } from 'react';
import FinancialHealthService from '../services/financialHealth.service';
import type { FinancialHealthResponse } from '../types/api.types';
import type { ApiError } from '../types/api.types';

const useFinancialHealth = () => {
  const [healthScore,   setHealthScore]   = useState<FinancialHealthResponse | null>(null);
  const [isFetching,    setIsFetching]    = useState(false);
  const [isRecomputing, setIsRecomputing] = useState(false);
  const [error,         setError]         = useState<string | null>(null);

  // ── Fetch latest saved score ─────────────────────────────
  const fetchScore = useCallback(async () => {
    setIsFetching(true);
    setError(null);
    try {
      const score = await FinancialHealthService.getLatestScore();
      setHealthScore(score);
    } catch (err) {
      const apiErr = err as ApiError;
      // 404 = no score computed yet — not a real error
      if (apiErr.status !== 404) {
        setError(apiErr.message);
      }
    } finally {
      setIsFetching(false);
    }
  }, []);

  // ── Trigger fresh computation ─────────────────────────────
  const recompute = useCallback(async () => {
    setIsRecomputing(true);
    setError(null);
    try {
      const fresh = await FinancialHealthService.computeScore();
      setHealthScore(fresh);
    } catch (err) {
      const apiErr = err as ApiError;
      setError(apiErr.message);
    } finally {
      setIsRecomputing(false);
    }
  }, []);

  useEffect(() => {
    fetchScore();
  }, [fetchScore]);

  return {
    healthScore,
    isFetching,
    isRecomputing,
    error,
    hasScore:   !!healthScore,
    recompute,
    refetch:    fetchScore,
  };
};

export default useFinancialHealth;