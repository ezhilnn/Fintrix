// ================================================================
// financialHealth.service.ts  (maps to dashboard.service use-case)
//
// Mirrors FinancialHealthController.java — /api/v1/financial-health
//
//   GET  /api/v1/financial-health         → getLatestScore()
//   POST /api/v1/financial-health/compute → computeScore()
//
// getLatestScore():
//   Returns the last saved FinancialHealthScore from DB.
//   Used on dashboard to show current score without recomputing.
//
// computeScore():
//   Triggers a fresh computation using the latest FinancialProfile.
//   Runs all 4 analyzers: DebtToIncome, SavingsRate,
//   CreditScore, CreditUtilization → produces overall 0-100 score.
//   Saves result to DB, clears Redis cache.
//   Returns the fresh FinancialHealthResponse.
//
// FinancialHealthResponse fields:
//   overallScore       → 0–100
//   riskLevel          → LOW | MEDIUM | HIGH | CRITICAL
//   riskLabel          → human-readable string
//   debtBurdenScore    → 0–100 (30% weight)
//   savingsRateScore   → 0–100 (25% weight)
//   creditScoreComponent → 0–100 (25% weight)
//   utilizationScore   → 0–100 (20% weight)
//   foir, creditScore, creditUtilization, savingsRate (raw values)
//   improvementTips    → string[]
//   riskWarnings       → string[]
//   scoreTrend         → ScoreTrend[] for chart
//   scoredOn           → LocalDate as "YYYY-MM-DD"
//   isFirstScore       → boolean
// ================================================================

import { get, post } from './api.client';
import { API } from '../utils/constants';
import type { FinancialHealthResponse } from '../types/api.types';

const FinancialHealthService = {

  // ── GET /api/v1/financial-health ─────────────────────────────
  // Returns latest saved score. Fast — reads from DB (or Redis cache).
  getLatestScore(): Promise<FinancialHealthResponse> {
    return get<FinancialHealthResponse>(API.FINANCIAL_HEALTH);
  },

  // ── POST /api/v1/financial-health/compute ────────────────────
  // Triggers fresh computation. Slower — runs all 4 analyzers.
  // Call this when user updates their financial profile.
  computeScore(): Promise<FinancialHealthResponse> {
    return post<FinancialHealthResponse>(API.FINANCIAL_HEALTH_COMPUTE);
  },
};

export default FinancialHealthService;