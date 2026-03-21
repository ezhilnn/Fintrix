package com.fintrix.modules.creditcard.domain;

import com.fintrix.infrastructure.persistence.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

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

    // "Apply Now" button destination — direct link to bank's card application page
    @Column(name = "apply_url", length = 500)
    private String applyUrl;

    // Added in V2 schema enhancement
    @Column(name = "card_network", length = 30)
    private String cardNetwork;                  // Visa, Mastercard, Amex, RuPay

    @Column(name = "interest_rate", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal interestRate = new BigDecimal("42.00");

    @Column(name = "fuel_surcharge_waiver", nullable = false)
    @Builder.Default
    private Boolean fuelSurchargeWaiver = false;

    @Column(name = "international_usage", nullable = false)
    @Builder.Default
    private Boolean internationalUsage = true;

    @Column(name = "lounge_access", length = 100)
    private String loungeAccess;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_category", nullable = false)
    private CardCategory cardCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "reward_type", nullable = false)
    private RewardType rewardType;

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

    @Column(name = "joining_fee", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal joiningFee = BigDecimal.ZERO;

    @Column(name = "annual_fee", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal annualFee = BigDecimal.ZERO;

    @Column(name = "annual_fee_waiver_condition", length = 300)
    private String annualFeeWaiverCondition;

    @Column(name = "reward_rate", length = 200)
    private String rewardRate;

    @Column(name = "welcome_benefit", length = 300)
    private String welcomeBenefit;

    @Column(name = "key_benefits", length = 1000)
    private String keyBenefits;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}