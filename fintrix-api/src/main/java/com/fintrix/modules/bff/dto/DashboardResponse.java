// ================================================================
// FILE 1: DashboardResponse.java
// ================================================================
package com.fintrix.modules.bff.dto;

import com.fintrix.modules.decisionengine.dto.FinancialHealthResponse;
import com.fintrix.modules.financialprofile.dto.FinancialProfileResponse;
import com.fintrix.modules.user.dto.UserProfileResponse;
import lombok.Builder;
import lombok.Getter;

/**
 * DashboardResponse — BFF Aggregated Response
 *
 * The BFF (Backend For Frontend) pattern:
 *  Problem without BFF:
 *   React dashboard needs user profile + financial profile + health score
 *   → 3 separate API calls
 *   → 3 loading states to manage
 *   → Slower page load (waterfall requests)
 *
 *  Solution with BFF:
 *   React calls ONE endpoint: GET /api/v1/bff/dashboard
 *   → BFF internally calls all 3 services
 *   → Returns combined response in one JSON
 *   → Single loading state, faster page load
 *
 * This is especially valuable for mobile apps
 * where network latency matters most.
 */
@Getter
@Builder
public class DashboardResponse {
    private UserProfileResponse       userProfile;
    private FinancialProfileResponse  financialProfile;
    private FinancialHealthResponse   healthScore;
    private Boolean                   isProfileComplete;
    private Boolean                   isFinancialProfileComplete;
    private String                    nextActionPrompt;  // what to do next
}



