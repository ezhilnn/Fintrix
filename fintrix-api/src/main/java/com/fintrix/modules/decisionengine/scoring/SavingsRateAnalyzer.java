
// ================================================================
// FILE 3: SavingsRateAnalyzer.java
// Analyzes monthly savings rate and assigns a sub-score (0-100)
// ================================================================
package com.fintrix.modules.decisionengine.scoring;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * SavingsRateAnalyzer
 *
 * Savings Rate = (Monthly Savings / Monthly Income) × 100
 *
 * Weight in overall score = 25%.
 *
 * The 50/30/20 rule (popular personal finance rule):
 *  50% → needs (rent, food, EMIs)
 *  30% → wants (entertainment, dining out)
 *  20% → savings and investments
 *
 * Indian household average savings rate: ~10-15%.
 * Financial advisors recommend minimum 20%.
 *
 * Scoring bands:
 *  > 40%   → score 100  (exceptional saver)
 *  30-40%  → score 90   (excellent)
 *  20-30%  → score 75   (good — meets 50/30/20 rule)
 *  10-20%  → score 55   (below recommended)
 *  5-10%   → score 30   (low — financial vulnerability)
 *  0-5%    → score 15   (very low — one emergency away from debt)
 *  < 0     → score 0    (spending more than earning — deficit)
 */
@Component
public class SavingsRateAnalyzer {

    public AnalyzerResult analyze(BigDecimal monthlyIncome,
                                  BigDecimal monthlySavings) {

        if (monthlyIncome == null ||
                monthlyIncome.compareTo(BigDecimal.ZERO) == 0) {
            return new AnalyzerResult(0,
                    "Income data missing", "Enter your monthly income.");
        }

        BigDecimal savings = monthlySavings != null
                ? monthlySavings : BigDecimal.ZERO;

        // Savings rate percentage
        double savingsRate = savings
                .divide(monthlyIncome, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();

        if (savingsRate < 0) return new AnalyzerResult(0,
                "You are spending more than you earn — monthly deficit",
                "Track every expense. Identify and cut non-essential " +
                "spending immediately. Create an emergency budget.");

        if (savingsRate < 5)  return new AnalyzerResult(15,
                String.format("Very low savings rate of %.1f%%", savingsRate),
                "Try to save at least ₹" +
                monthlyIncome.multiply(BigDecimal.valueOf(0.10))
                        .setScale(0, RoundingMode.HALF_UP) +
                " (10% of income) each month.");

        if (savingsRate < 10) return new AnalyzerResult(30,
                String.format("Low savings rate of %.1f%%", savingsRate),
                "Target 20% savings rate (50/30/20 rule). " +
                "Set up an automatic SIP to enforce savings discipline.");

        if (savingsRate < 20) return new AnalyzerResult(55,
                String.format("Below recommended savings rate of %.1f%%",
                        savingsRate),
                "You are saving but below the recommended 20%. " +
                "Increase SIP by ₹500-1000/month each quarter.");

        if (savingsRate < 30) return new AnalyzerResult(75,
                String.format("Good savings rate of %.1f%%", savingsRate),
                null);

        if (savingsRate < 40) return new AnalyzerResult(90,
                String.format("Excellent savings rate of %.1f%%", savingsRate),
                null);

        return new AnalyzerResult(100,
                String.format("Exceptional savings rate of %.1f%%", savingsRate),
                null);
    }

    public record AnalyzerResult(
            int    score,
            String observation,
            String tip
    ) {}
}

