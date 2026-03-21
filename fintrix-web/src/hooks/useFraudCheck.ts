// ================================================================
// useFraudCheck.ts
//
// Hook for FraudCheckPage.
//
// POST /api/v1/fraud/check
//   Request:  { entityName: string, entityType: EntityType }
//   Response: { isSafe, severity, isSebiRegistered,
//               isRbiRegistered, redFlags[], safetyTips[],
//               verdict, regulatorCheckUrl }
//
// GET /api/v1/fraud/my-alerts
//   Returns list of all past fraud checks by this user.
//   Loaded on page mount to show check history.
// ================================================================

import { useState, useEffect, useCallback } from 'react';
import FraudService from '../services/fraud.service';
import type { FraudCheckRequest, FraudCheckResponse } from '../types/api.types';
import type { ApiError } from '../types/api.types';

const useFraudCheck = () => {
  const [result,       setResult]       = useState<FraudCheckResponse | null>(null);
  const [history,      setHistory]      = useState<FraudCheckResponse[]>([]);
  const [isChecking,   setIsChecking]   = useState(false);
  const [isFetching,   setIsFetching]   = useState(false);
  const [checkError,   setCheckError]   = useState<string | null>(null);
  const [fieldErrors,  setFieldErrors]  = useState<Record<string, string> | null>(null);

  // ── checkEntity ─────────────────────────────────────────────
  const checkEntity = useCallback(async (
    request: FraudCheckRequest,
    onSuccess?: (result: FraudCheckResponse) => void,
  ) => {
    setIsChecking(true);
    setCheckError(null);
    setFieldErrors(null);
    setResult(null);

    try {
      const response = await FraudService.checkEntity(request);
      setResult(response);
      // Prepend to history for immediate UI update without re-fetch
      setHistory(prev => [response, ...prev]);
      onSuccess?.(response);
    } catch (err) {
      const apiErr = err as ApiError;
      setCheckError(apiErr.message);
      setFieldErrors(apiErr.errors ?? null);
    } finally {
      setIsChecking(false);
    }
  }, []);

  // ── fetchHistory ─────────────────────────────────────────────
  const fetchHistory = useCallback(async () => {
    setIsFetching(true);
    try {
      const alerts = await FraudService.getMyAlerts();
      setHistory(alerts);
    } catch {
      // History is non-critical — fail silently
    } finally {
      setIsFetching(false);
    }
  }, []);

  // Load history on mount — only if not already fetched
  useEffect(() => {
    if (history.length === 0) {
      fetchHistory();
    }
  }, [fetchHistory, history.length]);

  const resetResult = useCallback(() => {
    setResult(null);
    setCheckError(null);
    setFieldErrors(null);
  }, []);

  return {
    result,
    history,
    isChecking,
    isFetching,
    checkError,
    fieldErrors,
    hasResult:    !!result,
    historyCount: history.length,
    checkEntity,
    resetResult,
    refetchHistory: fetchHistory,
  };
};

export default useFraudCheck;