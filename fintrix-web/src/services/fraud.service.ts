// ================================================================
// fraud.service.ts
//
// Mirrors FraudCheckController.java — /api/v1/fraud
//
//   POST /api/v1/fraud/check      → checkEntity()
//   GET  /api/v1/fraud/my-alerts  → getMyAlerts()
//
// checkEntity() request fields:
//   entityName  — @NotBlank — e.g. "ABC Investment Scheme"
//   entityType  — @NotNull  — from EntityType enum:
//     INVESTMENT_SCHEME | LENDER | BROKER | INSURANCE_COMPANY
//     CRYPTOCURRENCY_PLATFORM | CHIT_FUND | OTHER
//
// checkEntity() response fields:
//   isSafe           → overall verdict boolean
//   severity         → LOW | MEDIUM | HIGH | CRITICAL
//   isSebiRegistered → checked against SEBI data
//   isRbiRegistered  → checked against RBI data
//   redFlags         → list of specific warning strings
//   safetyTips       → what the user should do
//   verdict          → plain language summary
//   regulatorCheckUrl → link so user can verify themselves
// ================================================================

import { get, post } from './api.client';
import { API } from '../utils/constants';
import type {
  FraudCheckRequest,
  FraudCheckResponse,
} from '../types/api.types';

const FraudService = {

  // ── POST /api/v1/fraud/check ─────────────────────────────────
  checkEntity(request: FraudCheckRequest): Promise<FraudCheckResponse> {
    return post<FraudCheckResponse>(API.FRAUD_CHECK, request);
  },

  // ── GET /api/v1/fraud/my-alerts ──────────────────────────────
  // Returns all past fraud checks made by the current user.
  getMyAlerts(): Promise<FraudCheckResponse[]> {
    return get<FraudCheckResponse[]>(API.FRAUD_ALERTS);
  },
};

export default FraudService;