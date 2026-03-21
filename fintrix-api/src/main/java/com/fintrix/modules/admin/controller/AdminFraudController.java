// ================================================================
// FILE 3: AdminFraudController.java
// ================================================================
package com.fintrix.modules.admin.controller;

import com.fintrix.common.response.ApiResponse;
import com.fintrix.modules.fraud.domain.FraudKeyword;
import com.fintrix.modules.fraud.repository.FraudKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AdminFraudController
 *
 * GET    /api/v1/admin/fraud/keywords       → list all keywords
 * POST   /api/v1/admin/fraud/keywords       → add new keyword
 * DELETE /api/v1/admin/fraud/keywords/{id}  → deactivate keyword
 */
@RestController
@RequestMapping("/api/v1/admin/fraud")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminFraudController {

    private final FraudKeywordRepository keywordRepository;

    @GetMapping("/keywords")
    public ResponseEntity<ApiResponse<List<FraudKeyword>>> getKeywords() {
        return ResponseEntity.ok(ApiResponse.success(
                keywordRepository.findAll()));
    }

    @PostMapping("/keywords")
    public ResponseEntity<ApiResponse<FraudKeyword>> addKeyword(
            @RequestBody FraudKeyword keyword) {
        keyword.setIsActive(true);
        return ResponseEntity.ok(ApiResponse.success(
                keywordRepository.save(keyword)));
    }

    @DeleteMapping("/keywords/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivateKeyword(
            @PathVariable String id) {
        keywordRepository.findById(id).ifPresent(kw -> {
            kw.setIsActive(false);
            keywordRepository.save(kw);
        });
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

