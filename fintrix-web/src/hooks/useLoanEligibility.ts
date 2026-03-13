// ================================================================
// useLoanEligibility.ts
//
// Hook for LoanEligibilityPage.
//
// POST /api/v1/loans/check-eligibility
//
// Request (3 required fields + optional purpose):
//   loanType, requestedAmount, tenureMonths, purpose?
//
// Response splits lenders into two lists:
//   eligibleLenders   — approvalProbability >= 60
//   ineligibleLenders — approvalProbability < 60
//
// Each LenderResult has:
//   approvalProbability, estimatedEmi, totalInterestPayable,
//   failureReasons[], improvementTips[]
// ================================================================

import { useState, useCallback } from 'react';
import LoanService from '../services/loan.service';
import type { LoanEligibilityRequest, LoanEligibilityResponse } from '../types/loan.types';
import type { ApiError } from '../types/api.types';

const useLoanEligibility = () => {
  const [result,     setResult]     = useState<LoanEligibilityResponse | null>(null);
  const [isChecking, setIsChecking] = useState(false);
  const [error,      setError]      = useState<string | null>(null);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string> | null>(null);

  // ── checkEligibility ────────────────────────────────────────
  const checkEligibility = useCallback(async (
    request: LoanEligibilityRequest,
    onSuccess?: (result: LoanEligibilityResponse) => void,
  ) => {
    setIsChecking(true);
    setError(null);
    setFieldErrors(null);
    setResult(null);

    try {
      const response = await LoanService.checkEligibility(request);
      setResult(response);
      onSuccess?.(response);
    } catch (err) {
      const apiErr = err as ApiError;
      setError(apiErr.message);
      setFieldErrors(apiErr.errors ?? null);
    } finally {
      setIsChecking(false);
    }
  }, []);

  const reset = useCallback(() => {
    setResult(null);
    setError(null);
    setFieldErrors(null);
  }, []);

  return {
    result,
    isChecking,
    error,
    fieldErrors,
    hasResult:  !!result,
    // Derived counts for UI
    eligibleCount:   result?.eligibleLenders.length   ?? 0,
    ineligibleCount: result?.ineligibleLenders.length ?? 0,
    checkEligibility,
    reset,
  };
};

export default useLoanEligibility;