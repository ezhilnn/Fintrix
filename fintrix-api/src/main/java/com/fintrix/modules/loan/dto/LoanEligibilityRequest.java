// ================================================================
// FILE 1: LoanEligibilityRequest.java
// ================================================================
package com.fintrix.modules.loan.dto;

import com.fintrix.modules.loan.domain.LoanType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * LoanEligibilityRequest
 *
 * What the user submits when checking loan eligibility.
 * The system reads their financial profile from DB automatically —
 * user does NOT re-enter income, credit score etc.
 *
 * Why only 3 fields?
 *  Everything else (income, credit score, FOIR, employment)
 *  is already stored in FinancialProfile from Step 4.
 *  We JOIN those values in the service layer.
 *
 *  This is better UX — user fills profile ONCE,
 *  then just says "I want ₹5L personal loan for 36 months".
 */
@Getter
@Setter
public class LoanEligibilityRequest {

    @NotNull(message = "Loan type is required")
    private LoanType loanType;

    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "10000.00",
            message = "Minimum loan amount is ₹10,000")
    @DecimalMax(value = "100000000.00",
            message = "Maximum loan amount is ₹10 Crore")
    private BigDecimal requestedAmount;

    @NotNull(message = "Loan tenure is required")
    @Min(value = 3,   message = "Minimum tenure is 3 months")
    @Max(value = 360, message = "Maximum tenure is 360 months (30 years)")
    private Integer tenureMonths;

    private String purpose;   // "Home renovation", "Medical", "Wedding" etc.
}

