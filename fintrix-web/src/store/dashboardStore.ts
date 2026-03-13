// ================================================================
// dashboardStore.ts
//
// Powers the main dashboard — one BFF call gets everything.
//
// GET /api/v1/bff/dashboard returns DashboardResponse:
//   userProfile              → UserProfileResponse
//   financialProfile         → FinancialProfileResponse
//   healthScore              → FinancialHealthResponse
//   isProfileComplete        → boolean
//   isFinancialProfileComplete → boolean
//   nextActionPrompt         → string
//
// Also exposes recomputeScore() for the "Refresh Score" button
// that calls POST /api/v1/financial-health/compute directly.
// ================================================================

import { create } from 'zustand';
import DashboardService       from '../services/dashboard.service';
import FinancialHealthService from '../services/financialHealth.service';
import type { DashboardResponse, FinancialHealthResponse } from '../types/api.types';
import type { ApiError } from '../types/api.types';

interface DashboardState {
  // ── State ──────────────────────────────────────────────────
  dashboard:        DashboardResponse | null;
  isFetching:       boolean;
  isRecomputing:    boolean;    // for "Refresh Score" button spinner
  fetchError:       string | null;
  recomputeError:   string | null;

  // ── Actions ────────────────────────────────────────────────

  /** GET /api/v1/bff/dashboard — loads everything in one call */
  fetchDashboard: () => Promise<void>;

  /**
   * POST /api/v1/financial-health/compute
   * Triggers fresh score computation and patches healthScore
   * in the existing dashboard state (no full re-fetch needed).
   */
  recomputeScore: () => Promise<void>;

  clearErrors: () => void;
}

const useDashboardStore = create<DashboardState>((set, get) => ({
  dashboard:      null,
  isFetching:     false,
  isRecomputing:  false,
  fetchError:     null,
  recomputeError: null,

  // ── fetchDashboard ─────────────────────────────────────────
  fetchDashboard: async () => {
    set({ isFetching: true, fetchError: null });
    try {
      const dashboard = await DashboardService.getDashboard();
      set({ dashboard, isFetching: false });
    } catch (err) {
      const apiErr = err as ApiError;
      set({ fetchError: apiErr.message, isFetching: false });
    }
  },

  // ── recomputeScore ─────────────────────────────────────────
  // Only updates the healthScore slice — avoids full re-fetch
  recomputeScore: async () => {
    set({ isRecomputing: true, recomputeError: null });
    try {
      const fresh: FinancialHealthResponse =
        await FinancialHealthService.computeScore();

      const existing = get().dashboard;
      if (existing) {
        set({
          dashboard:     { ...existing, healthScore: fresh },
          isRecomputing: false,
        });
      } else {
        set({ isRecomputing: false });
      }
    } catch (err) {
      const apiErr = err as ApiError;
      set({ recomputeError: apiErr.message, isRecomputing: false });
    }
  },

  clearErrors: () => set({ fetchError: null, recomputeError: null }),
}));

export default useDashboardStore;