// ================================================================
// dashboard.service.ts
//
// Mirrors DashboardController.java (BFF) — /api/v1/bff
//
//   GET /api/v1/bff/dashboard → getDashboard()
//
// BFF pattern — returns everything the dashboard needs in one call:
//   userProfile              → UserProfileResponse
//   financialProfile         → FinancialProfileResponse
//   healthScore              → FinancialHealthResponse
//   isProfileComplete        → Boolean
//   isFinancialProfileComplete → Boolean
//   nextActionPrompt         → String (what to do next)
//
// This avoids 3 waterfall requests on dashboard load.
// ================================================================

import { get } from './api.client';
import { API } from '../utils/constants';
import type { DashboardResponse } from '../types/api.types';

const DashboardService = {

  // ── GET /api/v1/bff/dashboard ────────────────────────────────
  getDashboard(): Promise<DashboardResponse> {
    return get<DashboardResponse>(API.DASHBOARD);
  },
};

export default DashboardService;