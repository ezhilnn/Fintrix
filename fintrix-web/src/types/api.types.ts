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
  applyUrl?: string;             // direct "Apply Now" link to bank's card application page
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
// Mirrors: DashboardResponse.java + DashboardFinancialMetrics.java
// ================================================================

// ── Inner types used by DashboardFinancialMetrics ─────────────────
export interface DashboardEmiSummary {
  loanName: string;
  lenderName?: string;
  emiAmount: number;
  dueDateOfMonth: number;
  dueDateLabel: string;
  isDueSoon: boolean;
  remainingEmis?: number;
  endDate?: string;          // LocalDate → "YYYY-MM-DD"
}

export interface DashboardScoreTrendPoint {
  scoredOn: string;          // LocalDate → "YYYY-MM-DD"
  score: number;
  riskLevel: string;
}

export interface DashboardFinancialAlert {
  severity: string;          // DANGER | WARNING | INFO
  icon: string;
  title: string;
  message: string;
  action: string;
}

// ── Mirrors DashboardFinancialMetrics.java (9 sections) ───────────
export interface DashboardFinancialMetrics {
  // Section 1: Employment
  employmentType?: string;
  employmentTypeLabel?: string;
  employerName?: string;
  yearsOfExperience?: number;
  experienceLabel?: string;

  // Section 2: Income & cash flow
  monthlyIncome: number;
  monthlyExpenses?: number;
  existingEmiTotal?: number;
  monthlySavings?: number;
  disposableIncome?: number;
  savingsRatePercent?: number;
  savingsRateLabel?: string;
  cashFlowStatus?: string;          // POSITIVE | TIGHT | NEGATIVE

  // Section 3: Debt profile
  foir?: number;
  foirLabel?: string;
  foirStatus?: string;              // HEALTHY | MODERATE | HIGH | CRITICAL
  dti?: number;
  dtiRange?: string;                // LOW | MODERATE | HIGH | CRITICAL
  dtiLabel?: string;
  dtiVsFoirInsight?: string;
  numberOfActiveLoans?: number;
  loanBurdenLabel?: string;
  maxAdditionalEmiCapacity?: number;
  additionalEmiLabel?: string;

  // Section 4: EMI tracker
  totalTrackedEmis?: number;
  totalMonthlyEmiCommitment?: number;
  emisDueSoon?: number;
  upcomingEmis?: DashboardEmiSummary[];

  // Section 5: Credit profile
  creditScore?: number;
  creditScoreRange?: string;
  creditScoreLabel?: string;
  creditScoreTip?: string;
  numberOfCreditCards?: number;
  totalCreditLimit?: number;
  currentCreditUtilization?: number;
  utilizationLabel?: string;
  utilizationStatus?: string;       // HEALTHY | MODERATE | HIGH | MAXED

  // Section 6: Health sub-scores
  overallHealthScore?: number;
  overallHealthLabel?: string;
  riskLevel?: string;
  riskLabel?: string;
  debtBurdenScore?: number;
  savingsRateScore?: number;
  creditScoreComponent?: number;
  creditUtilizationScore?: number;
  scoreTrend?: number;              // delta vs previous score
  scoreTrendLabel?: string;
  scoreTrendHistory?: DashboardScoreTrendPoint[];

  // Section 7: Activity summary
  totalLoanChecks?: number;
  totalCardChecks?: number;
  totalFraudChecks?: number;
  totalAffiliateClicks?: number;
  unreadNotifications?: number;
  lastActivityLabel?: string;

  // Section 8: Smart alerts
  alerts?: DashboardFinancialAlert[];

  // Section 9: Product readiness
  loanReadiness?: string;           // READY | BORDERLINE | NOT_READY
  loanReadinessLabel?: string;
  cardUpgradeReady?: string;
  cardUpgradeLabel?: string;
}

