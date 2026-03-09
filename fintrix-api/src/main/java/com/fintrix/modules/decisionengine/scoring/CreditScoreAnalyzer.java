package com.fintrix.modules.decisionengine.scoring;

import org.springframework.stereotype.Component;

/**
 * CreditScoreAnalyzer
 *
 * Converts CIBIL score (300–900) into a 0–100 sub-score.
 * Weight in overall score = 25%.
 *
 * Linear mapping within bands:
 *  300–549 → 0–20   (poor)
 *  550–649 → 20–45  (below average)
 *  650–699 → 45–65  (fair)
 *  700–749 → 65–80  (good)
 *  750–799 → 80–92  (very good)
 *  800–900 → 92–100 (excellent)
 */
@Component
public class CreditScoreAnalyzer {

    public AnalyzerResult analyze(Integer creditScore) {

        if (creditScore == null) {
            return new AnalyzerResult(40,
                    "Credit score not provided",
                    "Check your free CIBIL score at cibil.com or via " +
                    "your bank app. Knowing your score is step one.");
        }

        if (creditScore < 550) return new AnalyzerResult(
                linearMap(creditScore, 300, 549, 0, 20),
                "Poor credit score: " + creditScore,
                "Focus on: (1) Pay ALL EMIs on time. " +
                "(2) Don't apply for new credit. " +
                "(3) Reduce credit card utilization below 30%. " +
                "Expect 6–12 months to see significant improvement.");

        if (creditScore < 650) return new AnalyzerResult(
                linearMap(creditScore, 550, 649, 20, 45),
                "Below average credit score: " + creditScore,
                "Key improvement: Ensure zero late payments for " +
                "6+ months. Check your CIBIL report for errors — " +
                "dispute any incorrect entries.");

        if (creditScore < 700) return new AnalyzerResult(
                linearMap(creditScore, 650, 699, 45, 65),
                "Fair credit score: " + creditScore,
                "You are close to the 700 threshold. " +
                "Reduce utilization and maintain timely payments. " +
                "3–4 months of consistent behavior should push you past 700.");

        if (creditScore < 750) return new AnalyzerResult(
                linearMap(creditScore, 700, 749, 65, 80),
                "Good credit score: " + creditScore,
                "Good score. Push to 750+ for best loan rates. " +
                "Maintain low utilization and mix of credit types.");

        if (creditScore < 800) return new AnalyzerResult(
                linearMap(creditScore, 750, 799, 80, 92),
                "Very good credit score: " + creditScore, null);

        return new AnalyzerResult(
                linearMap(creditScore, 800, 900, 92, 100),
                "Excellent credit score: " + creditScore, null);
    }

    // Linear interpolation within a band
    private int linearMap(int value, int inMin, int inMax,
                           int outMin, int outMax) {
        double ratio = (double)(value - inMin) / (inMax - inMin);
        return (int)(outMin + ratio * (outMax - outMin));
    }

    public record AnalyzerResult(
            int    score,
            String observation,
            String tip
    ) {}
}
