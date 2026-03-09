
// ================================================================
// FILE 4: FinancialHealthController.java
// ================================================================
package com.fintrix.modules.decisionengine.controller;

import com.fintrix.common.response.ApiResponse;
import com.fintrix.modules.decisionengine.dto.FinancialHealthResponse;
import com.fintrix.modules.decisionengine.service.FinancialHealthService;
import com.fintrix.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * FinancialHealthController
 *
 * GET  /api/v1/financial-health        → get latest saved score
 * POST /api/v1/financial-health/compute → compute fresh score now
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/financial-health")
@RequiredArgsConstructor
public class FinancialHealthController {

    private final FinancialHealthService healthService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<FinancialHealthResponse>> getLatestScore(
            @AuthenticationPrincipal UserPrincipal currentUser) {

        FinancialHealthResponse response =
                healthService.getLatestScore(currentUser.getId());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/compute")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<FinancialHealthResponse>> computeScore(
            @AuthenticationPrincipal UserPrincipal currentUser) {

        log.info("Manual score compute triggered by userId: {}",
                currentUser.getId());

        FinancialHealthResponse response =
                healthService.computeAndSave(currentUser.getId());

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Financial health score updated", response));
    }
}