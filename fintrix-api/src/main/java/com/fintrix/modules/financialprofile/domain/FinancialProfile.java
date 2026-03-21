package com.fintrix.modules.financialprofile.domain;

import com.fintrix.infrastructure.persistence.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "financial_profiles", indexes = {
    @Index(name = "idx_fp_user_id", columnList = "user_id", unique = true)
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FinancialProfile extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    // ── Employment ────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", nullable = false)
    private EmploymentType employmentType;

    @Column(name = "employer_name", length = 200)
    private String employerName;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    // ── Income & Expenses ─────────────────────────────────────
    @Column(name = "monthly_income", nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyIncome;

    @Column(name = "monthly_expenses", precision = 12, scale = 2)
    private BigDecimal monthlyExpenses;

    @Column(name = "monthly_savings", precision = 12, scale = 2)
    private BigDecimal monthlySavings;

    // ── Existing Obligations ──────────────────────────────────
    @Column(name = "existing_emi_total", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal existingEmiTotal = BigDecimal.ZERO;

    @Column(name = "number_of_active_loans")
    @Builder.Default
    private Integer numberOfActiveLoans = 0;

    // ── Credit Profile ────────────────────────────────────────
    @Column(name = "credit_score")
    private Integer creditScore;

    @Column(name = "credit_score_range", length = 20)
    private String creditScoreRange;

    @Column(name = "number_of_credit_cards")
    @Builder.Default
    private Integer numberOfCreditCards = 0;

    @Column(name = "total_credit_limit", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalCreditLimit = BigDecimal.ZERO;

    @Column(name = "current_credit_utilization", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal currentCreditUtilization = BigDecimal.ZERO;

    // ── Spending Preferences ──────────────────────────────────
    @Column(name = "preferred_reward_type", length = 50)
    private String preferredRewardType;

    @Column(name = "top_spending_category", length = 100)
    private String topSpendingCategory;

    // ── Computed fields ───────────────────────────────────────

    // FOIR = existingEMI / income × 100
    // Only counts loan EMIs — what banks check for eligibility
    @Column(name = "foir", precision = 5, scale = 2)
    private BigDecimal foir;

    // DTI = (existingEMI + monthlyExpenses) / income × 100
    // Counts ALL obligations — broader picture of financial health
    // FOIR ignores rent/food/utilities; DTI includes everything
    @Column(name = "dti", precision = 5, scale = 2)
    private BigDecimal dti;

    // DTI range label: LOW (<30%) / MODERATE (30-43%) / HIGH (43-50%) / CRITICAL (50%+)
    @Column(name = "dti_range", length = 20)
    private String dtiRange;

    @Column(name = "financial_health_score")
    private Integer financialHealthScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", length = 20)
    private RiskLevel riskLevel;
}