export interface DashboardResponse {
  userProfile: UserProfileResponse;
  financialProfile: FinancialProfileResponse;
  healthScore: FinancialHealthResponse;
  financialMetrics?: DashboardFinancialMetrics;   // NEW — enriched metrics
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
// v2: added SAFE (verified in registry) and UNVERIFIED (not found)
export type AlertSeverity = 'SAFE' | 'UNVERIFIED' | 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

// ── Matches FraudCheckRequest.java ──────────────────────────────
export interface FraudCheckRequest {
  entityName: string;      // @NotBlank
  entityType: EntityType;  // @NotNull
}

// ── Matches FraudCheckResponse.java (v2 — new fields added) ─────
export interface FraudCheckResponse {
  entityName: string;
  entityType: EntityType;
  isSafe: boolean;
  severity: AlertSeverity;
  severityLabel?: string;       // e.g. "✅ VERIFIED", "⛔ CRITICAL RISK"
  isSebiRegistered?: boolean;   // null = not checked
  isRbiRegistered?: boolean;    // null = not checked
  registrationNumber?: string;  // e.g. "INZ000031331" — shown when found in registry
  regulatorName?: string;       // "SEBI" | "RBI" | "IRDAI"
  redFlags: string[];
  safetyTips: string[];
  verdict: string;
  regulatorCheckUrl?: string;
}

// ================================================================
// Keyword Scan Types  (NEW — Feature 2 in FraudCheckController)
// Mirrors: KeywordScanRequest.java, KeywordScanResponse.java
// POST /api/v1/fraud/scan
// GET  /api/v1/fraud/scan/content-types
// ================================================================

export type ContentType =
  | 'WHATSAPP_MESSAGE' | 'SMS' | 'EMAIL' | 'SOCIAL_MEDIA_POST'
  | 'WEBSITE_URL' | 'PHONE_CALL_SCRIPT' | 'INVESTMENT_PITCH'
  | 'LOAN_OFFER' | 'JOB_OFFER' | 'OTHER';

export interface KeywordScanRequest {
  text:         string;       // @NotBlank @Size(max=5000)
  contentType?: ContentType;  // default OTHER
}

export interface KeywordMatch {
  keyword:       string;
  riskLevel:     string;      // LOW | MEDIUM | HIGH | CRITICAL
  fraudType:     string;      // PONZI | PHISHING | TASK_FRAUD etc.
  explanation:   string;
  matchedPhrase: string;      // surrounding context snippet
  matchType:     string;      // "EXACT" | "FUZZY"
}

export interface KeywordScanResponse {
  overallRisk:        string;          // SAFE | LOW | MEDIUM | HIGH | CRITICAL
  isSafe:             boolean;
  totalMatchesFound:  number;
  verdict:            string;
  contentTypeLabel:   string;          // "WhatsApp Message" etc.
  matches:            KeywordMatch[];
  safetyActions:      string[];
  reportUrl:          string;
  scannedTextPreview: string;          // first 200 chars
}

export interface ContentTypeOption {
  value: ContentType;
  label: string;
}


// Mirrors: EmiTrackerRequest.java, EmiTrackerResponse.java
// ================================================================

export interface EmiTrackerRequest {
  loanName: string;          // @NotBlank
  lenderName?: string;
  loanType?: string;         // free text, not the LoanType enum
  principalAmount: number;   // @DecimalMin("1000")
  emiAmount: number;         // @DecimalMin("100")
  dueDateOfMonth: number;    // @Min(1) @Max(31)
  startDate: string;         // LocalDate → "YYYY-MM-DD"
  endDate: string;           // LocalDate → "YYYY-MM-DD"
  reminderDaysBefore?: number; // @Min(1) @Max(10), default 3
}

export interface EmiTrackerResponse {
  id: string;
  loanName: string;
  lenderName?: string;
  loanType?: string;
  principalAmount: number;
  emiAmount: number;
  dueDateOfMonth: number;
  startDate: string;         // "YYYY-MM-DD"
  endDate: string;           // "YYYY-MM-DD"
  remainingEmis?: number;    // computed by backend
  isDueSoon?: boolean;       // true if due within reminderDaysBefore days
  dueDateLabel?: string;     // "Due on 5th of every month"
}

// ================================================================
// Notification Types
// Mirrors: NotificationResponse.java, RegisterTokenRequest.java
// ================================================================

export interface NotificationResponse {
  id: string;
  title: string;
  body: string;
  notificationType: string;  // EMI_REMINDER | SCORE_UPDATE | FRAUD_ALERT | OFFER
  payload?: string;          // JSON string: { route: "/loans", entityId: "xxx" }
  isRead: boolean;
  createdAt: string;         // LocalDateTime → ISO string
}

export interface RegisterTokenRequest {
  fcmToken: string;          // @NotBlank
  deviceType?: string;       // ANDROID | IOS | WEB, default "ANDROID"
  deviceId?: string;
  appVersion?: string;
}

// Paginated wrapper used by GET /api/v1/notifications
export interface PagedNotifications {
  content: NotificationResponse[];
  totalElements: number;
  totalPages: number;
  number: number;           // current page (0-indexed)
  size: number;
  last: boolean;
}

// ================================================================
// Consent Types
// Mirrors: ConsentType.java, ConsentRequest.java,
//          ConsentStatusResponse.java
// ================================================================

export type ConsentType =
  | 'DATA_PROCESSING'   // mandatory — app cannot function without
  | 'MARKETING'         // optional — promotional comms
  | 'CREDIT_CHECK'      // expires every 6 months
  | 'THIRD_PARTY_SHARE'; // per-click when visiting lender/card

export interface ConsentRequest {
  consentType: ConsentType;  // @NotBlank
}

export interface ConsentStatusResponse {
  userId: string;
  consentVersion?: string;
  dataProcessing: boolean;   // mandatory — gate entire app on this
  marketing: boolean;
  creditCheck: boolean;
  thirdPartyShare: boolean;
}

// ================================================================
// Tracking Types
// Mirrors: TrackEventRequest.java, AffiliateClickResponse.java
// ================================================================

export interface TrackEventRequest {
  sessionId?: string;
  eventType: string;         // PAGE_VIEW | CARD_VIEW | LOAN_VIEW | APPLY_CLICK
  page?: string;
  elementId?: string;
  entityId?: string;
  metadata?: Record<string, unknown>;
  durationMs?: number;
}

export interface AffiliateClickResponse {
  hasPartnership: boolean;
  trackedUrl?: string;       // redirect here if hasPartnership = true
  clickRef?: string;         // for conversion tracking
  partnerName?: string;
  commissionType?: string;   // CPA | CPL | FLAT
}

// ================================================================
// Admin Types
// Mirrors: AdminDashboardStats.java, FraudKeyword.java
// Admin controllers use Lender + CreditCard domain objects directly
// ================================================================

export interface AdminDashboardStats {
  totalUsers: number;
  activeUsers: number;
  totalLoanChecks: number;
  totalCardChecks: number;
  totalFraudChecks: number;
  totalAffiliateClicks: number;
  totalConversions: number;
  estimatedRevenue: number;
  totalNotificationsSent: number;
  pendingConsentUsers: number;
}

export interface FraudKeyword {
  id: string;
  keyword: string;
  riskLevel: string;         // LOW | MEDIUM | HIGH | CRITICAL
  fraudType: string;         // PONZI | ADVANCE_FEE | FAKE_INVESTMENT etc.
  description?: string;
  isActive: boolean;
  createdAt: string;
}

// Admin lender shape (domain object returned directly by AdminLenderController)
export interface AdminLender {
  id: string;
  name: string;
  logoUrl?: string;
  applyUrl?: string;              // direct apply link shown on "Apply Now"
  lenderType?: string;            // BANK | NBFC | MFI — default "BANK"
  regulator?: string;             // RBI | SEBI — default "RBI"
  loanType: string;
  minCreditScore: number;
  minMonthlyIncome: number;
  maxFoir: number;
  minAge: number;
  maxAge: number;
  minEmploymentYears?: number;
  allowedEmploymentTypes?: string;
  minLoanAmount?: number;
  maxLoanAmount?: number;
  minInterestRate: number;
  maxInterestRate: number;
  processingFeePercent: number;
  isActive: boolean;
}

// Admin card shape (domain object returned directly by AdminCardController)
export interface AdminCard {
  id: string;
  bankName: string;
  cardName: string;
  logoUrl?: string;
  applyUrl?: string;              // direct apply link shown on "Apply Now"
  cardNetwork?: string;           // Visa | Mastercard | Amex | RuPay
  interestRate?: number;          // e.g. 42.00 (%)
  fuelSurchargeWaiver?: boolean;
  internationalUsage?: boolean;
  loungeAccess?: string;          // e.g. "2 domestic visits/quarter"
  cardCategory: string;
  rewardType: string;
  minCreditScore: number;
  minMonthlyIncome: number;
  minAge: number;
  maxAge: number;
  allowedEmploymentTypes?: string;
  joiningFee: number;
  annualFee: number;
  annualFeeWaiverCondition?: string;
  rewardRate?: string;
  welcomeBenefit?: string;
  keyBenefits?: string[];
  isActive: boolean;
}

// Paginated wrapper used by admin list endpoints
export interface Paged<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  last: boolean;
}