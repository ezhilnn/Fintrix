// ================================================================
// loan.service.ts
//
// Mirrors LoanController.java — /api/v1/loans
//
//   POST /api/v1/loans/check-eligibility → checkEligibility()
//
// Request sends only 3 required fields:
//   loanType, requestedAmount, tenureMonths (+ optional purpose)
//
// Backend automatically reads userId from JWT and joins with
// FinancialProfile to compute:
//   - userFoir, userCreditScore, userCreditScoreRange
//   - eligibleLenders   (approvalProbability >= 60)
//   - ineligibleLenders (approvalProbability < 60)
//   - estimatedEmi, totalInterestPayable per lender
//   - failureReasons and improvementTips per lender
// ================================================================

import { post } from './api.client';
import { API } from '../utils/constants';
import type {
  LoanEligibilityRequest,
  LoanEligibilityResponse,
} from '../types/loan.types';

const LoanService = {

  // ── POST /api/v1/loans/check-eligibility ────────────────────
  checkEligibility(
    request: LoanEligibilityRequest,
  ): Promise<LoanEligibilityResponse> {
    return post<LoanEligibilityResponse>(API.LOAN_ELIGIBILITY, request);
  },
};

export default LoanService;