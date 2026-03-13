// ================================================================
// formatters.ts — display helpers for Indian financial context
// ================================================================

/**
 * Format number as Indian Rupees
 * formatCurrency(150000)        → "₹1,50,000"
 * formatCurrency(150000, true)  → "₹1.5L"
 */
export const formatCurrency = (amount: number, compact = false): string => {
  if (compact) {
    if (amount >= 10_000_000) return `₹${(amount / 10_000_000).toFixed(1)}Cr`;
    if (amount >= 100_000)    return `₹${(amount / 100_000).toFixed(1)}L`;
    if (amount >= 1_000)      return `₹${(amount / 1_000).toFixed(1)}K`;
  }
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 0,
  }).format(amount);
};

/**
 * Format a decimal as percentage string
 * formatPercent(42.5) → "42.5%"
 */
export const formatPercent = (value: number, decimals = 1): string =>
  `${value.toFixed(decimals)}%`;

/**
 * Format ISO / LocalDate string to Indian locale
 * "2025-06-15" → "15 Jun 2025"
 */
export const formatDate = (dateStr: string): string => {
  const d = new Date(dateStr);
  // Guard against invalid dates
  if (isNaN(d.getTime())) return dateStr;
  return d.toLocaleDateString('en-IN', {
    day: '2-digit', month: 'short', year: 'numeric',
  });
};

/**
 * Format loan tenure in months to human-readable string
 * formatTenure(36) → "3 yrs"
 * formatTenure(18) → "1 yr 6 m"
 * formatTenure(6)  → "6 months"
 */
export const formatTenure = (months: number): string => {
  if (months % 12 === 0) {
    const y = months / 12;
    return `${y} yr${y > 1 ? 's' : ''}`;
  }
  const years = Math.floor(months / 12);
  const rem   = months % 12;
  if (years > 0) return `${years} yr${years > 1 ? 's' : ''} ${rem} m`;
  return `${rem} months`;
};

/**
 * Estimate monthly EMI using standard reducing balance formula
 * Used for frontend preview — matches backend MathUtils logic
 *
 * P = principal, r = annual interest rate (%), n = months
 */
export const calculateEmi = (
  principal: number,
  annualRatePercent: number,
  months: number,
): number => {
  if (months <= 0 || principal <= 0) return 0;
  const r = annualRatePercent / 100 / 12;
  if (r === 0) return Math.round(principal / months);
  const emi = (principal * r * Math.pow(1 + r, months)) / (Math.pow(1 + r, months) - 1);
  return Math.round(emi);
};

/**
 * Format approval probability as text label
 * 80+ → "Very Likely"   60–79 → "Likely"
 * 40–59 → "Possible"    <40   → "Unlikely"
 */
export const formatApprovalLabel = (prob: number): string => {
  if (prob >= 80) return 'Very Likely';
  if (prob >= 60) return 'Likely';
  if (prob >= 40) return 'Possible';
  return 'Unlikely';
};

/**
 * Get colour for approval probability
 * >= 60 → green   40-59 → amber   < 40 → red
 */
export const approvalColor = (prob: number): string => {
  if (prob >= 60) return '#22c55e';
  if (prob >= 40) return '#f59e0b';
  return '#ef4444';
};