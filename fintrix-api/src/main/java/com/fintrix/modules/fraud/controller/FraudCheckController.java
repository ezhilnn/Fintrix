
// ================================================================
// FILE 3: FraudCheckController.java
// ================================================================
package com.fintrix.modules.fraud.controller;

import com.fintrix.common.response.ApiResponse;
import com.fintrix.modules.fraud.dto.FraudCheckRequest;
import com.fintrix.modules.fraud.dto.FraudCheckResponse;
import com.fintrix.modules.fraud.service.FraudDetectionService;
import com.fintrix.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * FraudCheckController
 *
 * POST /api/v1/fraud/check       → check if an entity is safe
 * GET  /api/v1/fraud/my-alerts   → get user's past fraud checks
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/fraud")
@RequiredArgsConstructor
public class FraudCheckController {

    private final FraudDetectionService fraudService;

    @PostMapping("/check")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<FraudCheckResponse>> checkEntity(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody FraudCheckRequest request) {

        log.info("Fraud check by userId: {} entity: {}",
                currentUser.getId(), request.getEntityName());

        FraudCheckResponse response =
                fraudService.checkEntity(currentUser.getId(), request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/my-alerts")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<FraudCheckResponse>>> getMyAlerts(
            @AuthenticationPrincipal UserPrincipal currentUser) {

        List<FraudCheckResponse> alerts =
                fraudService.getMyAlerts(currentUser.getId());

        return ResponseEntity.ok(ApiResponse.success(alerts));
    }
}