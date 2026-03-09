// ================================================================
// FILE 1: FinancialProfileService.java — Interface
// ================================================================
package com.fintrix.modules.financialprofile.service;

import com.fintrix.modules.financialprofile.dto.FinancialProfileRequest;
import com.fintrix.modules.financialprofile.dto.FinancialProfileResponse;

/**
 * FinancialProfileService — Interface
 *
 * Defines the contract for financial profile operations.
 * Controller and other modules depend on this interface,
 * never on the concrete implementation.
 */
public interface FinancialProfileService {

    // Create profile (first time setup)
    FinancialProfileResponse createProfile(String userId,
            FinancialProfileRequest request);

    // Get existing profile
    FinancialProfileResponse getProfile(String userId);

    // Update existing profile
    FinancialProfileResponse updateProfile(String userId,
            FinancialProfileRequest request);
}
