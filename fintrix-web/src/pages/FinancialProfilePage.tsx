// ================================================================
// FinancialProfilePage.tsx  —  /financial-profile  (protected)
// Step 2 of 2 onboarding — financial details.
// POST /api/v1/financial-profile (first time → 201)
// PUT  /api/v1/financial-profile (update     → 200)
//
// FinancialProfileRequest fields (backend @Valid):
//   employmentType           @NotNull  (EmploymentType enum)
//   employerName             optional
//   yearsOfExperience        0–50
//   monthlyIncome            @DecimalMin("1000.00")
//   monthlyExpenses          >= 0
//   existingEmiTotal         >= 0
//   numberOfActiveLoans      0–20
//   creditScore              optional 300–900
//   numberOfCreditCards      0–20
//   totalCreditLimit         >= 0
//   currentCreditUtilization 0–100
//   preferredRewardType      optional string
//   topSpendingCategory      optional string
// ================================================================

import { useState, useEffect } from 'react';
import type { FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import useFinancialProfile from '../hooks/useFinancialProfile';
import { ROUTES, EMPLOYMENT_TYPE_LABELS } from '../utils/constants';
import { formatCurrency, formatPercent } from '../utils/formatters';
import {
  validateMonthlyIncome,
  validateCreditScore,
  validateUtilization,
} from '../utils/scoreHelpers';
import type { FinancialProfileRequest, EmploymentType } from '../types/financialProfile.types';
import './FinancialProfilePage.css';

const REWARD_TYPES   = ['CASHBACK', 'TRAVEL', 'FUEL', 'REWARD_POINTS'];
const SPEND_CATS     = ['FOOD', 'SHOPPING', 'TRAVEL', 'FUEL', 'ENTERTAINMENT', 'HEALTHCARE'];

const FinancialProfilePage = () => {
  const navigate = useNavigate();
  const { profile, isSaving, saveError, fieldErrors, saveProfile } = useFinancialProfile();

  const [form, setForm] = useState<FinancialProfileRequest>({
    employmentType:          (profile?.employmentType ?? 'SALARIED') as EmploymentType,
    employerName:            profile?.employerName            ?? '',
    yearsOfExperience:       profile?.yearsOfExperience       ?? ('' as unknown as number),
    monthlyIncome:           profile?.monthlyIncome           ?? ('' as unknown as number),
    monthlyExpenses:         profile?.monthlyExpenses         ?? ('' as unknown as number),
    existingEmiTotal:        profile?.existingEmiTotal        ?? ('' as unknown as number),
    numberOfActiveLoans:     ('' as unknown as number),
    creditScore:             profile?.creditScore             ?? ('' as unknown as number),
    numberOfCreditCards:     profile?.numberOfCreditCards     ?? ('' as unknown as number),
    totalCreditLimit:        profile?.totalCreditLimit        ?? ('' as unknown as number),
    currentCreditUtilization: profile?.currentCreditUtilization ?? ('' as unknown as number),
    preferredRewardType:     profile?.preferredRewardType     ?? '',
    topSpendingCategory:     profile?.topSpendingCategory     ?? '',
  });

  const [localErrors, setLocalErrors] = useState<Record<string, string>>({});

  // Merge backend 400 field errors
  useEffect(() => {
    if (fieldErrors) setLocalErrors(prev => ({ ...prev, ...fieldErrors }));
  }, [fieldErrors]);

  const set = (key: keyof FinancialProfileRequest, value: string | number) => {
    setForm(f => ({ ...f, [key]: value }));
    setLocalErrors(e => ({ ...e, [key]: '' }));
  };

  // ── Live FOIR preview ─────────────────────────────────────
  const income   = Number(form.monthlyIncome)    || 0;
  const emi      = Number(form.existingEmiTotal) || 0;
  const savings  = income - (Number(form.monthlyExpenses) || 0) - emi;
  const foir     = income > 0 ? (emi / income) * 100 : 0;

  const foirClass =
    foir === 0          ? '' :
    foir <= 30          ? 'fin-profile-page__preview-value--ok' :
    foir <= 50          ? 'fin-profile-page__preview-value--warn' :
    'fin-profile-page__preview-value--danger';

  // ── Validation ────────────────────────────────────────────
  const validate = (): boolean => {
    const errs: Record<string, string> = {};
    if (!form.employmentType) errs.employmentType = 'Employment type is required';
    const incomeErr = validateMonthlyIncome(Number(form.monthlyIncome));
    if (incomeErr) errs.monthlyIncome = incomeErr;
    const csErr = validateCreditScore(form.creditScore ? Number(form.creditScore) : undefined);
    if (csErr) errs.creditScore = csErr;
    const utilErr = validateUtilization(
      form.currentCreditUtilization ? Number(form.currentCreditUtilization) : undefined,
    );
    if (utilErr) errs.currentCreditUtilization = utilErr;
    setLocalErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    if (!validate()) return;
    saveProfile(
      {
        ...form,
        monthlyIncome:            Number(form.monthlyIncome),
        monthlyExpenses:          form.monthlyExpenses    ? Number(form.monthlyExpenses)    : undefined,
        existingEmiTotal:         form.existingEmiTotal   ? Number(form.existingEmiTotal)   : undefined,
        numberOfActiveLoans:      form.numberOfActiveLoans ? Number(form.numberOfActiveLoans) : undefined,
        creditScore:              form.creditScore         ? Number(form.creditScore)         : undefined,
        numberOfCreditCards:      form.numberOfCreditCards ? Number(form.numberOfCreditCards) : undefined,
        totalCreditLimit:         form.totalCreditLimit    ? Number(form.totalCreditLimit)    : undefined,
        currentCreditUtilization: form.currentCreditUtilization
          ? Number(form.currentCreditUtilization) : undefined,
        yearsOfExperience:        form.yearsOfExperience  ? Number(form.yearsOfExperience)   : undefined,
      },
      () => navigate(ROUTES.DASHBOARD),
    );
  };

  return (
    <div className="fin-profile-page">
      <div className="fin-profile-page__container">

        <span className="fin-profile-page__step-badge">
          ✦ Step 2 of 2 — Financial Details
        </span>

        <h1 className="fin-profile-page__title">Your financial profile</h1>
        <p className="fin-profile-page__sub">
          Used only to estimate loan eligibility and recommend credit cards.
          We never access your real credit report.
        </p>

        <div className="fin-profile-page__card">
          <form onSubmit={handleSubmit}>

            {/* ── Section: Employment ─────────────────────── */}
            <div className="fin-profile-page__section">
              <p className="fin-profile-page__section-title">Employment</p>
              <div className="form-grid">

                <div className="form-group col-full">
                  <label className="form-label">
                    Employment Type <span className="required">*</span>
                  </label>
                  <select
                    className={`form-input${localErrors.employmentType ? ' input-error' : ''}`}
                    value={form.employmentType}
                    onChange={e => set('employmentType', e.target.value)}
                  >
                    {(Object.entries(EMPLOYMENT_TYPE_LABELS) as [string, string][]).map(([val, label]) => (
                      <option key={val} value={val}>{label}</option>
                    ))}
                  </select>
                  {localErrors.employmentType && (
                    <span className="form-error">{localErrors.employmentType}</span>
                  )}
                </div>

                <div className="form-group">
                  <label className="form-label">Employer Name</label>
                  <input
                    className="form-input"
                    placeholder="Optional"
                    value={form.employerName}
                    onChange={e => set('employerName', e.target.value)}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Years of Experience</label>
                  <input
                    className="form-input"
                    type="number"
                    min={0}
                    max={50}
                    placeholder="e.g. 3"
                    value={form.yearsOfExperience}
                    onChange={e => set('yearsOfExperience', e.target.value)}
                  />
                </div>

              </div>
            </div>

            {/* ── Section: Income & Expenses ──────────────── */}
            <div className="fin-profile-page__section">
              <p className="fin-profile-page__section-title">Income & Expenses</p>
              <div className="form-grid">

                <div className="form-group">
                  <label className="form-label">
                    Monthly Income (₹) <span className="required">*</span>
                  </label>
                  <input
                    className={`form-input${localErrors.monthlyIncome ? ' input-error' : ''}`}
                    type="number"
                    min={1000}
                    placeholder="e.g. 60000"
                    value={form.monthlyIncome}
                    onChange={e => set('monthlyIncome', e.target.value)}
                  />
                  {localErrors.monthlyIncome && (
                    <span className="form-error">{localErrors.monthlyIncome}</span>
                  )}
                </div>

                <div className="form-group">
                  <label className="form-label">Monthly Expenses (₹)</label>
                  <input
                    className="form-input"
                    type="number"
                    min={0}
                    placeholder="e.g. 25000"
                    value={form.monthlyExpenses}
                    onChange={e => set('monthlyExpenses', e.target.value)}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Existing EMI Total (₹)</label>
                  <input
                    className="form-input"
                    type="number"
                    min={0}
                    placeholder="Sum of all current EMIs"
                    value={form.existingEmiTotal}
                    onChange={e => set('existingEmiTotal', e.target.value)}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Active Loans Count</label>
                  <input
                    className="form-input"
                    type="number"
                    min={0}
                    max={20}
                    placeholder="e.g. 1"
                    value={form.numberOfActiveLoans}
                    onChange={e => set('numberOfActiveLoans', e.target.value)}
                  />
                </div>

              </div>

              {/* Live FOIR preview — only show when income is entered */}
              {income > 0 && (
                <div className="fin-profile-page__preview">
                  <div className="fin-profile-page__preview-item">
                    <span className="fin-profile-page__preview-label">Monthly Savings</span>
                    <span className="fin-profile-page__preview-value">
                      {formatCurrency(savings)}
                    </span>
                  </div>
                  <div className="fin-profile-page__preview-item">
                    <span className="fin-profile-page__preview-label">FOIR</span>
                    <span className={`fin-profile-page__preview-value ${foirClass}`}>
                      {formatPercent(foir)}
                    </span>
                  </div>
                  <div className="fin-profile-page__preview-item">
                    <span className="fin-profile-page__preview-label">Lenders prefer</span>
                    <span className="fin-profile-page__preview-value">
                      &lt; 50%
                    </span>
                  </div>
                </div>
              )}
            </div>

            {/* ── Section: Credit Profile ─────────────────── */}
            <div className="fin-profile-page__section">
              <p className="fin-profile-page__section-title">Credit Profile</p>
              <div className="form-grid">

                <div className="form-group">
                  <label className="form-label">CIBIL Score</label>
                  <input
                    className={`form-input${localErrors.creditScore ? ' input-error' : ''}`}
                    type="number"
                    min={300}
                    max={900}
                    placeholder="300–900 (optional)"
                    value={form.creditScore}
                    onChange={e => set('creditScore', e.target.value)}
                  />
                  {localErrors.creditScore ? (
                    <span className="form-error">{localErrors.creditScore}</span>
                  ) : (
                    <span className="fin-profile-page__hint">
                      Leave blank if you don't know — we'll estimate.
                    </span>
                  )}
                </div>

                <div className="form-group">
                  <label className="form-label">Credit Cards Count</label>
                  <input
                    className="form-input"
                    type="number"
                    min={0}
                    max={20}
                    placeholder="e.g. 2"
                    value={form.numberOfCreditCards}
                    onChange={e => set('numberOfCreditCards', e.target.value)}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Total Credit Limit (₹)</label>
                  <input
                    className="form-input"
                    type="number"
                    min={0}
                    placeholder="Sum of all card limits"
                    value={form.totalCreditLimit}
                    onChange={e => set('totalCreditLimit', e.target.value)}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Credit Utilization (%)</label>
                  <input
                    className={`form-input${localErrors.currentCreditUtilization ? ' input-error' : ''}`}
                    type="number"
                    min={0}
                    max={100}
                    placeholder="0–100"
                    value={form.currentCreditUtilization}
                    onChange={e => set('currentCreditUtilization', e.target.value)}
                  />
                  {localErrors.currentCreditUtilization && (
                    <span className="form-error">{localErrors.currentCreditUtilization}</span>
                  )}
                </div>

              </div>
            </div>

            {/* ── Section: Preferences ────────────────────── */}
            <div className="fin-profile-page__section">
              <p className="fin-profile-page__section-title">Preferences</p>
              <div className="form-grid">

                <div className="form-group">
                  <label className="form-label">Preferred Reward Type</label>
                  <select
                    className="form-input"
                    value={form.preferredRewardType}
                    onChange={e => set('preferredRewardType', e.target.value)}
                  >
                    <option value="">No preference</option>
                    {REWARD_TYPES.map(r => (
                      <option key={r} value={r}>{r.replace('_', ' ')}</option>
                    ))}
                  </select>
                </div>

                <div className="form-group">
                  <label className="form-label">Top Spending Category</label>
                  <select
                    className="form-input"
                    value={form.topSpendingCategory}
                    onChange={e => set('topSpendingCategory', e.target.value)}
                  >
                    <option value="">Select category</option>
                    {SPEND_CATS.map(c => (
                      <option key={c} value={c}>{c}</option>
                    ))}
                  </select>
                </div>

              </div>
            </div>

            {/* API-level error */}
            {saveError && (
              <div className="alert alert-error fin-profile-page__api-error">
                {saveError}
              </div>
            )}

            <div className="fin-profile-page__submit">
              <button
                type="submit"
                className="btn btn-primary btn-full btn-lg"
                disabled={isSaving}
              >
                {isSaving ? (
                  <><span className="spinner" /> Saving…</>
                ) : (
                  profile ? 'Update Profile' : 'Save & Go to Dashboard →'
                )}
              </button>
            </div>

          </form>
        </div>

      </div>
    </div>
  );
};

export default FinancialProfilePage;