package com.fintrix.modules.financialprofile.controller;

import com.fintrix.common.response.ApiResponse;
import com.fintrix.modules.financialprofile.dto.FinancialProfileRequest;
import com.fintrix.modules.financialprofile.dto.FinancialProfileResponse;
import com.fintrix.modules.financialprofile.service.FinancialProfileService;
import com.fintrix.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * FinancialProfileController
 *
 * REST API for financial profile CRUD.
 *
 * Endpoints:
 *
 * POST   /api/v1/financial-profile
 *   → First-time setup of financial profile
 *   → Returns HTTP 201 Created
 *
 * GET    /api/v1/financial-profile
 *   → Fetch current profile with computed fields
 *   → Returns HTTP 200 with FOIR, risk level, score
 *
 * PUT    /api/v1/financial-profile
 *   → Update profile when financial situation changes
 *   → Recomputes FOIR, risk level automatically
 *   → Clears Redis cache → fresh data on next GET
 *
 * Design note — why no userId in URL path?
 *   Bad:  PUT /api/v1/financial-profile/{userId}
 *         → user could pass any userId → access other profiles
 *
 *   Good: PUT /api/v1/financial-profile
 *         → userId comes from JWT token (@AuthenticationPrincipal)
 *         → user can ONLY update their own profile
 *         → security enforced at token level, not URL level
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/financial-profile")
@RequiredArgsConstructor
public class FinancialProfileController {

    private final FinancialProfileService financialProfileService;

    // ── POST /api/v1/financial-profile ────────────────────────
    // Create profile — first time after completing user profile
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<FinancialProfileResponse>> createProfile(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody FinancialProfileRequest request) {

        log.info("POST /financial-profile by userId: {}",
                currentUser.getId());

        FinancialProfileResponse response =
                financialProfileService.createProfile(
                        currentUser.getId(), request);

        /*
         * HTTP 201 Created → standard for successful resource creation
         * HTTP 200 OK      → for updates and fetches
         */
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Financial profile created successfully",
                        response));
    }

    // ── GET /api/v1/financial-profile ─────────────────────────
    // Get profile — called on dashboard load
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<FinancialProfileResponse>> getProfile(
            @AuthenticationPrincipal UserPrincipal currentUser) {

        log.debug("GET /financial-profile by userId: {}",
                currentUser.getId());

        FinancialProfileResponse response =
                financialProfileService.getProfile(currentUser.getId());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ── PUT /api/v1/financial-profile ─────────────────────────
    // Update profile — when income, EMI or credit info changes
    @PutMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<FinancialProfileResponse>> updateProfile(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody FinancialProfileRequest request) {

        log.info("PUT /financial-profile by userId: {}",
                currentUser.getId());

        FinancialProfileResponse response =
                financialProfileService.updateProfile(
                        currentUser.getId(), request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Financial profile updated successfully",
                        response));
    }
}