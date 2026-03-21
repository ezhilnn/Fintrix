package com.fintrix.modules.financialprofile.dto;

import com.fintrix.modules.financialprofile.domain.EmploymentType;
import com.fintrix.modules.financialprofile.domain.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialProfileResponse {

    private String         id;
    private String         userId;

    // ── Employment ────────────────────────────────────────────
    private EmploymentType employmentType;
    private String         employerName;
    private Integer        yearsOfExperience;

    // ── Income & Expenses ─────────────────────────────────────
    private BigDecimal     monthlyIncome;
    private BigDecimal     monthlyExpenses;
    private BigDecimal     monthlySavings;
    private BigDecimal     existingEmiTotal;
    private Integer        numberOfActiveLoans;

    // ── Credit Profile ────────────────────────────────────────
    private Integer        creditScore;
    private String         creditScoreRange;
    private Integer        numberOfCreditCards;
    private BigDecimal     totalCreditLimit;
    private BigDecimal     currentCreditUtilization;

    // ── Computed ratios ───────────────────────────────────────

    // FOIR: existingEMI / income × 100
    // What banks check — only loan EMIs counted
    private BigDecimal     foir;

    // DTI: (existingEMI + monthlyExpenses) / income × 100
    // Full financial obligation picture — includes rent, food, utilities
    // A person with high rent but zero loans has low FOIR but high DTI
    private BigDecimal     dti;
    private String         dtiRange;   // LOW / MODERATE / HIGH / CRITICAL

    // ── Other computed ────────────────────────────────────────
    private Integer        financialHealthScore;
    private RiskLevel      riskLevel;

    // ── Preferences ───────────────────────────────────────────
    private String         preferredRewardType;
    private String         topSpendingCategory;

    // ── Profile completeness ──────────────────────────────────
    private Boolean        isComplete;
}