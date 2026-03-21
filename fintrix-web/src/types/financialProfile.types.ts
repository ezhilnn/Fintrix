// ================================================================
// financialProfile.types.ts
// Mirrors: FinancialProfileRequest.java, FinancialProfileResponse.java,
//          EmploymentType.java, RiskLevel.java
// ================================================================

// ── Matches EmploymentType.java enum ────────────────────────────
export type EmploymentType =
  | 'SALARIED'       // Private sector employee
  | 'SELF_EMPLOYED'  // Business owner / freelancer
  | 'GOVERNMENT'     // Central / State government employee
  | 'PSU'            // Public sector undertaking
  | 'RETIRED'
  | 'STUDENT'
  | 'UNEMPLOYED';

// ── Matches RiskLevel.java enum ─────────────────────────────────
export type RiskLevel = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

// ── Matches FinancialProfileRequest.java ────────────────────────
export interface FinancialProfileRequest {
  employmentType: EmploymentType;        // @NotNull
  employerName?: string;
  yearsOfExperience?: number;            // 0–50

  monthlyIncome: number;                 // @DecimalMin("1000.00")
  monthlyExpenses?: number;              // >= 0
  existingEmiTotal?: number;             // >= 0
  numberOfActiveLoans?: number;          // 0–20

  creditScore?: number;                  // optional, 300–900 (CIBIL)
  numberOfCreditCards?: number;          // 0–20
  totalCreditLimit?: number;             // >= 0
  currentCreditUtilization?: number;     // 0.00–100.00 (percent)

  preferredRewardType?: string;          // "CASHBACK" | "TRAVEL" | "FUEL"
  topSpendingCategory?: string;          // "FOOD" | "SHOPPING" | "TRAVEL"
}

// ── Matches FinancialProfileResponse.java ───────────────────────
export interface FinancialProfileResponse {
  id: string;
  userId: string;

  // Employment
  employmentType: EmploymentType;
  employerName?: string;
  yearsOfExperience?: number;

  // Income & Expenses
  monthlyIncome: number;
  monthlyExpenses?: number;
  monthlySavings?: number;          // computed: income - expenses - emi
  existingEmiTotal?: number;
  numberOfActiveLoans?: number;     // now returned in response

  // Credit Profile
  creditScore?: number;
  creditScoreRange?: string;        // EXCELLENT | GOOD | FAIR | POOR | UNKNOWN
  numberOfCreditCards?: number;
  totalCreditLimit?: number;
  currentCreditUtilization?: number;

  // Computed ratios
  foir?: number;                    // existingEMI / income × 100 (bank eligibility check)
  dti?: number;                     // (existingEMI + expenses) / income × 100 (full picture)
  dtiRange?: string;                // LOW | MODERATE | HIGH | CRITICAL

  // Health
  financialHealthScore?: number;    // 0–100
  riskLevel?: RiskLevel;

  // Preferences
  preferredRewardType?: string;
  topSpendingCategory?: string;

  isComplete: boolean;
}