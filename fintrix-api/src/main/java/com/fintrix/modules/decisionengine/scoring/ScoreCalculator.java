package com.fintrix.modules.decisionengine.scoring;

import com.fintrix.modules.financialprofile.domain.FinancialProfile;
import com.fintrix.modules.financialprofile.domain.RiskLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ScoreCalculator
 *
 * Combines all 4 analyzers into a single Financial Health Score.
 *
 * Weighted formula:
 *  Overall = (debtScore    × 0.30)
 *           + (savingsScore × 0.25)
 *           + (creditScore  × 0.25)
 *           + (utilization  × 0.20)
 *
 * Why these weights?
 *  Debt burden (30%)    → #1 cause of financial distress in India
 *  Savings rate (25%)   → indicator of future financial security
 *  Credit score (25%)   → access to formal credit system
 *  Utilization (20%)    → short-term credit behaviour signal
 *
 * This is a deterministic weighted scoring model —
 * similar to how CIBIL internally weights their 5 factors.
 * Our model is simplified but teaches the core concept.
 *
 * ScoreResult:
 *  Contains all sub-scores, overall score, risk level,
 *  compiled improvement tips and risk warnings.
 *  Passed directly to FinancialHealthScore entity for persistence.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScoreCalculator {

    private final DebtToIncomeAnalyzer      debtAnalyzer;
    private final SavingsRateAnalyzer       savingsAnalyzer;
    private final CreditScoreAnalyzer       creditAnalyzer;
    private final CreditUtilizationAnalyzer utilizationAnalyzer;

    // Weights must sum to 1.0
    private static final double WEIGHT_DEBT        = 0.30;
    private static final double WEIGHT_SAVINGS     = 0.25;
    private static final double WEIGHT_CREDIT      = 0.25;
    private static final double WEIGHT_UTILIZATION = 0.20;

    public ScoreResult calculate(FinancialProfile profile) {

        log.debug("Calculating financial health score for userId: {}",
                profile.getUserId());

        // ── Run all 4 analyzers ───────────────────────────────
        DebtToIncomeAnalyzer.AnalyzerResult debtResult =
                debtAnalyzer.analyze(profile.getFoir());

        SavingsRateAnalyzer.AnalyzerResult savingsResult =
                savingsAnalyzer.analyze(
                        profile.getMonthlyIncome(),
                        profile.getMonthlySavings());

        CreditScoreAnalyzer.AnalyzerResult creditResult =
                creditAnalyzer.analyze(profile.getCreditScore());

        CreditUtilizationAnalyzer.AnalyzerResult utilResult =
                utilizationAnalyzer.analyze(
                        profile.getCurrentCreditUtilization(),
                        profile.getNumberOfCreditCards());

        // ── Weighted composite score ──────────────────────────
        double weightedScore =
                (debtResult.score()    * WEIGHT_DEBT)
              + (savingsResult.score() * WEIGHT_SAVINGS)
              + (creditResult.score()  * WEIGHT_CREDIT)
              + (utilResult.score()    * WEIGHT_UTILIZATION);

        int overallScore = (int) Math.round(weightedScore);

        // ── Compile tips and warnings ─────────────────────────
        List<String> tips     = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        addIfNotNull(tips,     debtResult.tip());
        addIfNotNull(tips,     savingsResult.tip());
        addIfNotNull(tips,     creditResult.tip());
        addIfNotNull(tips,     utilResult.tip());

        // Generate risk warnings from score thresholds
        buildWarnings(overallScore, profile, warnings);

        // ── Determine risk level ──────────────────────────────
        RiskLevel riskLevel = computeRiskLevel(overallScore);

        log.info("Score calculated for userId: {} → score: {} risk: {}",
                profile.getUserId(), overallScore, riskLevel);

        return new ScoreResult(
                overallScore,
                debtResult.score(),
                savingsResult.score(),
                creditResult.score(),
                utilResult.score(),
                riskLevel,
                tips,
                warnings,
                LocalDate.now()
        );
    }

    // ── Risk level from overall score ─────────────────────────
    private RiskLevel computeRiskLevel(int score) {
        if (score >= 75) return RiskLevel.LOW;
        if (score >= 50) return RiskLevel.MEDIUM;
        if (score >= 25) return RiskLevel.HIGH;
        return RiskLevel.CRITICAL;
    }

    // ── Generate warnings for dashboard ──────────────────────
    private void buildWarnings(int score,
                                FinancialProfile profile,
                                List<String> warnings) {
        if (score < 40) {
            warnings.add("⚠️ Your financial health is critical. " +
                         "Avoid all new loan applications.");
        }
        if (profile.getFoir() != null &&
                profile.getFoir().doubleValue() > 50) {
            warnings.add("⚠️ FOIR above 50% — most lenders will reject " +
                         "your loan applications.");
        }
        if (profile.getCreditScore() != null &&
                profile.getCreditScore() < 650) {
            warnings.add("⚠️ Credit score below 650 — you are likely to " +
                         "be rejected for unsecured loans.");
        }
        if (profile.getCurrentCreditUtilization() != null &&
                profile.getCurrentCreditUtilization().doubleValue() > 75) {
            warnings.add("⚠️ Credit utilization above 75% is actively " +
                         "damaging your CIBIL score every month.");
        }
    }

    private void addIfNotNull(List<String> list, String value) {
        if (value != null && !value.isBlank()) list.add(value);
    }

    // ── Result record returned by calculate() ─────────────────
    public record ScoreResult(
            int         overallScore,
            int         debtBurdenScore,
            int         savingsRateScore,
            int         creditScoreComponent,
            int         creditUtilizationScore,
            RiskLevel   riskLevel,
            List<String> improvementTips,
            List<String> riskWarnings,
            LocalDate   scoredOn
    ) {}
}