// ================================================================
// user.service.ts
//
// Mirrors UserController.java — /api/v1/users
//
//   GET    /api/v1/users/me  → getMyProfile()
//   PUT    /api/v1/users/me  → updateMyProfile()
//   DELETE /api/v1/users/me  → deactivateAccount()
// ================================================================

import { get, put, del } from './api.client';
import { API } from '../utils/constants';
import type { UserProfileRequest, UserProfileResponse } from '../types/user.types';

const UserService = {

  // ── GET /api/v1/users/me ─────────────────────────────────────
  // Returns the logged-in user's profile.
  // userId comes from JWT — no param needed.
  getMyProfile(): Promise<UserProfileResponse> {
    return get<UserProfileResponse>(API.USER_ME);
  },

  // ── PUT /api/v1/users/me ─────────────────────────────────────
  // Updates profile. Backend validates with @Valid.
  // Fields: fullName, phoneNumber, city, state, age
  // On success: sets isProfileComplete = true if all required fields filled
  updateMyProfile(request: UserProfileRequest): Promise<UserProfileResponse> {
    return put<UserProfileResponse>(API.USER_ME, request);
  },

  // ── DELETE /api/v1/users/me ──────────────────────────────────
  // Soft-deactivates the account (backend never hard-deletes).
  // Returns void — frontend should logout after calling this.
  deactivateAccount(): Promise<void> {
    return del<void>(API.USER_ME);
  },
};

export default UserService;