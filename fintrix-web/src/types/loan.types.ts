// ================================================================
// loan.types.ts
// Mirrors: LoanType.java, LoanEligibilityRequest.java,
//          LoanEligibilityResponse.java, LenderResult.java
// ================================================================

// ── Matches LoanType.java enum ──────────────────────────────────
export type LoanType =
  | 'PERSONAL_LOAN'
  | 'HOME_LOAN'
  | 'CAR_LOAN'
  | 'EDUCATION_LOAN'
  | 'BUSINESS_LOAN'
  | 'GOLD_LOAN'
  | 'TWO_WHEELER_LOAN'
  | 'CONSUMER_DURABLE_LOAN';

// ── Matches LoanEligibilityRequest.java ─────────────────────────
export interface LoanEligibilityRequest {
  loanType: LoanType;            // @NotNull
  requestedAmount: number;       // @DecimalMin("10000") @DecimalMax("100000000")
  tenureMonths: number;          // @Min(3) @Max(360)
  purpose?: string;              // optional free text
}

// ── Matches LenderResult.java ────────────────────────────────────
export interface LenderResult {
  lenderId: string;
  lenderName: string;
  logoUrl?: string;
  applyUrl?: string;             // direct "Apply Now" link to bank's application page

  approvalProbability: number;   // 0–100
  isEligible: boolean;           // true if >= 60%
  minInterestRate: number;
  maxInterestRate: number;
  processingFeePercent: number;

  estimatedEmi: number;          // monthly EMI in ₹
  totalInterestPayable: number;  // total interest over tenure

  failureReasons: string[];      // empty if fully eligible
  improvementTips: string[];
}

// ── Matches LoanEligibilityResponse.java ────────────────────────
export interface LoanEligibilityResponse {
  // What was requested
  loanType: LoanType;
  requestedAmount: number;
  tenureMonths: number;
  purpose?: string;

  // User's financial snapshot at time of check
  userFoir: number;
  userCreditScore?: number;
  userCreditScoreRange?: string;
  userMonthlyIncome: number;

  // Results split into two lists for clear UX
  eligibleLenders: LenderResult[];     // probability >= 60
  ineligibleLenders: LenderResult[];   // probability < 60

  // Summary messages
  overallSuggestion: string;
  creditScoreWarning?: string;         // null if score is fine
}