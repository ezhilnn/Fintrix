
// ================================================================
// FILE 3: FinancialHealthService.java + FinancialHealthServiceImpl
// ================================================================
package com.fintrix.modules.decisionengine.service;

import com.fintrix.modules.decisionengine.dto.FinancialHealthResponse;

public interface FinancialHealthService {
    // Compute fresh score and persist
    FinancialHealthResponse computeAndSave(String userId);
    // Get latest saved score (from Redis or DB)
    FinancialHealthResponse getLatestScore(String userId);
}