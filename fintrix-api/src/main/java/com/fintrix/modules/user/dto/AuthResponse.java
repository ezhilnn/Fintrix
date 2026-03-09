
// ================================================================
// FILE 2: AuthResponse.java
// ================================================================
package com.fintrix.modules.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AuthResponse
 * Returned after successful OAuth2 login + JWT generation.
 * Frontend stores the token and uses it for all subsequent requests.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String  accessToken;
    private String  tokenType;      // "Bearer"
    private Long    expiresIn;      // milliseconds
    private String  userId;
    private String  email;
    private String  fullName;
    private Boolean isProfileComplete;
}
