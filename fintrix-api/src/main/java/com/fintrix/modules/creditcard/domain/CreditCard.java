

// ================================================================
// FILE: CreditCard.java
// com/fintrix/modules/creditcard/domain/CreditCard.java
// ================================================================
package com.fintrix.modules.creditcard.domain;

import com.fintrix.infrastructure.persistence.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * CreditCard represents a bank's credit card product with eligibility criteria.
 * Populated via Flyway seed data (V8 migration).
 */
@Entity
@Table(name = "credit_cards")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreditCard extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "bank_name", nullable = false, length = 150)
    private String bankName;

    @Column(name = "card_name", nullable = false, length = 200)
    private String cardName;

    @Column(name = "logo_url", length = 300)
    private String logoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_category", nullable = false)
    private CardCategory cardCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "reward_type", nullable = false)
    private RewardType rewardType;

    // ── Eligibility Criteria ─────────────────────────────────
    @Column(name = "min_credit_score", nullable = false)
    private Integer minCreditScore;

    @Column(name = "min_monthly_income", nullable = false, precision = 12, scale = 2)
    private BigDecimal minMonthlyIncome;

    @Column(name = "min_age", nullable = false)
    private Integer minAge;

    @Column(name = "max_age", nullable = false)
    private Integer maxAge;

    @Column(name = "allowed_employment_types", length = 200)
    private String allowedEmploymentTypes;

    // ── Fees ─────────────────────────────────────────────────
    @Column(name = "joining_fee", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal joiningFee = BigDecimal.ZERO;

    @Column(name = "annual_fee", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal annualFee = BigDecimal.ZERO;

    @Column(name = "annual_fee_waiver_condition", length = 300)
    private String annualFeeWaiverCondition;       // "Spend ₹1.5L in a year"

    // ── Rewards ──────────────────────────────────────────────
    @Column(name = "reward_rate", length = 200)
    private String rewardRate;                     // "1.5% cashback on all spends"

    @Column(name = "welcome_benefit", length = 300)
    private String welcomeBenefit;

    @Column(name = "key_benefits", length = 1000)
    private String keyBenefits;                    // JSON string of benefit list

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}

