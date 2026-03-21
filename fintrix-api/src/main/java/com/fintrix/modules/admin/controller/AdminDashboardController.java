// ================================================================
// FILE 4: AdminDashboardController.java — KPI metrics
// ================================================================
package com.fintrix.modules.admin.controller;

import com.fintrix.common.response.ApiResponse;
import com.fintrix.modules.admin.dto.AdminDashboardStats;
import com.fintrix.modules.admin.service.AdminStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * AdminDashboardController
 *
 * GET /api/v1/admin/dashboard → overall platform KPIs
 *   totalUsers, activeUsers, totalLoanChecks, totalCardChecks,
 *   totalFraudChecks, totalAffiliateClicks, totalConversions,
 *   estimatedRevenue
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final AdminStatsService statsService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardStats>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(
                statsService.buildStats()));
    }
}




