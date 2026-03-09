
// ================================================================
// FILE 3: DashboardController.java
// ================================================================
package com.fintrix.modules.bff.controller;

import com.fintrix.common.response.ApiResponse;
import com.fintrix.modules.bff.dto.DashboardResponse;
import com.fintrix.modules.bff.service.DashboardAggregatorService;
import com.fintrix.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * DashboardController — BFF endpoint
 *
 * GET /api/v1/bff/dashboard
 *  → returns everything React needs for the main dashboard
 *  → single API call instead of 3 separate calls
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/bff")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardAggregatorService aggregatorService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(
            @AuthenticationPrincipal UserPrincipal currentUser) {

        log.debug("Dashboard requested by userId: {}",
                currentUser.getId());

        DashboardResponse response =
                aggregatorService.buildDashboard(currentUser.getId());

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}