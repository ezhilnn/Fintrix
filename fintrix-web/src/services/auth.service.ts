// ================================================================
// auth.service.ts
//
// Handles the Google OAuth2 flow initiated by Spring Security.
//
// Full flow (matches backend exactly):
//
//  1. User clicks "Sign in with Google"
//     → Frontend redirects to: GET /oauth2/authorization/google
//     → Spring Security handles this (SecurityConfig.oauth2Login)
//
//  2. Google authenticates the user
//     → Spring calls OAuth2UserService.java (loads/creates user in DB)
//
//  3. OAuth2AuthenticationSuccessHandler.java runs:
//     → Generates JWT via JwtTokenProvider.generateToken(userId)
//     → Redirects to: http://localhost:5173/oauth2/callback?token=<jwt>
//
//  4. OAuthCallbackPage.tsx renders → calls auth.service.handleCallback()
//     → Extracts ?token= from URL
//     → Stores in localStorage
//     → Fetches user profile via GET /api/v1/users/me
//     → Stores user in authStore
//     → Redirects to /dashboard
// ================================================================

import { get } from './api.client';
import { AUTH_TOKEN_KEY, AUTH_USER_KEY, GOOGLE_OAUTH_URL, API } from '../utils/constants';
import type { UserProfileResponse } from '../types/user.types';

const AuthService = {

  // ── Step 1: Redirect browser to Spring's OAuth2 login ───────
  // SecurityConfig exposes: /oauth2/authorization/google
  initiateGoogleLogin(): void {
    window.location.href = GOOGLE_OAUTH_URL;
  },

  // ── Step 2: Called by OAuthCallbackPage after redirect ───────
  // OAuth2AuthenticationSuccessHandler appends: ?token=eyJhbGci...
  extractTokenFromUrl(): string | null {
    const params = new URLSearchParams(window.location.search);
    return params.get('token');
  },

  // ── Step 3: Store JWT in localStorage ───────────────────────
  // JwtAuthenticationFilter reads: Authorization: Bearer <token>
  saveToken(token: string): void {
    localStorage.setItem(AUTH_TOKEN_KEY, token);
  },

  // ── Step 4: Fetch the logged-in user after token saved ───────
  // GET /api/v1/users/me — UserController.getMyProfile()
  async fetchCurrentUser(): Promise<UserProfileResponse> {
    const user = await get<UserProfileResponse>(API.USER_ME);
    // Cache user in localStorage to avoid refetch on reload
    localStorage.setItem(AUTH_USER_KEY, JSON.stringify(user));
    return user;
  },

  // ── Get cached user from localStorage ───────────────────────
  getCachedUser(): UserProfileResponse | null {
    const raw = localStorage.getItem(AUTH_USER_KEY);
    if (!raw) return null;
    try {
      return JSON.parse(raw) as UserProfileResponse;
    } catch {
      return null;
    }
  },

  // ── Get stored JWT ───────────────────────────────────────────
  getToken(): string | null {
    return localStorage.getItem(AUTH_TOKEN_KEY);
  },

  // ── Check if a token exists (doesn't validate expiry) ───────
  isTokenPresent(): boolean {
    return !!localStorage.getItem(AUTH_TOKEN_KEY);
  },

  // ── Logout — clear all stored auth data ─────────────────────
  // Backend is stateless (JWT) — no server-side logout needed
  logout(): void {
    localStorage.removeItem(AUTH_TOKEN_KEY);
    localStorage.removeItem(AUTH_USER_KEY);
  },
};

export default AuthService;