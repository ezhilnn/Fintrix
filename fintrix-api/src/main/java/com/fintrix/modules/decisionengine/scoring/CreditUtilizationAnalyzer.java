// ================================================================
// FILE 2: CreditUtilizationAnalyzer.java
// Analyzes credit card utilization and assigns a sub-score (0-100)
// ================================================================
package com.fintrix.modules.decisionengine.scoring;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * CreditUtilizationAnalyzer
 *
 * Credit utilization = (Used Credit / Total Credit Limit) × 100
 *
 * Weight in overall score = 20%.
 *
 * Rule: Keep utilization BELOW 30% for a good CIBIL score.
 * CIBIL scores drop significantly above 50% utilization.
 *
 * Scoring bands:
 *  0–10%   → score 100  (ideal)
 *  10–20%  → score 90   (excellent)
 *  20–30%  → score 75   (good — ideal upper range)
 *  30–50%  → score 50   (moderate — CIBIL impact begins)
 *  50–75%  → score 25   (high — CIBIL score dropping)
 *  75–100% → score 5    (maxed out — serious risk signal)
 *
 * Special case:
 *  No credit cards → score 60 (neutral, not penalised)
 *  This is India-specific — many people have no credit history.
 */
@Component
public class CreditUtilizationAnalyzer {

    public AnalyzerResult analyze(BigDecimal utilization,
                                  Integer numberOfCards) {

        // No credit cards at all
        if (numberOfCards == null || numberOfCards == 0) {
            return new AnalyzerResult(60,
                    "No credit cards — utilization not applicable",
                    "Consider getting a secured credit card (against FD) " +
                    "to build credit history. This improves your CIBIL score.");
        }

        if (utilization == null) {
            return new AnalyzerResult(50,
                    "Utilization data missing",
                    "Enter your credit card utilization for accurate scoring.");
        }

        double u = utilization.doubleValue();

        if (u <= 10)  return new AnalyzerResult(100,
                "Ideal utilization at " + u + "%", null);

        if (u <= 20)  return new AnalyzerResult(90,
                "Excellent utilization at " + u + "%", null);

        if (u <= 30)  return new AnalyzerResult(75,
                "Good utilization at " + u + "%",
                "Keep utilization below 30% to maintain your CIBIL score.");

        if (u <= 50)  return new AnalyzerResult(50,
                "Moderate utilization at " + u + "% — CIBIL impact starting",
                "Pay down outstanding balance to bring utilization " +
                "below 30%. Aim for 20% for best CIBIL impact.");

        if (u <= 75)  return new AnalyzerResult(25,
                "High utilization at " + u + "% — CIBIL score dropping",
                "Your high utilization is actively hurting your CIBIL score. " +
                "Pay outstanding balance immediately or request a " +
                "credit limit increase from your bank.");

        return new AnalyzerResult(5,
                "Maxed out utilization at " + u + "% — serious risk",
                "You have used almost all available credit. This is a " +
                "major red flag for lenders. Pay down immediately.");
    }

    public record AnalyzerResult(
            int    score,
            String observation,
            String tip
    ) {}
}

