// ================================================================
// userStore.ts
//
// Manages user profile state separate from auth.
// authStore owns: token, isAuthenticated, isLoading
// userStore owns: profile edit state, update loading, errors
//
// Why separate from authStore?
//   authStore is about IDENTITY (are you logged in?)
//   userStore is about PROFILE DATA (what are your details?)
//   Mixing them causes unnecessary re-renders — e.g. updating
//   your city would re-run all auth-gated route checks.
// ================================================================

import { create } from 'zustand';
import UserService from '../services/user.service';
import useAuthStore from './authStore';
import type { UserProfileRequest, UserProfileResponse } from '../types/user.types';
import type { ApiError } from '../types/api.types';

interface UserState {
  // ── State ──────────────────────────────────────────────────
  isUpdating:  boolean;
  isDeactivating: boolean;
  updateError: string | null;
  fieldErrors: Record<string, string> | null;   // from 400 validation

  // ── Actions ────────────────────────────────────────────────

  /**
   * PUT /api/v1/users/me
   * Updates profile, then syncs updated user into authStore.
   * Validation errors from backend (400) are stored in fieldErrors.
   */
  updateProfile: (
    request: UserProfileRequest,
    onSuccess?: (updated: UserProfileResponse) => void,
  ) => Promise<void>;

  /**
   * DELETE /api/v1/users/me
   * Soft-deactivates account then triggers logout.
   */
  deactivateAccount: (onSuccess?: () => void) => Promise<void>;

  clearErrors: () => void;
}

const useUserStore = create<UserState>((set) => ({
  isUpdating:     false,
  isDeactivating: false,
  updateError:    null,
  fieldErrors:    null,

  // ── updateProfile ──────────────────────────────────────────
  updateProfile: async (request, onSuccess) => {
    set({ isUpdating: true, updateError: null, fieldErrors: null });
    try {
      const updated = await UserService.updateMyProfile(request);

      // Sync into authStore so Navbar/header reflects new name instantly
      useAuthStore.getState().setUser(updated);

      set({ isUpdating: false });
      onSuccess?.(updated);

    } catch (err) {
      const apiErr = err as ApiError;
      set({
        isUpdating:  false,
        updateError: apiErr.message,
        // 400 validation: { fullName: "...", age: "..." }
        fieldErrors: apiErr.errors ?? null,
      });
    }
  },

  // ── deactivateAccount ──────────────────────────────────────
  deactivateAccount: async (onSuccess) => {
    set({ isDeactivating: true, updateError: null });
    try {
      await UserService.deactivateAccount();
      // Logout clears token + user from localStorage
      useAuthStore.getState().logout();
      set({ isDeactivating: false });
      onSuccess?.();
    } catch (err) {
      const apiErr = err as ApiError;
      set({ isDeactivating: false, updateError: apiErr.message });
    }
  },

  clearErrors: () => set({ updateError: null, fieldErrors: null }),
}));

export default useUserStore;