

// ================================================================
// FILE 2: FinancialProfileResponse.java
// What we SEND BACK to the frontend
// ================================================================
package com.fintrix.modules.financialprofile.dto;

import com.fintrix.modules.financialprofile.domain.EmploymentType;
import com.fintrix.modules.financialprofile.domain.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * FinancialProfileResponse
 *
 * Includes computed fields that the service calculates:
 *  - foir               → Fixed Obligation to Income Ratio
 *  - monthlySavings     → income - expenses - EMIs
 *  - creditScoreRange   → EXCELLENT / GOOD / FAIR / POOR
 *  - financialHealthScore → 0-100 computed score
 *  - riskLevel          → LOW / MEDIUM / HIGH / CRITICAL
 *
 * Frontend uses these directly to show:
 *  - "Your FOIR is 42% — lenders prefer below 50%"
 *  - "Credit score range: GOOD (700-749)"
 *  - Risk badge on dashboard
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialProfileResponse {

    private String         id;
    private String         userId;

    // ── Employment ───────────────────────────────────────────
    private EmploymentType employmentType;
    private String         employerName;
    private Integer        yearsOfExperience;

    // ── Income & Expenses ────────────────────────────────────
    private BigDecimal     monthlyIncome;
    private BigDecimal     monthlyExpenses;
    private BigDecimal     monthlySavings;         // computed
    private BigDecimal     existingEmiTotal;

    // ── Credit Profile ───────────────────────────────────────
    private Integer        creditScore;
    private String         creditScoreRange;       // computed label
    private Integer        numberOfCreditCards;
    private BigDecimal     totalCreditLimit;
    private BigDecimal     currentCreditUtilization;

    // ── Computed by Decision Engine ──────────────────────────
    private BigDecimal     foir;                   // computed
    private Integer        financialHealthScore;   // computed
    private RiskLevel      riskLevel;              // computed

    // ── Preferences ──────────────────────────────────────────
    private String         preferredRewardType;
    private String         topSpendingCategory;

    // ── Profile completeness ─────────────────────────────────
    private Boolean        isComplete;
}