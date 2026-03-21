package com.fintrix.modules.admin.controller;

import com.fintrix.common.response.ApiResponse;
import com.fintrix.modules.loan.domain.Lender;
import com.fintrix.modules.loan.repository.LenderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * AdminLenderController
 *
 * GET    /api/v1/admin/lenders             → paginated lender list
 * POST   /api/v1/admin/lenders             → CREATE new lender
 * PUT    /api/v1/admin/lenders/{id}        → update existing lender
 * POST   /api/v1/admin/lenders/{id}/toggle → activate / deactivate
 * DELETE /api/v1/admin/lenders/{id}        → soft delete
 */
@RestController
@RequestMapping("/api/v1/admin/lenders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminLenderController {

    private final LenderRepository lenderRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Lender>>> getAllLenders(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                lenderRepository.findAll(PageRequest.of(page, size))));
    }

    // ── CREATE new lender ─────────────────────────────────────────
    @PostMapping
    public ResponseEntity<ApiResponse<Lender>> createLender(
            @RequestBody Lender lender) {
        lender.setIsActive(true);
        Lender saved = lenderRepository.save(lender);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Lender created", saved));
    }

    // ── UPDATE existing lender ────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Lender>> updateLender(
            @PathVariable String id,
            @RequestBody Lender updated) {
        return lenderRepository.findById(id).map(lender -> {
            // Eligibility criteria
            lender.setMinCreditScore(updated.getMinCreditScore());
            lender.setMinMonthlyIncome(updated.getMinMonthlyIncome());
            lender.setMaxFoir(updated.getMaxFoir());
            lender.setMinAge(updated.getMinAge());
            lender.setMaxAge(updated.getMaxAge());
            lender.setMinEmploymentYears(updated.getMinEmploymentYears());
            lender.setAllowedEmploymentTypes(updated.getAllowedEmploymentTypes());
            // Product details
            lender.setMinLoanAmount(updated.getMinLoanAmount());
            lender.setMaxLoanAmount(updated.getMaxLoanAmount());
            lender.setMinInterestRate(updated.getMinInterestRate());
            lender.setMaxInterestRate(updated.getMaxInterestRate());
            lender.setProcessingFeePercent(updated.getProcessingFeePercent());
            // Identity
            lender.setName(updated.getName());
            lender.setLogoUrl(updated.getLogoUrl());
            lender.setApplyUrl(updated.getApplyUrl());
            lender.setLenderType(updated.getLenderType());
            lender.setRegulator(updated.getRegulator());
            return ResponseEntity.ok(ApiResponse.success(
                    lenderRepository.save(lender)));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ── TOGGLE active / inactive ──────────────────────────────────
    @PostMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<Lender>> toggleLender(
            @PathVariable String id) {
        return lenderRepository.findById(id).map(lender -> {
            lender.setIsActive(!Boolean.TRUE.equals(lender.getIsActive()));
            return ResponseEntity.ok(ApiResponse.success(
                    lenderRepository.save(lender)));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ── SOFT DELETE ───────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLender(
            @PathVariable String id) {
        lenderRepository.findById(id).ifPresent(lender -> {
            lender.setIsActive(false);
            lenderRepository.save(lender);
        });
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}