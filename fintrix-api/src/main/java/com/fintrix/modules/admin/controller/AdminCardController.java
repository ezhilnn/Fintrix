// ================================================================
// FILE 2: AdminCardController.java
// ================================================================
package com.fintrix.modules.admin.controller;

import com.fintrix.common.response.ApiResponse;
import com.fintrix.modules.creditcard.domain.CreditCard;
import com.fintrix.modules.creditcard.repository.CreditCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * AdminCardController
 *
 * GET    /api/v1/admin/cards         → paginated card list
 * PUT    /api/v1/admin/cards/{id}    → update card details
 * POST   /api/v1/admin/cards         → add new card
 * DELETE /api/v1/admin/cards/{id}    → soft deactivate
 */
@RestController
@RequestMapping("/api/v1/admin/cards")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCardController {

    private final CreditCardRepository cardRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CreditCard>>> getAllCards(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                cardRepository.findAll(PageRequest.of(page, size))));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CreditCard>> addCard(
            @RequestBody CreditCard card) {
        return ResponseEntity.ok(ApiResponse.success(
                cardRepository.save(card)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CreditCard>> updateCard(
            @PathVariable String id,
            @RequestBody CreditCard updated) {
        return cardRepository.findById(id).map(card -> {
            card.setAnnualFee(updated.getAnnualFee());
            card.setJoiningFee(updated.getJoiningFee());
            card.setMinCreditScore(updated.getMinCreditScore());
            card.setMinMonthlyIncome(updated.getMinMonthlyIncome());
            card.setRewardRate(updated.getRewardRate());
            card.setKeyBenefits(updated.getKeyBenefits());
            card.setAnnualFeeWaiverCondition(updated.getAnnualFeeWaiverCondition());
            return ResponseEntity.ok(ApiResponse.success(
                    cardRepository.save(card)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivateCard(
            @PathVariable String id) {
        cardRepository.findById(id).ifPresent(card -> {
            card.setIsActive(false);
            cardRepository.save(card);
        });
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}


