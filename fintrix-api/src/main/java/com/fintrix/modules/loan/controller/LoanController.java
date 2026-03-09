
// ================================================================
// FILE 3: LoanController.java
// ================================================================
package com.fintrix.modules.loan.controller;

import com.fintrix.common.response.ApiResponse;
import com.fintrix.modules.loan.dto.LoanEligibilityRequest;
import com.fintrix.modules.loan.dto.LoanEligibilityResponse;
import com.fintrix.modules.loan.service.LoanEligibilityService;
import com.fintrix.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * LoanController
 *
 * POST /api/v1/loans/check-eligibility
 *
 * User sends: loanType, requestedAmount, tenureMonths
 * System returns: eligible lenders, probabilities, EMIs, tips
 *
 * This is a POST not GET because:
 *  - Request body contains financial parameters
 *  - We don't want financial data in URL (logs, browser history)
 *  - Results depend on both URL params AND stored profile
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanEligibilityService loanEligibilityService;

    @PostMapping("/check-eligibility")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<LoanEligibilityResponse>> checkEligibility(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody LoanEligibilityRequest request) {

        log.info("Loan eligibility check — userId: {} type: {} amount: {}",
                currentUser.getId(),
                request.getLoanType(),
                request.getRequestedAmount());

        LoanEligibilityResponse response =
                loanEligibilityService.checkEligibility(
                        currentUser.getId(), request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}