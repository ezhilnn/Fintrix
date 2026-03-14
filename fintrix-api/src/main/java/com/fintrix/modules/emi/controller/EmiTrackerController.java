// ================================================================
// FILE 5: EmiTrackerController.java
// ================================================================
package com.fintrix.modules.emi.controller;
 
import com.fintrix.common.response.ApiResponse;
import com.fintrix.modules.emi.dto.EmiTrackerRequest;
import com.fintrix.modules.emi.dto.EmiTrackerResponse;
import com.fintrix.modules.emi.service.EmiTrackerService;
import com.fintrix.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
 
/**
 * EmiTrackerController
 *
 * POST   /api/v1/emi-tracker        → add EMI
 * GET    /api/v1/emi-tracker        → list my EMIs
 * DELETE /api/v1/emi-tracker/{id}   → remove EMI
 */
@RestController
@RequestMapping("/api/v1/emi-tracker")
@RequiredArgsConstructor
public class EmiTrackerController {
 
    private final EmiTrackerService emiService;
 
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<EmiTrackerResponse>> addEmi(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody EmiTrackerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        emiService.addEmi(currentUser.getId(), request)));
    }
 
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<EmiTrackerResponse>>> getMyEmis(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                emiService.getMyEmis(currentUser.getId())));
    }
 
    @DeleteMapping("/{emiId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> deleteEmi(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable String emiId) {
        emiService.deleteEmi(currentUser.getId(), emiId);
        return ResponseEntity.ok(
                ApiResponse.success("EMI tracker removed", null));
    }
}