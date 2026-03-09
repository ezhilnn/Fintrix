// ================================================================
// FILE 1: DebtToIncomeAnalyzer.java
// Analyzes FOIR and assigns a sub-score (0-100)
// ================================================================
package com.fintrix.modules.decisionengine.scoring;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * DebtToIncomeAnalyzer
 *
 * Converts FOIR (Fixed Obligation to Income Ratio) into
 * a 0–100 sub-score. Weight in overall score = 30%.
 *
 * Scoring bands (based on Indian bank underwriting standards):
 *
 *  FOIR < 10%  → score 100  (almost no debt burden)
 *  FOIR 10-20% → score 90   (very healthy)
 *  FOIR 20-30% → score 75   (healthy)
 *  FOIR 30-40% → score 60   (moderate — watch it)
 *  FOIR 40-50% → score 40   (high — nearing bank limits)
 *  FOIR 50-60% → score 20   (critical — most banks reject)
 *  FOIR > 60%  → score 5    (over-leveraged)
 *
 * Real-world insight:
 *  Indian banks have an internal FOIR cutoff of 50-55%.
 *  Beyond that, even a high credit score won't help.
 *  This analyzer surfaces that risk before the user applies.
 */
@Component
public class DebtToIncomeAnalyzer {

    public AnalyzerResult analyze(BigDecimal foir) {

        if (foir == null) {
            return new AnalyzerResult(50, "FOIR unknown",
                    "Update your EMI details for accurate debt analysis.");
        }

        double f = foir.doubleValue();

        if (f < 10)  return new AnalyzerResult(100,
                "Excellent debt management — FOIR below 10%", null);

        if (f < 20)  return new AnalyzerResult(90,
                "Very healthy FOIR of " + f + "%", null);

        if (f < 30)  return new AnalyzerResult(75,
                "Healthy FOIR of " + f + "%", null);

        if (f < 40)  return new AnalyzerResult(60,
                "Moderate FOIR of " + f + "% — monitor your debt",
                "Avoid taking new EMIs. Pay down existing loans " +
                "to improve your FOIR below 30%.");

        if (f < 50)  return new AnalyzerResult(40,
                "High FOIR of " + f + "% — approaching bank limits",
                "Your debt is high. Close at least one loan before " +
                "applying for new credit. Banks prefer FOIR under 50%.");

        if (f < 60)  return new AnalyzerResult(20,
                "Critical FOIR of " + f + "% — most banks will reject",
                "Urgent: Pay down EMIs aggressively. Avoid all new " +
                "credit applications until FOIR drops below 50%.");

        return new AnalyzerResult(5,
                "Severe over-leverage — FOIR " + f + "%",
                "You are severely over-leveraged. Consider loan " +
                "restructuring or consulting a financial advisor.");
    }

    public record AnalyzerResult(
            int    score,
            String observation,
            String tip
    ) {}
}

