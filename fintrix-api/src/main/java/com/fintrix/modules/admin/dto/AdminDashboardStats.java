// ================================================================
// FILE 5: AdminDashboardStats.java + AdminStatsService
// ================================================================
package com.fintrix.modules.admin.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Getter @Builder
public class AdminDashboardStats {
    private Long       totalUsers;
    private Long       activeUsers;
    private Long       totalLoanChecks;
    private Long       totalCardChecks;
    private Long       totalFraudChecks;
    private Long       totalAffiliateClicks;
    private Long       totalConversions;
    private BigDecimal estimatedRevenue;
    private Long       totalNotificationsSent;
    private Long       pendingConsentUsers;     // users without DATA_PROCESSING consent
}