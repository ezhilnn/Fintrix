
// ────────────────────────────────────────────────────────────────
package com.fintrix.modules.creditcard.controller;

import com.fintrix.common.response.ApiResponse;
import com.fintrix.modules.creditcard.dto.CardRecommendationRequest;
import com.fintrix.modules.creditcard.dto.CardRecommendationResponse;
import com.fintrix.modules.creditcard.service.CreditCardRecommendationService;
import com.fintrix.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * CreditCardController
 *
 * POST /api/v1/credit-cards/recommendations
 *   → returns matched cards split into recommended/other/future
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/credit-cards")
@RequiredArgsConstructor
public class CreditCardController {

    private final CreditCardRecommendationService cardService;

    @PostMapping("/recommendations")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CardRecommendationResponse>> getRecommendations(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestBody(required = false)
            CardRecommendationRequest request) {

        log.info("Card recommendations for userId: {}",
                currentUser.getId());

        CardRecommendationResponse response =
                cardService.getRecommendations(
                        currentUser.getId(),
                        request != null ? request
                                : new CardRecommendationRequest());

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}