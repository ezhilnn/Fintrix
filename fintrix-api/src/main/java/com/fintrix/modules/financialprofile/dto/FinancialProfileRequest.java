// ================================================================
// FILE 1: FinancialProfileRequest.java
// What the frontend SENDS when creating/updating financial profile
// ================================================================
package com.fintrix.modules.financialprofile.dto;

import com.fintrix.modules.financialprofile.domain.EmploymentType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * FinancialProfileRequest
 *
 * Captures user's self-reported financial data.
 *
 * Real-world validation rules applied here:
 *
 * Monthly income:
 *   @DecimalMin("1000") → minimum ₹1,000/month
 *   Rejects obviously wrong data like ₹0 or negative income
 *
 * Credit score:
 *   Range 300–900 → CIBIL score range in India
 *   Optional (nullable) → user may not know their score
 *
 * Credit utilization:
 *   Range 0–100 → percentage
 *   High utilization (>30%) hurts credit score
 *   We use this in our ScoreCalculator later
 *
 * Why @NotNull vs @NotBlank?
 *   @NotBlank → for String fields (checks empty string too)
 *   @NotNull  → for non-String fields (Integer, BigDecimal etc.)
 */
@Getter
@Setter
public class FinancialProfileRequest {

    // ── Employment ───────────────────────────────────────────
    @NotNull(message = "Employment type is required")
    private EmploymentType employmentType;

    private String employerName;

    @Min(value = 0, message = "Years of experience cannot be negative")
    @Max(value = 50, message = "Please enter valid years of experience")
    private Integer yearsOfExperience;

    // ── Income & Expenses ────────────────────────────────────
    @NotNull(message = "Monthly income is required")
    @DecimalMin(value = "1000.00",
            message = "Monthly income must be at least ₹1,000")
    @DecimalMax(value = "100000000.00",
            message = "Please enter a valid monthly income")
    private BigDecimal monthlyIncome;

    @DecimalMin(value = "0.00",
            message = "Monthly expenses cannot be negative")
    private BigDecimal monthlyExpenses;

    // ── Existing Obligations ─────────────────────────────────
    @DecimalMin(value = "0.00",
            message = "EMI total cannot be negative")
    private BigDecimal existingEmiTotal;

    @Min(value = 0, message = "Number of loans cannot be negative")
    @Max(value = 20, message = "Please enter valid number of loans")
    private Integer numberOfActiveLoans;

    // ── Credit Profile ───────────────────────────────────────
    // Optional — user may not know their credit score
    @Min(value = 300, message = "CIBIL score minimum is 300")
    @Max(value = 900, message = "CIBIL score maximum is 900")
    private Integer creditScore;

    @Min(value = 0, message = "Number of cards cannot be negative")
    @Max(value = 20, message = "Please enter valid number of cards")
    private Integer numberOfCreditCards;

    @DecimalMin(value = "0.00",
            message = "Credit limit cannot be negative")
    private BigDecimal totalCreditLimit;

    @DecimalMin(value = "0.00",
            message = "Utilization cannot be negative")
    @DecimalMax(value = "100.00",
            message = "Utilization cannot exceed 100%")
    private BigDecimal currentCreditUtilization;

    // ── Spending Preferences ─────────────────────────────────
    private String preferredRewardType;    // CASHBACK / TRAVEL / FUEL
    private String topSpendingCategory;   // FOOD / SHOPPING / TRAVEL
}
