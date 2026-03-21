// ================================================================
// FILE 6: TrackingController.java
// ================================================================
package com.fintrix.modules.tracking.controller;

import com.fintrix.common.response.ApiResponse;
import com.fintrix.modules.tracking.dto.AffiliateClickResponse;
import com.fintrix.modules.tracking.dto.TrackEventRequest;
import com.fintrix.modules.tracking.service.TrackingService;
import com.fintrix.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * TrackingController
 *
 * POST /api/v1/tracking/event              → log a frontend event
 * GET  /api/v1/tracking/affiliate-link     → get tracked apply URL
 * POST /api/v1/tracking/affiliate/convert  → webhook for partner conversion
 */
@RestController
@RequestMapping("/api/v1/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final TrackingService trackingService;

    @PostMapping("/event")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> trackEvent(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody TrackEventRequest request,
            HttpServletRequest httpRequest) {

        String ip     = getClientIp(httpRequest);
        String device = httpRequest.getHeader("X-Device-Type");
        trackingService.trackEvent(user.getId(), request, ip, device);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/affiliate-link")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<AffiliateClickResponse>> getAffiliateLink(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam String entityId,
            @RequestParam String productType,
            @RequestParam(required = false) Integer approvalProbability,
            HttpServletRequest httpRequest) {

        AffiliateClickResponse response = trackingService.getAffiliateLink(
                user.getId(), entityId, productType,
                approvalProbability, getClientIp(httpRequest));

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private String getClientIp(HttpServletRequest req) {
        String forwarded = req.getHeader("X-Forwarded-For");
        return forwarded != null
                ? forwarded.split(",")[0].trim()
                : req.getRemoteAddr();
    }
}