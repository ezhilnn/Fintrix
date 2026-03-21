package com.fintrix.modules.bff.dto;

import com.fintrix.modules.financialprofile.domain.EmploymentType;
import com.fintrix.modules.financialprofile.domain.RiskLevel;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class DashboardFinancialMetrics {

    // ── Section 1: Employment identity ────────────────────────────
    private EmploymentType employmentType;
    private String         employmentTypeLabel;
    private String         employerName;
    private Integer        yearsOfExperience;
    private String         experienceLabel;

    // ── Section 2: Income & cash flow ─────────────────────────────
    private BigDecimal monthlyIncome;
    private BigDecimal monthlyExpenses;
    private BigDecimal existingEmiTotal;
    private BigDecimal monthlySavings;
    private BigDecimal disposableIncome;
    private Integer    savingsRatePercent;
    private String     savingsRateLabel;
    private String     cashFlowStatus;       // POSITIVE / TIGHT / NEGATIVE

    // ── Section 3: Debt profile ────────────────────────────────────

    // FOIR — Fixed Obligation to Income Ratio
    // Formula: existingEMI / income × 100
    // Used by: banks for loan eligibility checks
    // Only counts EMI obligations
    private BigDecimal foir;
    private String     foirLabel;
    private String     foirStatus;           // HEALTHY / MODERATE / HIGH / CRITICAL

    // DTI — Debt-to-Income Ratio
    // Formula: (existingEMI + monthlyExpenses) / income × 100
    // Used by: financial planners for overall health assessment
    // Counts ALL obligations including rent, food, utilities
    // Someone with FOIR=0% but DTI=90% (high rent) looks eligible
    // for loans but is actually financially stretched
    private BigDecimal dti;
    private String     dtiRange;             // LOW / MODERATE / HIGH / CRITICAL
    private String     dtiLabel;             // "Moderate — 42% of income to obligations"
    private String     dtiVsFoirInsight;     // explains the gap between the two

    private Integer    numberOfActiveLoans;
    private String     loanBurdenLabel;
    private BigDecimal maxAdditionalEmiCapacity;
    private String     additionalEmiLabel;

    // ── Section 4: EMI tracker ─────────────────────────────────────
    private Integer          totalTrackedEmis;
    private BigDecimal       totalMonthlyEmiCommitment;
    private Integer          emisDueSoon;
    private List<EmiSummary> upcomingEmis;

    // ── Section 5: Credit profile ──────────────────────────────────
    private Integer    creditScore;
    private String     creditScoreRange;
    private String     creditScoreLabel;
    private String     creditScoreTip;
    private Integer    numberOfCreditCards;
    private BigDecimal totalCreditLimit;
    private BigDecimal currentCreditUtilization;
    private String     utilizationLabel;
    private String     utilizationStatus;

    // ── Section 6: Health sub-scores ──────────────────────────────
    private Integer    overallHealthScore;
    private String     overallHealthLabel;
    private RiskLevel  riskLevel;
    private String     riskLabel;
    private Integer    debtBurdenScore;
    private Integer    savingsRateScore;
    private Integer    creditScoreComponent;
    private Integer    creditUtilizationScore;
    private Integer    scoreTrend;
    private String     scoreTrendLabel;
    private List<ScoreTrendPoint> scoreTrendHistory;

    // ── Section 7: Activity summary ───────────────────────────────
    private Long   totalLoanChecks;
    private Long   totalCardChecks;
    private Long   totalFraudChecks;
    private Long   totalAffiliateClicks;
    private Long   unreadNotifications;
    private String lastActivityLabel;

    // ── Section 8: Smart alerts ────────────────────────────────────
    private List<FinancialAlert> alerts;

    // ── Section 9: Product readiness ──────────────────────────────
    private String loanReadiness;
    private String loanReadinessLabel;
    private String cardUpgradeReady;
    private String cardUpgradeLabel;

    // ── Inner: EMI summary ─────────────────────────────────────────
    @Getter @Builder
    public static class EmiSummary {
        private String     loanName;
        private String     lenderName;
        private BigDecimal emiAmount;
        private Integer    dueDateOfMonth;
        private String     dueDateLabel;
        private Boolean    isDueSoon;
        private Integer    remainingEmis;
        private LocalDate  endDate;
    }

    // ── Inner: Score trend point ───────────────────────────────────
    @Getter @Builder
    public static class ScoreTrendPoint {
        private LocalDate scoredOn;
        private Integer   score;
        private String    riskLevel;
    }

    // ── Inner: Financial alert ─────────────────────────────────────
    @Getter @Builder
    public static class FinancialAlert {
        private String severity;
        private String icon;
        private String title;
        private String message;
        private String action;
    }
}