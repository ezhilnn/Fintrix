// ================================================================
// FILE 6: ConsentController.java
// ================================================================
package com.fintrix.modules.consent.controller;

import com.fintrix.common.response.ApiResponse;
import com.fintrix.modules.consent.dto.ConsentRequest;
import com.fintrix.modules.consent.dto.ConsentStatusResponse;
import com.fintrix.modules.consent.service.ConsentService;
import com.fintrix.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * ConsentController
 *
 * GET  /api/v1/consent        → get current consent status
 * POST /api/v1/consent/grant  → grant a specific consent
 * POST /api/v1/consent/withdraw → withdraw a consent
 *
 * Frontend calls GET on first login to check what's needed.
 * If DATA_PROCESSING = false → show consent modal before any feature.
 */
@RestController
@RequestMapping("/api/v1/consent")
@RequiredArgsConstructor
public class ConsentController {

    private final ConsentService consentService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<ConsentStatusResponse>> getStatus(
            @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(ApiResponse.success(
                consentService.getConsentStatus(user.getId())));
    }

    @PostMapping("/grant")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> grant(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody ConsentRequest request,
            HttpServletRequest httpRequest) {

        String ip = getClientIp(httpRequest);
        String ua = httpRequest.getHeader("User-Agent");
        consentService.grantConsent(user.getId(), request, ip, ua);
        return ResponseEntity.ok(
                ApiResponse.success("Consent recorded", null));
    }

    @PostMapping("/withdraw")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> withdraw(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam String consentType) {
        consentService.withdrawConsent(user.getId(), consentType);
        return ResponseEntity.ok(
                ApiResponse.success("Consent withdrawn", null));
    }

    private String getClientIp(HttpServletRequest req) {
        String forwarded = req.getHeader("X-Forwarded-For");
        return forwarded != null
                ? forwarded.split(",")[0].trim()
                : req.getRemoteAddr();
    }
}