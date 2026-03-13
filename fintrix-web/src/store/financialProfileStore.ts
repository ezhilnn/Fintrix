// ================================================================
// financialProfileStore.ts
//
// Manages the full financial profile lifecycle:
//   - Fetching existing profile (GET)
//   - Creating for first time (POST → 201)
//   - Updating when finances change (PUT)
//
// The backend auto-computes on save:
//   foir, monthlySavings, creditScoreRange,
//   financialHealthScore, riskLevel, isComplete
//
// After a successful create/update we also trigger
// financialHealth recompute so the dashboard score stays fresh.
// ================================================================

import { create } from 'zustand';
import FinancialProfileService from '../services/financialProfile.service';
import FinancialHealthService  from '../services/financialHealth.service';
import type {
  FinancialProfileRequest,
  FinancialProfileResponse,
} from '../types/financialProfile.types';
import type { ApiError } from '../types/api.types';

interface FinancialProfileState {
  // ── State ──────────────────────────────────────────────────
  profile:     FinancialProfileResponse | null;
  isFetching:  boolean;
  isSaving:    boolean;
  fetchError:  string | null;
  saveError:   string | null;
  fieldErrors: Record<string, string> | null;   // 400 validation

  // ── Actions ────────────────────────────────────────────────

  /** GET /api/v1/financial-profile */
  fetchProfile: () => Promise<void>;

  /**
   * POST /api/v1/financial-profile — first-time creation (201)
   * PUT  /api/v1/financial-profile — update existing (200)
   * Automatically chooses POST vs PUT based on whether
   * profile exists in store already.
   */
  saveProfile: (
    request: FinancialProfileRequest,
    onSuccess?: (profile: FinancialProfileResponse) => void,
  ) => Promise<void>;

  clearErrors: () => void;
}

const useFinancialProfileStore = create<FinancialProfileState>((set, get) => ({
  profile:     null,
  isFetching:  false,
  isSaving:    false,
  fetchError:  null,
  saveError:   null,
  fieldErrors: null,

  // ── fetchProfile ───────────────────────────────────────────
  fetchProfile: async () => {
    set({ isFetching: true, fetchError: null });
    try {
      const profile = await FinancialProfileService.getProfile();
      set({ profile, isFetching: false });
    } catch (err) {
      const apiErr = err as ApiError;
      // 404 = profile not created yet — not a real error for UI
      if (apiErr.status === 404) {
        set({ profile: null, isFetching: false });
      } else {
        set({ fetchError: apiErr.message, isFetching: false });
      }
    }
  },

  // ── saveProfile ────────────────────────────────────────────
  saveProfile: async (request, onSuccess) => {
    set({ isSaving: true, saveError: null, fieldErrors: null });
    try {
      const existing = get().profile;
      const saved = existing
        ? await FinancialProfileService.updateProfile(request)
        : await FinancialProfileService.createProfile(request);

      // After save, recompute health score so dashboard is fresh
      // Fire-and-forget — don't block the save flow
      FinancialHealthService.computeScore().catch(() => {
        // silently ignore — health score recalc is best-effort
      });

      set({ profile: saved, isSaving: false });
      onSuccess?.(saved);

    } catch (err) {
      const apiErr = err as ApiError;
      set({
        isSaving:    false,
        saveError:   apiErr.message,
        fieldErrors: apiErr.errors ?? null,
      });
    }
  },

  clearErrors: () => set({ saveError: null, fetchError: null, fieldErrors: null }),
}));

export default useFinancialProfileStore;