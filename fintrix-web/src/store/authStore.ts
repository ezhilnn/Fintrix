// ================================================================
// authStore.ts — Zustand global auth state
//
// State is the single source of truth for:
//   - Is the user logged in?
//   - Who is the current user?
//   - Is auth still loading?
//
// Integrates with:
//   - auth.service.ts  → token/user storage
//   - api.client.ts    → listens to 'fintrix:unauthorized' event
//                        dispatched when backend returns 401
// ================================================================

import { create } from 'zustand';
import AuthService from '../services/auth.service';
import type { UserProfileResponse } from '../types/user.types';

interface AuthState {
  // ── State ──────────────────────────────────────────────────
  user:          UserProfileResponse | null;
  isAuthenticated: boolean;
  isLoading:     boolean;    // true while fetching user on app boot
  error:         string | null;

  // ── Actions ────────────────────────────────────────────────
  /**
   * Called on app boot (main.tsx / App.tsx useEffect).
   * If token exists in localStorage → fetch /api/v1/users/me
   * to hydrate user into store. If token is invalid → logout.
   */
  initAuth: () => Promise<void>;

  /**
   * Called by OAuthCallbackPage after extracting ?token= from URL.
   * Saves token → fetches user → sets store state.
   */
  handleOAuthSuccess: (token: string) => Promise<void>;

  /** Clear all auth state and localStorage */
  logout: () => void;

  /** Manually set user (e.g. after profile update) */
  setUser: (user: UserProfileResponse) => void;
}

const useAuthStore = create<AuthState>((set) => {

  // ── Listen for 401 from api.client interceptor ──────────────
  // When backend returns 401 (JWT expired/invalid),
  // api.client dispatches this event → we auto-logout
  if (typeof window !== 'undefined') {
    window.addEventListener('fintrix:unauthorized', () => {
      AuthService.logout();
      set({ user: null, isAuthenticated: false, isLoading: false, error: null });
    });
  }

  return {
    // ── Initial state ──────────────────────────────────────────
    user:            null,
    isAuthenticated: false,
    isLoading:       true,    // start true — we check localStorage on boot
    error:           null,

    // ── initAuth ───────────────────────────────────────────────
    initAuth: async () => {
      set({ isLoading: true, error: null });

      // No token → not logged in, nothing to do
      if (!AuthService.isTokenPresent()) {
        set({ isLoading: false, isAuthenticated: false, user: null });
        return;
      }

      try {
        // Try to hydrate from cache first for instant UI
        const cached = AuthService.getCachedUser();
        if (cached) {
          set({ user: cached, isAuthenticated: true });
        }

        // Always re-fetch from backend to ensure token is still valid
        // GET /api/v1/users/me — if 401, interceptor fires logout event
        const freshUser = await AuthService.fetchCurrentUser();
        set({ user: freshUser, isAuthenticated: true, isLoading: false });

      } catch {
        // Token was invalid — interceptor already cleared storage
        set({ user: null, isAuthenticated: false, isLoading: false });
      }
    },

    // ── handleOAuthSuccess ─────────────────────────────────────
    // Called by OAuthCallbackPage.tsx after ?token= is extracted
    handleOAuthSuccess: async (token: string) => {
      set({ isLoading: true, error: null });
      try {
        AuthService.saveToken(token);
        const user = await AuthService.fetchCurrentUser();
        set({ user, isAuthenticated: true, isLoading: false });
      } catch {
        AuthService.logout();
        set({
          user: null,
          isAuthenticated: false,
          isLoading: false,
          error: 'Login failed. Please try again.',
        });
      }
    },

    // ── logout ─────────────────────────────────────────────────
    logout: () => {
      AuthService.logout();
      set({ user: null, isAuthenticated: false, isLoading: false, error: null });
    },

    // ── setUser ────────────────────────────────────────────────
    setUser: (user: UserProfileResponse) => {
      localStorage.setItem('fintrix_user', JSON.stringify(user));
      set({ user });
    },
  };
});

export default useAuthStore;