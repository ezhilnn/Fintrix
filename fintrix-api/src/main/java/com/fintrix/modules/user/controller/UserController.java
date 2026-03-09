// ================================================================
// FILE 4: UserController.java
// com/fintrix/modules/user/controller/UserController.java
// ================================================================
package com.fintrix.modules.user.controller;

import com.fintrix.common.response.ApiResponse;
import com.fintrix.modules.user.dto.UserProfileRequest;
import com.fintrix.modules.user.dto.UserProfileResponse;
import com.fintrix.modules.user.service.UserService;
import com.fintrix.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * UserController
 *
 * REST API endpoints for User profile management.
 *
 * @RestController     → @Controller + @ResponseBody
 *                       all methods return JSON automatically
 *
 * @RequestMapping     → base URL for all methods in this class
 *
 * @AuthenticationPrincipal UserPrincipal currentUser
 *   → Spring injects the logged-in user automatically
 *   → comes from SecurityContextHolder (set by JwtFilter)
 *   → no need to parse JWT manually in controller
 *
 * @Valid
 *   → triggers Jakarta Bean Validation on request body
 *   → if @NotBlank, @Size etc. fail → GlobalExceptionHandler
 *      catches MethodArgumentNotValidException → returns 400
 *
 * @PreAuthorize("hasRole('USER')")
 *   → method-level security check
 *   → only users with ROLE_USER can call this endpoint
 *   → needs @EnableMethodSecurity in SecurityConfig ✅
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ── GET /api/v1/users/me ──────────────────────────────────
    // Returns current logged-in user's profile
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile(
            @AuthenticationPrincipal UserPrincipal currentUser) {

        log.debug("GET /me called by userId: {}", currentUser.getId());

        UserProfileResponse profile =
                userService.getMyProfile(currentUser.getId());

        return ResponseEntity.ok(
                ApiResponse.success(profile));
    }

    // ── PUT /api/v1/users/me ──────────────────────────────────
    // Updates current logged-in user's profile
    @PutMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateMyProfile(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody UserProfileRequest request) {

        log.info("PUT /me called by userId: {}", currentUser.getId());

        UserProfileResponse updated =
                userService.updateMyProfile(currentUser.getId(), request);

        return ResponseEntity.ok(
                ApiResponse.success("Profile updated successfully", updated));
    }

    // ── DELETE /api/v1/users/me ───────────────────────────────
    // Soft-deactivates the account (never hard delete)
    @DeleteMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> deactivateAccount(
            @AuthenticationPrincipal UserPrincipal currentUser) {

        log.warn("DELETE /me — deactivation requested by userId: {}",
                currentUser.getId());

        userService.deactivateAccount(currentUser.getId());

        return ResponseEntity.ok(
                ApiResponse.success("Account deactivated successfully", null));
    }
}