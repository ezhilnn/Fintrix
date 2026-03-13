// ================================================================
// constants.ts
// All values derived from actual backend config and enums
// ================================================================

// ── API Base — matches application.yml CORS allowed-origins ─────
export const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

export const API_TIMEOUT_MS = 15_000;

// ── Auth — OAuth2 flow from SecurityConfig + OAuth2SuccessHandler
// Backend redirects to: /oauth2/callback?token=<jwt>
export const GOOGLE_OAUTH_URL = `${API_BASE_URL}/oauth2/authorization/google`;
export const OAUTH_CALLBACK_PATH = '/oauth2/callback';   // must match authorized-redirect-uris in yml

// localStorage keys
export const AUTH_TOKEN_KEY   = 'fintrix_token';
export const AUTH_USER_KEY    = 'fintrix_user';

// ── API endpoints — exactly matching @RequestMapping in controllers
export const API = {
  // UserController — /api/v1/users
  USER_ME:          '/api/v1/users/me',

  // FinancialProfileController — /api/v1/financial-profile
  FINANCIAL_PROFILE: '/api/v1/financial-profile',

  // LoanController — /api/v1/loans
  LOAN_ELIGIBILITY: '/api/v1/loans/check-eligibility',

  // CreditCardController — /api/v1/credit-cards
  CARD_RECOMMENDATIONS: '/api/v1/credit-cards/recommendations',

  // FinancialHealthController — /api/v1/financial-health
  FINANCIAL_HEALTH:         '/api/v1/financial-health',
  FINANCIAL_HEALTH_COMPUTE: '/api/v1/financial-health/compute',

  // FraudCheckController — /api/v1/fraud
  FRAUD_CHECK:    '/api/v1/fraud/check',
  FRAUD_ALERTS:   '/api/v1/fraud/my-alerts',

  // DashboardController (BFF) — /api/v1/bff
  DASHBOARD: '/api/v1/bff/dashboard',
} as const;

// ── Frontend routes ──────────────────────────────────────────────
export const ROUTES = {
  LOGIN:             '/login',
  OAUTH_CALLBACK:    '/oauth2/callback',
  DASHBOARD:         '/dashboard',
  USER_PROFILE:      '/profile',
  FINANCIAL_PROFILE: '/financial-profile',
  LOAN:              '/loan',
  CREDIT_CARD:       '/credit-card',
  FRAUD_CHECK:       '/fraud-check',
  NOT_FOUND:         '*',
} as const;

// ── EmploymentType labels — matches EmploymentType.java ──────────
export const EMPLOYMENT_TYPE_LABELS: Record<string, string> = {
  SALARIED:      'Salaried (Private)',
  SELF_EMPLOYED: 'Self-Employed / Freelancer',
  GOVERNMENT:    'Government Employee',
  PSU:           'Public Sector (PSU)',
  RETIRED:       'Retired',
  STUDENT:       'Student',
  UNEMPLOYED:    'Unemployed',
};

// ── LoanType labels — matches LoanType.java ──────────────────────
export const LOAN_TYPE_LABELS: Record<string, string> = {
  PERSONAL_LOAN:          'Personal Loan',
  HOME_LOAN:              'Home Loan',
  CAR_LOAN:               'Car Loan',
  EDUCATION_LOAN:         'Education Loan',
  BUSINESS_LOAN:          'Business Loan',
  GOLD_LOAN:              'Gold Loan',
  TWO_WHEELER_LOAN:       'Two-Wheeler Loan',
  CONSUMER_DURABLE_LOAN:  'Consumer Durable Loan',
};

// ── CardCategory labels — matches CardCategory.java ──────────────
export const CARD_CATEGORY_LABELS: Record<string, string> = {
  ENTRY_LEVEL: 'Entry Level',
  CASHBACK:    'Cashback',
  TRAVEL:      'Travel',
  FUEL:        'Fuel',
  SHOPPING:    'Shopping',
  PREMIUM:     'Premium',
  BUSINESS:    'Business',
  SECURED:     'Secured (Against FD)',
};


// ── RewardType labels — matches RewardType.java ──────────────────
export const REWARD_TYPE_LABELS: Record<string, string> = {
  CASHBACK:              'Cashback',
  REWARD_POINTS:         'Reward Points',
  AIRLINE_MILES:         'Airline Miles',
  HOTEL_POINTS:          'Hotel Points',
  FUEL_SURCHARGE_WAIVER: 'Fuel Surcharge Waiver',
  NONE:                  'No Rewards',
};

// ── AlertSeverity labels — matches AlertSeverity.java ────────────
export const ALERT_SEVERITY_LABELS: Record<string, string> = {
  LOW:      'Low Risk',
  MEDIUM:   'Medium Risk',
  HIGH:     'High Risk',
  CRITICAL: 'Critical',
};

// ── EntityType labels — matches EntityType.java ──────────────────
export const ENTITY_TYPE_LABELS: Record<string, string> = {
  INVESTMENT_SCHEME:       'Investment Scheme',
  LENDER:                  'Lender / Loan Company',
  BROKER:                  'Stock Broker',
  INSURANCE_COMPANY:       'Insurance Company',
  CRYPTOCURRENCY_PLATFORM: 'Cryptocurrency Platform',
  CHIT_FUND:               'Chit Fund',
  OTHER:                   'Other',
};

// ── CIBIL Score bands (Indian standard: 300–900) ─────────────────
export const CREDIT_SCORE_BANDS = [
  { min: 750, max: 900, label: 'Excellent', color: '#22c55e' },
  { min: 700, max: 749, label: 'Good',      color: '#84cc16' },
  { min: 650, max: 699, label: 'Fair',      color: '#f59e0b' },
  { min: 550, max: 649, label: 'Poor',      color: '#f97316' },
  { min: 300, max: 549, label: 'Very Poor', color: '#ef4444' },
] as const;

// ── Financial Health Score bands — matches RiskLevel.java ────────
export const HEALTH_SCORE_BANDS = [
  { min: 75, max: 100, label: 'Low Risk',      riskLevel: 'LOW',      color: '#22c55e' },
  { min: 50, max: 74,  label: 'Medium Risk',   riskLevel: 'MEDIUM',   color: '#f59e0b' },
  { min: 25, max: 49,  label: 'High Risk',     riskLevel: 'HIGH',     color: '#f97316' },
  { min: 0,  max: 24,  label: 'Critical Risk', riskLevel: 'CRITICAL', color: '#ef4444' },
] as const;

// ── Indian States list ───────────────────────────────────────────
export const INDIAN_STATES = [
  'Andhra Pradesh', 'Arunachal Pradesh', 'Assam', 'Bihar', 'Chhattisgarh',
  'Goa', 'Gujarat', 'Haryana', 'Himachal Pradesh', 'Jharkhand', 'Karnataka',
  'Kerala', 'Madhya Pradesh', 'Maharashtra', 'Manipur', 'Meghalaya', 'Mizoram',
  'Nagaland', 'Odisha', 'Punjab', 'Rajasthan', 'Sikkim', 'Tamil Nadu',
  'Telangana', 'Tripura', 'Uttar Pradesh', 'Uttarakhand', 'West Bengal',
  'Delhi', 'Chandigarh', 'Puducherry', 'Ladakh', 'Jammu & Kashmir',
] as const;