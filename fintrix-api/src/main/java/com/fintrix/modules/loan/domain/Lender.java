// ================================================================
// FILE: Lender.java
// com/fintrix/modules/loan/domain/Lender.java
// ================================================================
package com.fintrix.modules.loan.domain;

import com.fintrix.infrastructure.persistence.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Lender represents a bank or NBFC with their loan product criteria.
 * Populated via Flyway seed data (V7 migration).
 * Used by LoanRuleEngine to match eligible lenders.
 */
@Entity
@Table(name = "lenders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Lender extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;                           // "HDFC Bank", "Bajaj Finance"

    @Column(name = "logo_url", length = 300)
    private String logoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "loan_type", nullable = false)
    private LoanType loanType;

    // ── Eligibility Criteria ─────────────────────────────────
    @Column(name = "min_credit_score", nullable = false)
    private Integer minCreditScore;

    @Column(name = "min_monthly_income", nullable = false, precision = 12, scale = 2)
    private BigDecimal minMonthlyIncome;

    @Column(name = "max_foir", nullable = false, precision = 5, scale = 2)
    private BigDecimal maxFoir;                    // e.g. 0.50 = 50%

    @Column(name = "min_age", nullable = false)
    private Integer minAge;

    @Column(name = "max_age", nullable = false)
    private Integer maxAge;

    @Column(name = "min_employment_years")
    private Integer minEmploymentYears;

    // ── Allowed Employment Types (stored as comma-separated) ─
    @Column(name = "allowed_employment_types", length = 200)
    private String allowedEmploymentTypes;         // "SALARIED,GOVERNMENT,PSU"

    // ── Product Details ──────────────────────────────────────
    @Column(name = "min_loan_amount", precision = 12, scale = 2)
    private BigDecimal minLoanAmount;

    @Column(name = "max_loan_amount", precision = 12, scale = 2)
    private BigDecimal maxLoanAmount;

    @Column(name = "min_interest_rate", precision = 5, scale = 2)
    private BigDecimal minInterestRate;            // e.g. 10.50

    @Column(name = "max_interest_rate", precision = 5, scale = 2)
    private BigDecimal maxInterestRate;

    @Column(name = "processing_fee_percent", precision = 5, scale = 2)
    private BigDecimal processingFeePercent;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
