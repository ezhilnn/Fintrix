
// ================================================================
// FILE: FinancialHealthScore.java
// com/fintrix/modules/decisionengine/domain/FinancialHealthScore.java
// ================================================================
package com.fintrix.modules.decisionengine.domain;

import com.fintrix.infrastructure.persistence.AuditableEntity;
import com.fintrix.modules.financialprofile.domain.RiskLevel;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Stores computed financial health scores per user over time.
 * History is preserved — new record per recalculation.
 * Used for trend analysis and dashboard display.
 */
@Entity
@Table(name = "financial_health_scores", indexes = {
    @Index(name = "idx_fhs_user_id", columnList = "user_id"),
    @Index(name = "idx_fhs_scored_on", columnList = "scored_on")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FinancialHealthScore extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    // ── Composite Score (0–100) ──────────────────────────────
    @Column(name = "overall_score", nullable = false)
    private Integer overallScore;

    // ── Sub-scores (each 0–100, weighted differently) ────────
    @Column(name = "savings_rate_score")
    private Integer savingsRateScore;          // weight: 25%

    @Column(name = "debt_burden_score")
    private Integer debtBurdenScore;           // weight: 30% (FOIR-based)

    @Column(name = "credit_utilization_score")
    private Integer creditUtilizationScore;    // weight: 20%

    @Column(name = "credit_score_component")
    private Integer creditScoreComponent;      // weight: 25%

    // ── Raw Inputs (snapshot at time of calculation) ─────────
    @Column(name = "foir_at_scoring", precision = 5, scale = 2)
    private BigDecimal foirAtScoring;

    @Column(name = "utilization_at_scoring", precision = 5, scale = 2)
    private BigDecimal utilizationAtScoring;

    @Column(name = "savings_rate_at_scoring", precision = 5, scale = 2)
    private BigDecimal savingsRateAtScoring;

    // ── Risk & Suggestions ───────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false)
    private RiskLevel riskLevel;

    @Column(name = "improvement_tips", length = 2000)
    private String improvementTips;            // JSON array of tip strings

    @Column(name = "risk_warnings", length = 1000)
    private String riskWarnings;               // JSON array of warning strings

    @Column(name = "scored_on", nullable = false)
    private LocalDate scoredOn;

    @Column(name = "is_latest", nullable = false)
    @Builder.Default
    private Boolean isLatest = true;           // only one latest per user
}