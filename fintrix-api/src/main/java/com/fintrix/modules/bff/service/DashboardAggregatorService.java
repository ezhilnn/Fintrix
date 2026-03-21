package com.fintrix.modules.bff.service;

import com.fintrix.modules.bff.dto.DashboardFinancialMetrics;
import com.fintrix.modules.bff.dto.DashboardResponse;
import com.fintrix.modules.decisionengine.dto.FinancialHealthResponse;
import com.fintrix.modules.decisionengine.service.FinancialHealthService;
import com.fintrix.modules.financialprofile.dto.FinancialProfileResponse;
import com.fintrix.modules.financialprofile.service.FinancialProfileService;
import com.fintrix.modules.user.dto.UserProfileResponse;
import com.fintrix.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardAggregatorService {

    private final UserService             userService;
    private final FinancialProfileService profileService;
    private final FinancialHealthService  healthService;
    private final FinancialMetricsBuilder metricsBuilder;

    public DashboardResponse buildDashboard(String userId) {
        log.debug("Building dashboard for userId: {}", userId);

        UserProfileResponse userProfile = userService.getMyProfile(userId);

        FinancialProfileResponse financialProfile = null;
        boolean financialProfileComplete = false;
        try {
            financialProfile = profileService.getProfile(userId);
            financialProfileComplete = Boolean.TRUE.equals(
                    financialProfile.getIsComplete());
        } catch (Exception e) {
            log.debug("No financial profile for userId: {}", userId);
        }

        FinancialHealthResponse healthScore = null;
        if (financialProfileComplete) {
            try {
                healthScore = healthService.getLatestScore(userId);
            } catch (Exception e) {
                log.debug("No health score yet for userId: {}", userId);
            }
        }

        // Pass userId so builder can query emi_trackers,
        // notifications, audit_logs, affiliate_clicks
        DashboardFinancialMetrics metrics =
                metricsBuilder.build(financialProfile, healthScore, userId);

        return DashboardResponse.builder()
                .userProfile(userProfile)
                .financialProfile(financialProfile)
                .healthScore(healthScore)
                .financialMetrics(metrics)
                .isProfileComplete(userProfile.getIsProfileComplete())
                .isFinancialProfileComplete(financialProfileComplete)
                .nextActionPrompt(determineNextAction(
                        userProfile, financialProfile, healthScore, metrics))
                .build();
    }

    private String determineNextAction(
            UserProfileResponse      userProfile,
            FinancialProfileResponse fp,
            FinancialHealthResponse  hs,
            DashboardFinancialMetrics metrics) {

        if (!Boolean.TRUE.equals(userProfile.getIsProfileComplete()))
            return "Complete your basic profile to get personalised recommendations";

        if (fp == null)
            return "Set up your financial profile to unlock all features";

        if (!Boolean.TRUE.equals(fp.getIsComplete()))
            return "Add your credit score and EMI details for complete analysis";

        if (hs == null)
            return "Compute your Financial Health Score to see improvement tips";

        if (metrics != null && metrics.getAlerts() != null
                && !metrics.getAlerts().isEmpty()) {
            var top = metrics.getAlerts().get(0);
            if ("DANGER".equals(top.getSeverity()))
                return "⚠️ " + top.getTitle() + " — " + top.getAction();
        }

        if (hs.getOverallScore() < 50)
            return "Your financial health needs attention — check tips below";

        return "Your finances look healthy! Explore loan or card options";
    }
}