
// ================================================================
// FILE 2: DashboardAggregatorService.java
// ================================================================
package com.fintrix.modules.bff.service;

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

/**
 * DashboardAggregatorService
 *
 * Calls multiple module services and combines into one response.
 * Each service call is independent — if one fails, we handle gracefully.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardAggregatorService {

    private final UserService            userService;
    private final FinancialProfileService profileService;
    private final FinancialHealthService  healthService;

    public DashboardResponse buildDashboard(String userId) {
        log.debug("Building dashboard for userId: {}", userId);

        // ── Load user profile ─────────────────────────────────
        UserProfileResponse userProfile =
                userService.getMyProfile(userId);

        // ── Load financial profile (may not exist yet) ────────
        FinancialProfileResponse financialProfile = null;
        boolean financialProfileComplete = false;
        try {
            financialProfile = profileService.getProfile(userId);
            financialProfileComplete = Boolean.TRUE.equals(
                    financialProfile.getIsComplete());
        } catch (Exception e) {
            log.debug("No financial profile for userId: {}", userId);
        }

        // ── Load health score (only if financial profile exists) ─
        FinancialHealthResponse healthScore = null;
        if (financialProfileComplete) {
            try {
                healthScore = healthService.getLatestScore(userId);
            } catch (Exception e) {
                // Score not computed yet — not an error
                log.debug("No health score yet for userId: {}", userId);
            }
        }

        // ── Determine next action prompt ──────────────────────
        String nextAction = determineNextAction(
                userProfile, financialProfile, healthScore);

        return DashboardResponse.builder()
                .userProfile(userProfile)
                .financialProfile(financialProfile)
                .healthScore(healthScore)
                .isProfileComplete(userProfile.getIsProfileComplete())
                .isFinancialProfileComplete(financialProfileComplete)
                .nextActionPrompt(nextAction)
                .build();
    }

    private String determineNextAction(
            UserProfileResponse user,
            FinancialProfileResponse fp,
            FinancialHealthResponse hs) {

        if (!Boolean.TRUE.equals(user.getIsProfileComplete()))
            return "Complete your basic profile to get personalised " +
                   "financial recommendations";

        if (fp == null)
            return "Set up your financial profile to check loan " +
                   "eligibility and get credit card recommendations";

        if (!Boolean.TRUE.equals(fp.getIsComplete()))
            return "Add your credit score and EMI details " +
                   "for complete financial health analysis";

        if (hs == null)
            return "Calculate your Financial Health Score to see " +
                   "personalised improvement tips";

        if (hs.getOverallScore() < 50)
            return "Your financial health needs attention. " +
                   "Check your improvement tips below";

        return "Your finances look healthy! Check loan eligibility " +
               "or explore credit card recommendations";
    }
}