// ================================================================
// scoreHelpers.ts — score band lookups that match backend logic
// validators.ts   — form validators that mirror backend @Validation
// ================================================================

import { CREDIT_SCORE_BANDS, HEALTH_SCORE_BANDS } from './constants';
import type { RiskLevel } from '../types/financialProfile.types';

// ── Score Band Helpers ───────────────────────────────────────────

export interface BandInfo {
  label: string;
  color: string;
}

/** Get CIBIL score band label + colour (300–900 scale) */
export const getCreditScoreBand = (score: number): BandInfo => {
  const band = CREDIT_SCORE_BANDS.find(b => score >= b.min && score <= b.max);
  return band ?? { label: 'Unknown', color: '#94a3b8' };
};

/** Get Financial Health score band — matches RiskLevel.java */
export const getHealthScoreBand = (score: number): BandInfo & { riskLevel: string } => {
  const band = HEALTH_SCORE_BANDS.find(b => score >= b.min && score <= b.max);
  return band ?? { label: 'Unknown', color: '#94a3b8', riskLevel: 'MEDIUM' };
};

/** RiskLevel → display colour */
export const riskLevelColor = (level: RiskLevel): string => {
  switch (level) {
    case 'LOW':      return '#22c55e';
    case 'MEDIUM':   return '#f59e0b';
    case 'HIGH':     return '#f97316';
    case 'CRITICAL': return '#ef4444';
  }
};

/** RiskLevel → badge text colour class (Tailwind) */
export const riskLevelBadgeClass = (level: RiskLevel): string => {
  switch (level) {
    case 'LOW':      return 'text-green-600 bg-green-50 border-green-200';
    case 'MEDIUM':   return 'text-yellow-600 bg-yellow-50 border-yellow-200';
    case 'HIGH':     return 'text-orange-600 bg-orange-50 border-orange-200';
    case 'CRITICAL': return 'text-red-600 bg-red-50 border-red-200';
  }
};

// ── Form Validators — mirror backend @Validation annotations ────
// Return null = valid, return string = error message

/** Mirrors: @Min(18) @Max(100) on UserProfileRequest.age */
export const validateAge = (age: number | undefined): string | null => {
  if (age === undefined || age === null) return 'Age is required';
  if (age < 18)  return 'You must be at least 18 years old';
  if (age > 100) return 'Please enter a valid age';
  return null;
};

/** Mirrors: @NotBlank on fullName, @Size(min=2, max=150) */
export const validateFullName = (name: string): string | null => {
  if (!name?.trim()) return 'Full name is required';
  if (name.trim().length < 2)   return 'Name must be at least 2 characters';
  if (name.trim().length > 150) return 'Name must be under 150 characters';
  return null;
};

/** Mirrors: @Pattern(regexp = "^[6-9]\\d{9}$") on phoneNumber */
export const validatePhone = (phone: string | undefined): string | null => {
  if (!phone) return null; // optional field
  if (!/^[6-9]\d{9}$/.test(phone)) return 'Enter a valid 10-digit Indian mobile number';
  return null;
};

/** Mirrors: @DecimalMin("1000.00") on monthlyIncome */
export const validateMonthlyIncome = (income: number | undefined): string | null => {
  if (income === undefined || income === null) return 'Monthly income is required';
  if (income < 1000) return 'Monthly income must be at least ₹1,000';
  return null;
};

/** Mirrors: @Min(300) @Max(900) on creditScore — optional field */
export const validateCreditScore = (score: number | undefined): string | null => {
  if (score === undefined || score === null) return null; // optional
  if (score < 300 || score > 900) return 'CIBIL score must be between 300 and 900';
  return null;
};

/** Mirrors: @DecimalMin("0.00") @DecimalMax("100.00") on currentCreditUtilization */
export const validateUtilization = (value: number | undefined): string | null => {
  if (value === undefined || value === null) return null;
  if (value < 0)   return 'Utilization cannot be negative';
  if (value > 100) return 'Utilization cannot exceed 100%';
  return null;
};

/** Mirrors: @DecimalMin("10000") @DecimalMax("100000000") on requestedAmount */
export const validateLoanAmount = (amount: number | undefined): string | null => {
  if (amount === undefined || amount === null) return 'Loan amount is required';
  if (amount < 10_000)      return 'Minimum loan amount is ₹10,000';
  if (amount > 100_000_000) return 'Maximum loan amount is ₹10 Crore';
  return null;
};

/** Mirrors: @Min(3) @Max(360) on tenureMonths */
export const validateTenure = (months: number | undefined): string | null => {
  if (months === undefined || months === null) return 'Loan tenure is required';
  if (months < 3)   return 'Minimum tenure is 3 months';
  if (months > 360) return 'Maximum tenure is 360 months (30 years)';
  return null;
};

/** Mirrors: @NotBlank on entityName */
export const validateRequired = (
  value: string | undefined,
  fieldLabel: string,
): string | null => {
  if (!value?.trim()) return `${fieldLabel} is required`;
  return null;
};