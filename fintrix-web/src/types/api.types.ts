// ================================================================
// api.types.ts
// Mirrors: ApiResponse.java, DashboardResponse.java,
//          CardCategory.java, RewardType.java, CardRecommendationRequest.java,
//          CardRecommendationResponse.java, CardResult.java,
//          FinancialHealthResponse.java (incl. ScoreTrend),
//          FraudCheckRequest.java, FraudCheckResponse.java,
//          EntityType.java, AlertSeverity.java
// ================================================================

import type { UserProfileResponse }      from './user.types';
import type { FinancialProfileResponse } from './financialProfile.types';
import type { RiskLevel }                from './financialProfile.types';

// ── Matches ApiResponse.java ─────────────────────────────────────
// NON_NULL — "errors" only present when success=false
export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  timestamp: string;        // LocalDateTime serialised as ISO string
}

// ── Generic API error shape ──────────────────────────────────────
export interface ApiError {
  status: number;
  message: string;
  errors?: Record<string, string>;
}

// ================================================================
// Credit Card Types
// Mirrors: CardCategory.java, RewardType.java,
//          CardRecommendationRequest.java, CardRecommendationResponse.java,
//          CardResult.java
// ================================================================

// ── Matches CardCategory.java ────────────────────────────────────
export type CardCategory =
  | 'ENTRY_LEVEL'
  | 'CASHBACK'
  | 'TRAVEL'
  | 'FUEL'
  | 'SHOPPING'
  | 'PREMIUM'
  | 'BUSINESS'
  | 'SECURED';

// ── Matches RewardType.java ──────────────────────────────────────
export type RewardType =
  | 'CASHBACK'
  | 'REWARD_POINTS'
  | 'AIRLINE_MILES'
  | 'HOTEL_POINTS'
  | 'FUEL_SURCHARGE_WAIVER'
  | 'NONE';

// ── Matches CardRecommendationRequest.java ───────────────────────
// All fields optional — backend handles null request too
export interface CardRecommendationRequest {
  preferredRewardType?: string;   // "CASHBACK" | "TRAVEL" | "FUEL"
  topSpendingCategory?: string;   // "FOOD" | "SHOPPING" | "TRAVEL"
  preferNoAnnualFee?: boolean;
}

// ── Matches CardResult.java ──────────────────────────────────────
export interface CardResult {
  cardId: string;
  bankName: string;
  cardName: string;
  logoUrl?: string;
  cardCategory: CardCategory;
  rewardType: RewardType;

  approvalProbability: number;       // 0–100
  isEligible: boolean;

  joiningFee: number;
  annualFee: number;
  annualFeeWaiverCondition?: string;

  rewardRate?: string;
  welcomeBenefit?: string;
  keyBenefits: string[];

  matchReason?: string;
  failureReasons: string[];
}

// ── Matches CardRecommendationResponse.java ──────────────────────
export interface CardRecommendationResponse {
  recommendedCards: CardResult[];    // eligible, best match first
  otherEligibleCards: CardResult[];  // eligible but not top match
  futureCards: CardResult[];         // not yet eligible — shows goal
  overallTip?: string;
  multipleCardWarning?: string;
}

// ================================================================
// Financial Health Types
// Mirrors: FinancialHealthResponse.java (incl. ScoreTrend inner class)
// ================================================================

// ── Matches FinancialHealthResponse.ScoreTrend ───────────────────
export interface ScoreTrend {
  scoredOn: string;             // LocalDate → "2025-03-01"
  score: number;
  riskLevel: RiskLevel;
}

// ── Matches FinancialHealthResponse.java ─────────────────────────
export interface FinancialHealthResponse {
  // Current score
  overallScore: number;          // 0–100
  riskLevel: RiskLevel;
  riskLabel: string;

  // Sub-score breakdown (weights: debtBurden 30%, savings 25%, credit 25%, utilization 20%)
  debtBurdenScore: number;       // 0–100
  savingsRateScore: number;      // 0–100
  creditScoreComponent: number;  // 0–100
  utilizationScore: number;      // 0–100

  // Raw values
  foir: number;
  creditScore?: number;
  creditScoreRange?: string;
  creditUtilization: number;
  savingsRate: number;           // percentage

  // Actionable output
  improvementTips: string[];
  riskWarnings: string[];

  // Score history
  scoreTrend: ScoreTrend[];

  // Meta
  scoredOn: string;              // LocalDate → "2025-03-01"
  isFirstScore: boolean;
}

// ================================================================
// Dashboard Types (BFF)
// Mirrors: DashboardResponse.java
// ================================================================

export interface DashboardResponse {
  userProfile: UserProfileResponse;
  financialProfile: FinancialProfileResponse;
  healthScore: FinancialHealthResponse;
  isProfileComplete: boolean;
  isFinancialProfileComplete: boolean;
  nextActionPrompt?: string;
}

// ================================================================
// Fraud Check Types
// Mirrors: EntityType.java, AlertSeverity.java,
//          FraudCheckRequest.java, FraudCheckResponse.java
// ================================================================

// ── Matches EntityType.java ──────────────────────────────────────
export type EntityType =
  | 'INVESTMENT_SCHEME'
  | 'LENDER'
  | 'BROKER'
  | 'INSURANCE_COMPANY'
  | 'CRYPTOCURRENCY_PLATFORM'
  | 'CHIT_FUND'
  | 'OTHER';

// ── Matches AlertSeverity.java ───────────────────────────────────
export type AlertSeverity = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

// ── Matches FraudCheckRequest.java ──────────────────────────────
export interface FraudCheckRequest {
  entityName: string;      // @NotBlank
  entityType: EntityType;  // @NotNull
}

// ── Matches FraudCheckResponse.java ─────────────────────────────
export interface FraudCheckResponse {
  entityName: string;
  entityType: EntityType;
  isSafe: boolean;
  severity: AlertSeverity;
  isSebiRegistered: boolean;
  isRbiRegistered: boolean;
  redFlags: string[];
  safetyTips: string[];
  verdict: string;
  regulatorCheckUrl?: string;
}