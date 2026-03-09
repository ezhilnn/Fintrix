// ================================================================
// FILE 1: LoanEligibilityService.java — Interface
// ================================================================
package com.fintrix.modules.loan.service;

import com.fintrix.modules.loan.dto.LoanEligibilityRequest;
import com.fintrix.modules.loan.dto.LoanEligibilityResponse;

public interface LoanEligibilityService {

    LoanEligibilityResponse checkEligibility(
            String userId,
            LoanEligibilityRequest request);
}

