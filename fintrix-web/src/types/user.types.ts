// ================================================================
// user.types.ts
// Mirrors: UserProfileRequest.java, UserProfileResponse.java,
//          AuthResponse.java
// ================================================================

// ── Matches UserProfileRequest.java ─────────────────────────────
export interface UserProfileRequest {
  fullName: string;       // @NotBlank, 2–150 chars
  phoneNumber?: string;   // optional — regex ^[6-9]\d{9}$
  city: string;           // @NotBlank, max 100
  state: string;          // @NotBlank, max 100
  age: number;            // @Min(18) @Max(100)
}

// ── Matches UserProfileResponse.java ────────────────────────────
export interface UserProfileResponse {
  id: string;
  email: string;
  fullName: string;
  profilePictureUrl?: string;
  phoneNumber?: string;
  city?: string;
  state?: string;
  age?: number;
  role: string;
  isProfileComplete: boolean;
}

// ── Matches AuthResponse.java ────────────────────────────────────
export interface AuthResponse {
  accessToken: string;
  tokenType: string;        // always "Bearer"
  expiresIn: number;        // ms — backend sends 86400000 (24h)
  userId: string;
  email: string;
  fullName: string;
  isProfileComplete: boolean;
}