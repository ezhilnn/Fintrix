package com.fintrix.modules.financialprofile.domain;

import com.fintrix.infrastructure.persistence.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * FinancialProfile stores a user's self-reported financial data.
 *
 * Design decisions:
 *  - One profile per user (OneToOne)
 *  - Credit score is optional (user may not know it)
 *  - All monetary values in INR, stored as BigDecimal for precision
 *  - FOIR (Fixed Obligation to Income Ratio) calculated at service layer
 *  - No Aadhaar / PAN stored — regulatory compliance
 */
@Entity
@Table(name = "financial_profiles", indexes = {
    @Index(name = "idx_fp_user_id", columnList = "user_id", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialProfile extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    // ── Relationship ─────────────────────────────────────────
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    // ── Employment ───────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", nullable = false)
    private EmploymentType employmentType;

    @Column(name = "employer_name", length = 200)
    private String employerName;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    // ── Income & Expenses (Monthly, INR) ─────────────────────
    @Column(name = "monthly_income", nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyIncome;

    @Column(name = "monthly_expenses", precision = 12, scale = 2)
    private BigDecimal monthlyExpenses;

    @Column(name = "monthly_savings", precision = 12, scale = 2)
    private BigDecimal monthlySavings;

    // ── Existing Obligations ─────────────────────────────────
    @Column(name = "existing_emi_total", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal existingEmiTotal = BigDecimal.ZERO;

    @Column(name = "number_of_active_loans")
    @Builder.Default
    private Integer numberOfActiveLoans = 0;

    // ── Credit Profile ───────────────────────────────────────
    @Column(name = "credit_score")
    private Integer creditScore;                   // null if user doesn't know

    @Column(name = "credit_score_range", length = 20)
    private String creditScoreRange;              // EXCELLENT / GOOD / FAIR / POOR

    @Column(name = "number_of_credit_cards")
    @Builder.Default
    private Integer numberOfCreditCards = 0;

    @Column(name = "total_credit_limit", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalCreditLimit = BigDecimal.ZERO;

    @Column(name = "current_credit_utilization", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal currentCreditUtilization = BigDecimal.ZERO;   // percentage 0-100

    // ── Spending Preferences ─────────────────────────────────
    @Column(name = "preferred_reward_type", length = 50)
    private String preferredRewardType;            // CASHBACK / TRAVEL / FUEL / SHOPPING

    @Column(name = "top_spending_category", length = 100)
    private String topSpendingCategory;

    // ── Derived / Computed (updated by Decision Engine) ──────
    @Column(name = "foir", precision = 5, scale = 2)
    private BigDecimal foir;                       // Fixed Obligation to Income Ratio

    @Column(name = "financial_health_score")
    private Integer financialHealthScore;          // 0–100, computed by ScoreCalculator

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", length = 20)
    private RiskLevel riskLevel;
}


