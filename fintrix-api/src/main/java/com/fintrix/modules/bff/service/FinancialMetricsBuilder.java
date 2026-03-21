package com.fintrix.modules.bff.service;

import com.fintrix.modules.audit.repository.DecisionAuditRepository;
import com.fintrix.modules.bff.dto.DashboardFinancialMetrics;
import com.fintrix.modules.bff.dto.DashboardFinancialMetrics.EmiSummary;
import com.fintrix.modules.bff.dto.DashboardFinancialMetrics.FinancialAlert;
import com.fintrix.modules.bff.dto.DashboardFinancialMetrics.ScoreTrendPoint;
import com.fintrix.modules.decisionengine.dto.FinancialHealthResponse;
import com.fintrix.modules.emi.repository.EmiTrackerRepository;
import com.fintrix.modules.financialprofile.dto.FinancialProfileResponse;
import com.fintrix.modules.notification.repository.NotificationRepository;
import com.fintrix.modules.tracking.repository.AffiliateClickRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinancialMetricsBuilder {

    private final EmiTrackerRepository     emiTrackerRepository;
    private final DecisionAuditRepository  auditRepository;
    private final AffiliateClickRepository affiliateClickRepository;
    private final NotificationRepository   notificationRepository;

    public DashboardFinancialMetrics build(
            FinancialProfileResponse profile,
            FinancialHealthResponse  health,
            String userId) {

        if (profile == null) return null;

        // ── Raw values ─────────────────────────────────────────
        BigDecimal income    = safe(profile.getMonthlyIncome());
        BigDecimal expenses  = safe(profile.getMonthlyExpenses());
        BigDecimal emi       = safe(profile.getExistingEmiTotal());
        BigDecimal savings   = safe(profile.getMonthlySavings());
        BigDecimal disposable = income.subtract(expenses).subtract(emi);
        double foirD = safe(profile.getFoir()).doubleValue();
        double dtiD  = safe(profile.getDti()).doubleValue();
        double util  = profile.getCurrentCreditUtilization() != null
                ? profile.getCurrentCreditUtilization().doubleValue() : 0;

        int savingsRatePct = 0;
        if (income.compareTo(BigDecimal.ZERO) > 0) {
            savingsRatePct = savings
                    .divide(income, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .intValue();
        }

        BigDecimal maxFoirEmi = income.multiply(BigDecimal.valueOf(0.50));
        BigDecimal additionalCapacity =
                maxFoirEmi.subtract(emi).max(BigDecimal.ZERO)
                          .setScale(0, RoundingMode.HALF_UP);

        // ── EMI tracker ────────────────────────────────────────
        var trackedEmis = emiTrackerRepository
                .findByUserIdAndIsActiveTrueOrderByDueDateOfMonthAsc(userId);

        BigDecimal totalEmiCommitment = trackedEmis.stream()
                .map(e -> safe(e.getEmiAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int today = LocalDate.now().getDayOfMonth();

        List<EmiSummary> upcomingEmis = trackedEmis.stream()
                .limit(5)
                .map(e -> {
                    int due = e.getDueDateOfMonth();
                    int daysUntil = due >= today ? due - today : (30 - today + due);
                    boolean dueSoon = daysUntil <= 3;
                    return EmiSummary.builder()
                            .loanName(e.getLoanName())
                            .lenderName(e.getLenderName())
                            .emiAmount(e.getEmiAmount())
                            .dueDateOfMonth(due)
                            .dueDateLabel(dueSoon
                                    ? "Due on " + due + ordinal(due) + " — in " + daysUntil + " day(s)"
                                    : "Due on " + due + ordinal(due) + " of every month")
                            .isDueSoon(dueSoon)
                            .remainingEmis(e.getRemainingEmis())
                            .endDate(e.getEndDate())
                            .build();
                })
                .collect(Collectors.toList());

        long emisDueSoon = upcomingEmis.stream()
                .filter(e -> Boolean.TRUE.equals(e.getIsDueSoon()))
                .count();

        // ── Activity ───────────────────────────────────────────
        long loanChecks  = countUserDecisions(userId, "LOAN_ELIGIBILITY");
        long cardChecks  = countUserDecisions(userId, "CARD_RECOMMENDATION");
        long fraudChecks = countUserDecisions(userId, "FRAUD_CHECK");
        long affClicks   = affiliateClickRepository.countByUserId(userId);
        long unreadNotifs = notificationRepository
                .countByUserIdAndIsReadFalse(userId);

        // ── Score trend ────────────────────────────────────────
        int scoreTrend = 0;
        String scoreTrendLabel = "No previous score to compare";
        List<ScoreTrendPoint> trendHistory = new ArrayList<>();

        if (health != null && health.getScoreTrend() != null) {
            trendHistory = health.getScoreTrend().stream()
                    .map(t -> ScoreTrendPoint.builder()
                            .scoredOn(t.getScoredOn())
                            .score(t.getScore())
                            .riskLevel(t.getRiskLevel() != null
                                    ? t.getRiskLevel().name() : "")
                            .build())
                    .collect(Collectors.toList());

            if (trendHistory.size() >= 2) {
                int latest = trendHistory.get(0).getScore();
                int prev   = trendHistory.get(1).getScore();
                scoreTrend = latest - prev;
                scoreTrendLabel = scoreTrend > 0
                        ? "↑ Up " + scoreTrend + " points since last calculation"
                        : scoreTrend < 0
                        ? "↓ Down " + Math.abs(scoreTrend) + " points since last calculation"
                        : "→ No change since last calculation";
            }
        }

        // ── Alerts ─────────────────────────────────────────────
        List<FinancialAlert> alerts = buildAlerts(
                profile, health, (int) emisDueSoon, dtiD);

        // ── Readiness ──────────────────────────────────────────
        String loanReadiness    = computeLoanReadiness(profile);
        String cardUpgradeReady = computeCardUpgradeReadiness(profile);

        return DashboardFinancialMetrics.builder()

                // Employment
                .employmentType(profile.getEmploymentType())
                .employmentTypeLabel(employmentLabel(profile))
                .employerName(profile.getEmployerName())
                .yearsOfExperience(profile.getYearsOfExperience())
                .experienceLabel(experienceLabel(profile.getYearsOfExperience()))

                // Income & cash flow
                .monthlyIncome(income)
                .monthlyExpenses(expenses)
                .existingEmiTotal(emi)
                .monthlySavings(savings)
                .disposableIncome(disposable)
                .savingsRatePercent(savingsRatePct)
                .savingsRateLabel(savingsRateLabel(savingsRatePct))
                .cashFlowStatus(cashFlowStatus(disposable, income))

                // Debt profile
                .foir(safe(profile.getFoir()))
                .foirLabel(foirLabel(foirD))
                .foirStatus(foirStatus(foirD))
                .dti(safe(profile.getDti()))
                .dtiRange(profile.getDtiRange())
                .dtiLabel(dtiLabel(dtiD))
                .dtiVsFoirInsight(dtiVsFoirInsight(foirD, dtiD))
                .numberOfActiveLoans(profile.getNumberOfActiveLoans() != null
                        ? profile.getNumberOfActiveLoans() : 0)
                .loanBurdenLabel(loanBurdenLabel(profile.getNumberOfActiveLoans()))
                .maxAdditionalEmiCapacity(additionalCapacity)
                .additionalEmiLabel(additionalEmiLabel(additionalCapacity))

                // EMI tracker
                .totalTrackedEmis(trackedEmis.size())
                .totalMonthlyEmiCommitment(totalEmiCommitment)
                .emisDueSoon((int) emisDueSoon)
                .upcomingEmis(upcomingEmis)

                // Credit
                .creditScore(profile.getCreditScore())
                .creditScoreRange(profile.getCreditScoreRange())
                .creditScoreLabel(creditScoreLabel(profile.getCreditScore()))
                .creditScoreTip(creditScoreTip(profile.getCreditScore()))
                .numberOfCreditCards(profile.getNumberOfCreditCards() != null
                        ? profile.getNumberOfCreditCards() : 0)
                .totalCreditLimit(safe(profile.getTotalCreditLimit()))
                .currentCreditUtilization(safe(profile.getCurrentCreditUtilization()))
                .utilizationLabel(utilizationLabel(util))
                .utilizationStatus(utilizationStatus(util))

                // Health scores
                .overallHealthScore(health != null ? health.getOverallScore() : null)
                .overallHealthLabel(health != null ? health.getRiskLabel() : "Not computed yet")
                .riskLevel(health != null ? health.getRiskLevel() : null)
                .riskLabel(health != null ? riskBadge(health.getRiskLevel().name()) : "—")
                .debtBurdenScore(health != null ? health.getDebtBurdenScore() : null)
                .savingsRateScore(health != null ? health.getSavingsRateScore() : null)
                .creditScoreComponent(health != null ? health.getCreditScoreComponent() : null)
                .creditUtilizationScore(health != null ? health.getUtilizationScore() : null)
                .scoreTrend(scoreTrend)
                .scoreTrendLabel(scoreTrendLabel)
                .scoreTrendHistory(trendHistory)

                // Activity
                .totalLoanChecks(loanChecks)
                .totalCardChecks(cardChecks)
                .totalFraudChecks(fraudChecks)
                .totalAffiliateClicks(affClicks)
                .unreadNotifications(unreadNotifs)
                .lastActivityLabel(buildLastActivityLabel(loanChecks, cardChecks, fraudChecks))

                // Alerts
                .alerts(alerts)

                // Readiness
                .loanReadiness(loanReadiness)
                .loanReadinessLabel(loanReadinessLabel(loanReadiness))
                .cardUpgradeReady(cardUpgradeReady)
                .cardUpgradeLabel(cardUpgradeLabel(cardUpgradeReady, profile))

                .build();
    }

    // ── Alerts ────────────────────────────────────────────────
    private List<FinancialAlert> buildAlerts(
            FinancialProfileResponse profile,
            FinancialHealthResponse  health,
            int emisDueSoon,
            double dti) {

        var alerts = new ArrayList<FinancialAlert>();
        double foir = safe(profile.getFoir()).doubleValue();
        double util = profile.getCurrentCreditUtilization() != null
                ? profile.getCurrentCreditUtilization().doubleValue() : 0;
        Integer score   = profile.getCreditScore();
        BigDecimal income  = safe(profile.getMonthlyIncome());
        BigDecimal savings = safe(profile.getMonthlySavings());

        // EMI due soon
        if (emisDueSoon > 0)
            alerts.add(alert("WARNING", "⏰",
                    emisDueSoon + " EMI(s) due within 3 days",
                    "Late payments directly hurt your CIBIL score.",
                    "Ensure funds are ready in your bank account."));

        // FOIR
        if (foir > 60)
            alerts.add(alert("DANGER", "🔴",
                    "Critical FOIR — " + String.format("%.1f", foir) + "%",
                    "Most lenders reject above 55% FOIR.",
                    "Close at least one loan before applying for new credit."));
        else if (foir > 50)
            alerts.add(alert("WARNING", "⚠️",
                    "High FOIR — " + String.format("%.1f", foir) + "%",
                    "Approaching the 50–55% limit most banks enforce.",
                    "Avoid new loans until FOIR drops below 50%."));

        // DTI — catches cases FOIR misses
        if (dti > 70 && foir < 30)
            alerts.add(alert("WARNING", "⚠️",
                    "High DTI despite low FOIR — " + String.format("%.1f", dti) + "%",
                    "Your EMIs are low but total expenses consume " +
                    String.format("%.1f", dti) + "% of income. " +
                    "High living costs leave little financial buffer.",
                    "Review recurring expenses — rent, subscriptions, utilities."));
        else if (dti > 50)
            alerts.add(alert("WARNING", "⚠️",
                    "High DTI — " + String.format("%.1f", dti) + "%",
                    "Over 50% of income goes to obligations. " +
                    "Financial planners consider this high risk.",
                    "Work on reducing fixed monthly obligations."));

        // Credit score
        if (score != null && score < 650)
            alerts.add(alert("DANGER", "🔴",
                    "Low credit score — " + score,
                    "Most lenders require minimum 650–700.",
                    "Pay all EMIs on time for 3–6 months to improve."));
        else if (score != null && score < 700)
            alerts.add(alert("WARNING", "⚠️",
                    "Borderline credit score — " + score,
                    "Just below the 700 threshold.",
                    "Reduce utilization and maintain timely payments."));

        // Utilization
        if (util > 75)
            alerts.add(alert("DANGER", "🔴",
                    "Credit utilization maxed — " + String.format("%.0f", util) + "%",
                    "Actively lowering your CIBIL score every month.",
                    "Pay down credit card balance immediately."));
        else if (util > 50)
            alerts.add(alert("WARNING", "⚠️",
                    "High utilization — " + String.format("%.0f", util) + "%",
                    "Above 30% negatively impacts your score.",
                    "Aim to keep utilization below 30%."));

        // Savings
        if (income.compareTo(BigDecimal.ZERO) > 0) {
            double sr = savings.divide(income, 4, RoundingMode.HALF_UP)
                    .doubleValue() * 100;
            if (sr < 0)
                alerts.add(alert("DANGER", "🔴",
                        "Monthly deficit",
                        "Expenses exceed income — you are in a deficit.",
                        "Cut non-essential spending immediately."));
            else if (sr < 5)
                alerts.add(alert("WARNING", "⚠️",
                        "Very low savings — " + String.format("%.0f", sr) + "% of income",
                        "One emergency could push you into debt.",
                        "Set up automatic SIP of at least 10% of income."));
        }

        // Health score warnings
        if (health != null && health.getRiskWarnings() != null)
            health.getRiskWarnings().stream().limit(2).forEach(w ->
                    alerts.add(alert("WARNING", "⚠️",
                            "Health score warning", w,
                            "Check your financial health score for details.")));

        // No credit score
        if (score == null)
            alerts.add(alert("INFO", "ℹ️",
                    "Credit score not entered",
                    "Cannot accurately predict loan or card eligibility.",
                    "Check free at cibil.com and update your profile."));

        // No cards — missed diversity
        int cards = profile.getNumberOfCreditCards() != null
                ? profile.getNumberOfCreditCards() : 0;
        if (cards == 0 && score != null && score >= 700)
            alerts.add(alert("INFO", "ℹ️",
                    "No credit cards — missed credit mix opportunity",
                    "Credit mix accounts for ~10% of your CIBIL score.",
                    "Consider a lifetime-free card to build credit history."));

        return alerts;
    }

    // ── DTI labels ─────────────────────────────────────────────
    private String dtiLabel(double dti) {
        if (dti < 30)  return String.format("Low — %.1f%% of income goes to obligations", dti);
        if (dti < 43)  return String.format("Moderate — %.1f%% — manageable but watch it", dti);
        if (dti < 50)  return String.format("High — %.1f%% — lenders will flag this", dti);
        return String.format("Critical — %.1f%% — exceeds safe threshold", dti);
    }

    private String dtiVsFoirInsight(double foir, double dti) {
        double gap = dti - foir;
        if (gap < 5)
            return "DTI and FOIR are close — your expenses are mostly EMI-driven.";
        if (gap < 20)
            return String.format(
                    "DTI (%.1f%%) is %.1f%% higher than FOIR (%.1f%%) — " +
                    "living expenses are a significant portion of your outflows.",
                    dti, gap, foir);
        return String.format(
                "⚠️ DTI (%.1f%%) is much higher than FOIR (%.1f%%) — " +
                "high living costs (rent, food, utilities) are the main burden. " +
                "Banks see a healthy FOIR but your actual disposable income is limited.",
                dti, foir);
    }

    // ── Helper methods ─────────────────────────────────────────
    private long countUserDecisions(String userId, String type) {
        try {
            return auditRepository
                    .findByUserIdAndDecisionTypeOrderByCreatedAtDesc(
                            userId, type, PageRequest.of(0, 1))
                    .getTotalElements();
        } catch (Exception e) { return 0L; }
    }

    private String buildLastActivityLabel(long loans, long cards, long fraud) {
        if (loans == 0 && cards == 0 && fraud == 0)
            return "No activity yet — start with a loan eligibility check";
        List<String> parts = new ArrayList<>();
        if (loans > 0) parts.add(loans + " loan check" + (loans > 1 ? "s" : ""));
        if (cards > 0) parts.add(cards + " card check" + (cards > 1 ? "s" : ""));
        if (fraud > 0) parts.add(fraud + " fraud check" + (fraud > 1 ? "s" : ""));
        return String.join(", ", parts) + " done so far";
    }

    private String employmentLabel(FinancialProfileResponse p) {
        if (p.getEmploymentType() == null) return "Not specified";
        return switch (p.getEmploymentType().name()) {
            case "SALARIED"      -> "Salaried Employee" +
                    (p.getEmployerName() != null ? " at " + p.getEmployerName() : "");
            case "SELF_EMPLOYED" -> "Self Employed";
            case "GOVERNMENT"    -> "Government Employee" +
                    (p.getEmployerName() != null ? " — " + p.getEmployerName() : "");
            case "PSU"           -> "PSU Employee";
            case "RETIRED"       -> "Retired";
            case "STUDENT"       -> "Student";
            default              -> p.getEmploymentType().name()
                    .replace("_", " ").toLowerCase();
        };
    }

    private String experienceLabel(Integer years) {
        if (years == null) return "Not specified";
        if (years == 0)    return "Less than 1 year";
        if (years < 2)     return years + " year — building stability";
        if (years < 5)     return years + " years — stable";
        return years + " years — highly stable";
    }

    private String savingsRateLabel(int rate) {
        if (rate < 0)  return "⚠️ Deficit — spending more than earning";
        if (rate < 5)  return "Very low — " + rate + "% saved";
        if (rate < 10) return "Low — " + rate + "% of income saved";
        if (rate < 20) return "Below recommended — " + rate + "% saved";
        if (rate < 30) return "Good — " + rate + "% of income saved";
        if (rate < 40) return "Excellent — " + rate + "% of income saved";
        return "Exceptional — " + rate + "% of income saved";
    }

    private String cashFlowStatus(BigDecimal disposable, BigDecimal income) {
        if (disposable.compareTo(BigDecimal.ZERO) < 0) return "NEGATIVE";
        if (income.compareTo(BigDecimal.ZERO) > 0) {
            double pct = disposable.divide(income, 4, RoundingMode.HALF_UP)
                    .doubleValue() * 100;
            if (pct < 10) return "TIGHT";
        }
        return "POSITIVE";
    }

    private String foirLabel(double foir) {
        if (foir < 20) return String.format("Excellent — %.1f%% of income", foir);
        if (foir < 30) return String.format("Healthy — %.1f%% of income", foir);
        if (foir < 40) return String.format("Moderate — %.1f%% of income", foir);
        if (foir < 50) return String.format("High — %.1f%% — approaching limits", foir);
        if (foir < 60) return String.format("Critical — %.1f%% — banks may reject", foir);
        return String.format("Over-leveraged — %.1f%%", foir);
    }

    private String foirStatus(double foir) {
        if (foir < 30) return "HEALTHY";
        if (foir < 40) return "MODERATE";
        if (foir < 55) return "HIGH";
        return "CRITICAL";
    }

    private String loanBurdenLabel(Integer loans) {
        if (loans == null || loans == 0) return "No active loans";
        return loans + " active loan" + (loans > 1 ? "s" : "");
    }

    private String additionalEmiLabel(BigDecimal capacity) {
        if (capacity.compareTo(BigDecimal.ZERO) <= 0)
            return "No EMI capacity — FOIR already at 50%";
        return "You can take up to ₹" + formatAmount(capacity) + "/month more in EMIs";
    }

    private String creditScoreLabel(Integer score) {
        if (score == null) return "Not entered — add for accurate results";
        if (score >= 800)  return score + " — Excellent — best rates guaranteed";
        if (score >= 750)  return score + " — Very Good — eligible for premium products";
        if (score >= 700)  return score + " — Good — eligible for most loans and cards";
        if (score >= 650)  return score + " — Fair — limited eligibility";
        if (score >= 600)  return score + " — Poor — mostly NBFCs only";
        return score + " — Very Poor — work on improvement first";
    }

    private String creditScoreTip(Integer score) {
        if (score == null) return "Check your free CIBIL score at cibil.com";
        if (score >= 800)  return "Excellent — negotiate for lowest rates";
        if (score >= 750)  return "Push to 800+ by keeping utilization below 20%";
        if (score >= 700)  return "Improve to 750+ for premium cards and best rates";
        if (score >= 650)  return "Focus on on-time payments for 3–4 months to cross 700";
        return "Pay all EMIs on time, reduce utilization below 30%";
    }

    private String utilizationLabel(double util) {
        if (util == 0)   return "0% — no credit card usage";
        if (util <= 10)  return String.format("%.0f%% — Ideal", util);
        if (util <= 30)  return String.format("%.0f%% — Healthy", util);
        if (util <= 50)  return String.format("%.0f%% — Moderate — CIBIL impact starting", util);
        if (util <= 75)  return String.format("%.0f%% — High — score dropping", util);
        return String.format("%.0f%% — Maxed out — urgent action needed", util);
    }

    private String utilizationStatus(double util) {
        if (util <= 30) return "HEALTHY";
        if (util <= 50) return "MODERATE";
        if (util <= 75) return "HIGH";
        return "MAXED";
    }

    private String riskBadge(String riskLevel) {
        return switch (riskLevel) {
            case "LOW"      -> "✅ Low Risk";
            case "MEDIUM"   -> "🟡 Moderate Risk";
            case "HIGH"     -> "🔴 High Risk";
            case "CRITICAL" -> "⛔ Critical";
            default         -> riskLevel;
        };
    }

    private String computeLoanReadiness(FinancialProfileResponse p) {
        double foir  = safe(p.getFoir()).doubleValue();
        double dti   = safe(p.getDti()).doubleValue();
        Integer score = p.getCreditScore();
        if (score == null) return "BORDERLINE";
        if (score >= 700 && foir < 50 && dti < 60) return "READY";
        if (score >= 650 && foir < 55)              return "BORDERLINE";
        return "NOT_READY";
    }

    private String loanReadinessLabel(String r) {
        return switch (r) {
            case "READY"     -> "✅ Ready — your profile supports loan approval";
            case "BORDERLINE"-> "🟡 Borderline — some lenders may approve";
            default          -> "🔴 Not ready — improve score and reduce FOIR first";
        };
    }

    private String computeCardUpgradeReadiness(FinancialProfileResponse p) {
        Integer score = p.getCreditScore();
        if (score == null) return "BORDERLINE";
        if (score >= 750)  return "READY";
        if (score >= 700)  return "BORDERLINE";
        return "NOT_READY";
    }

    private String cardUpgradeLabel(String r, FinancialProfileResponse p) {
        Integer score = p.getCreditScore();
        return switch (r) {
            case "READY"     -> "✅ Ready — eligible for premium credit cards";
            case "BORDERLINE"-> "🟡 Borderline — eligible for standard cards only";
            default          -> "🔴 Improve score by " +
                    (score != null ? (700 - score) + " points" : "checking first") +
                    " to unlock better cards";
        };
    }

    private String ordinal(int n) {
        if (n >= 11 && n <= 13) return "th";
        return switch (n % 10) {
            case 1  -> "st";
            case 2  -> "nd";
            case 3  -> "rd";
            default -> "th";
        };
    }

    private String formatAmount(BigDecimal amount) {
        double val = amount.doubleValue();
        if (val >= 100000) return String.format("%.1fL", val / 100000);
        if (val >= 1000)   return String.format("%.1fK", val / 1000);
        return String.format("%.0f", val);
    }

    private BigDecimal safe(BigDecimal val) {
        return val != null ? val : BigDecimal.ZERO;
    }

    private FinancialAlert alert(String severity, String icon,
            String title, String message, String action) {
        return FinancialAlert.builder()
                .severity(severity).icon(icon)
                .title(title).message(message).action(action)
                .build();
    }
}