
// ================================================================
// FILE 2: FinancialHealthResponse.java (DTO)
// ================================================================
package com.fintrix.modules.decisionengine.dto;

import com.fintrix.modules.financialprofile.domain.RiskLevel;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * FinancialHealthResponse
 *
 * Complete response for the financial health dashboard.
 * Frontend uses this to render:
 *  - Score gauge (0-100)
 *  - Sub-score breakdown chart
 *  - Risk badge
 *  - Improvement tips list
 *  - Risk warnings banner
 *  - Score trend line chart
 */
@Getter
@Builder
public class FinancialHealthResponse {

    // ── Current score ─────────────────────────────────────────
    private Integer    overallScore;          // 0-100
    private RiskLevel  riskLevel;             // LOW/MEDIUM/HIGH/CRITICAL
    private String     riskLabel;             // human-readable label

    // ── Sub-score breakdown (for radar/bar chart) ─────────────
    private Integer    debtBurdenScore;       // 0-100 (weight 30%)
    private Integer    savingsRateScore;      // 0-100 (weight 25%)
    private Integer    creditScoreComponent;  // 0-100 (weight 25%)
    private Integer    utilizationScore;      // 0-100 (weight 20%)

    // ── Raw values (shown alongside scores) ──────────────────
    private BigDecimal foir;
    private Integer    creditScore;
    private String     creditScoreRange;
    private BigDecimal creditUtilization;
    private BigDecimal savingsRate;           // percentage

    // ── Actionable output ─────────────────────────────────────
    private List<String> improvementTips;
    private List<String> riskWarnings;

    // ── Score history for trend chart ─────────────────────────
    private List<ScoreTrend> scoreTrend;

    // ── Meta ──────────────────────────────────────────────────
    private LocalDate  scoredOn;
    private Boolean    isFirstScore;

    @Getter
    @Builder
    public static class ScoreTrend {
        private LocalDate scoredOn;
        private Integer   score;
        private RiskLevel riskLevel;
    }
}

