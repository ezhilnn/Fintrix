package com.fintrix.modules.fraud.controller;

import com.fintrix.common.response.ApiResponse;
import com.fintrix.modules.fraud.dto.FraudCheckRequest;
import com.fintrix.modules.fraud.dto.FraudCheckResponse;
import com.fintrix.modules.fraud.dto.KeywordScanRequest;
import com.fintrix.modules.fraud.dto.KeywordScanResponse;
import com.fintrix.modules.fraud.service.FraudDetectionService;
import com.fintrix.modules.fraud.service.KeywordScanService;
import com.fintrix.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.fintrix.modules.fraud.dto.KeywordScanRequest;
import com.fintrix.modules.fraud.service.KeywordScanService;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import java.util.List;

/**
 * FraudCheckController
 *
 * Two completely separate fraud checking features:
 *
 * ── Feature 1: Entity Registry Check ─────────────────────────────
 * POST /api/v1/fraud/check
 *   User provides a COMPANY NAME + TYPE
 *   System checks: regulator registry + keyword match on name
 *   Use case: "Is Zerodha SEBI registered?" / "Is XYZ Investments legit?"
 *
 * ── Feature 2: Keyword Scan (NEW) ────────────────────────────────
 * POST /api/v1/fraud/scan
 *   User pastes ANY FREE TEXT — WhatsApp message, SMS, email, ad
 *   System scans the full text for 100+ fraud keyword patterns
 *   Returns per-keyword breakdown with explanations
 *   Use case: "Is this WhatsApp message a scam?"
 *             "Someone sent me this investment pitch, is it real?"
 *
 * GET  /api/v1/fraud/my-alerts → user's past entity checks
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/fraud")
@RequiredArgsConstructor
public class FraudCheckController {

    private final FraudDetectionService fraudService;
    private final KeywordScanService    keywordScanService;

    // ── Feature 1: Entity registry check ─────────────────────────
    @PostMapping("/check")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<FraudCheckResponse>> checkEntity(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody FraudCheckRequest request) {

        log.info("Entity fraud check by userId: {} entity: {}",
                currentUser.getId(), request.getEntityName());

        FraudCheckResponse response =
                fraudService.checkEntity(currentUser.getId(), request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

//     // ── Feature 2: Free-text keyword scan (NEW) ───────────────────
//     @PostMapping("/scan")
//     @PreAuthorize("hasRole('USER')")
//     public ResponseEntity<ApiResponse<KeywordScanResponse>> scanText(
//             @AuthenticationPrincipal UserPrincipal currentUser,
//             @Valid @RequestBody KeywordScanRequest request) {

//         log.info("Keyword scan by userId: {} textLength: {}",
//                 currentUser.getId(), request.getText().length());

//         KeywordScanResponse response =
//                 keywordScanService.scan(currentUser.getId(), request);

//         return ResponseEntity.ok(ApiResponse.success(response));
//     }
        // ── Feature 2: Free-text keyword scan ────────────────────────────
        @PostMapping("/scan")
        @PreAuthorize("hasRole('USER')")
        public ResponseEntity<ApiResponse<KeywordScanResponse>> scanText(
                @AuthenticationPrincipal UserPrincipal currentUser,
                @Valid @RequestBody KeywordScanRequest request) {

        log.info("Keyword scan by userId: {} contentType: {}",
                currentUser.getId(), request.getContentType());

        KeywordScanResponse response =
                keywordScanService.scan(currentUser.getId(), request);

        return ResponseEntity.ok(ApiResponse.success(response));
        }

        // ── Content type dropdown options for frontend ────────────────────
        // No auth needed — just returns enum values for the UI dropdown
        @GetMapping("/scan/content-types")
        public ResponseEntity<ApiResponse<List<Map<String, String>>>> getContentTypes() {
        List<Map<String, String>> types = Arrays.stream(
                KeywordScanRequest.ContentType.values())
                .map(ct -> Map.of(
                        "value", ct.name(),
                        "label", ct.getLabel()))
                .toList();
        return ResponseEntity.ok(ApiResponse.success(types));
        }
    // ── Get past entity checks ────────────────────────────────────
    @GetMapping("/my-alerts")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<FraudCheckResponse>>> getMyAlerts(
            @AuthenticationPrincipal UserPrincipal currentUser) {

        List<FraudCheckResponse> alerts =
                fraudService.getMyAlerts(currentUser.getId());

        return ResponseEntity.ok(ApiResponse.success(alerts));
    }

}