package com.fintrix.modules.bff.dto;

import com.fintrix.modules.decisionengine.dto.FinancialHealthResponse;
import com.fintrix.modules.financialprofile.dto.FinancialProfileResponse;
import com.fintrix.modules.user.dto.UserProfileResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardResponse {
    private UserProfileResponse       userProfile;
    private FinancialProfileResponse  financialProfile;
    private FinancialHealthResponse   healthScore;

    // NEW — enriched metrics with labels, statuses and alerts
    // This is what the frontend should use for the dashboard cards
    private DashboardFinancialMetrics financialMetrics;

    private Boolean isProfileComplete;
    private Boolean isFinancialProfileComplete;
    private String  nextActionPrompt;
}