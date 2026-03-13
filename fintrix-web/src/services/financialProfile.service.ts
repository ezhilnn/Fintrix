// ================================================================
// financialProfile.service.ts
//
// Mirrors FinancialProfileController.java — /api/v1/financial-profile
//
//   POST  /api/v1/financial-profile  → createProfile()   → HTTP 201
//   GET   /api/v1/financial-profile  → getProfile()      → HTTP 200
//   PUT   /api/v1/financial-profile  → updateProfile()   → HTTP 200
//
// Important design note from controller:
//   No userId in URL path — security enforced via JWT token.
//   User can ONLY read/write their own profile.
//
// Response includes backend-computed fields:
//   foir, monthlySavings, creditScoreRange,
//   financialHealthScore, riskLevel, isComplete
// ================================================================

import { get, post, put } from './api.client';
import { API } from '../utils/constants';
import type {
  FinancialProfileRequest,
  FinancialProfileResponse,
} from '../types/financialProfile.types';

const FinancialProfileService = {

  // ── POST /api/v1/financial-profile ──────────────────────────
  // First-time setup after user completes basic profile.
  // Backend returns HTTP 201 Created.
  createProfile(
    request: FinancialProfileRequest,
  ): Promise<FinancialProfileResponse> {
    return post<FinancialProfileResponse>(API.FINANCIAL_PROFILE, request);
  },

  // ── GET /api/v1/financial-profile ───────────────────────────
  // Fetches current profile with all computed fields.
  // Called on dashboard load and financial-profile page.
  getProfile(): Promise<FinancialProfileResponse> {
    return get<FinancialProfileResponse>(API.FINANCIAL_PROFILE);
  },

  // ── PUT /api/v1/financial-profile ───────────────────────────
  // Update when income, EMI or credit info changes.
  // Backend recomputes foir, riskLevel and clears Redis cache.
  updateProfile(
    request: FinancialProfileRequest,
  ): Promise<FinancialProfileResponse> {
    return put<FinancialProfileResponse>(API.FINANCIAL_PROFILE, request);
  },
};

export default FinancialProfileService;