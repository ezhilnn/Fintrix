// ================================================================
// LoanEligibilityPage.tsx  —  /loan-eligibility  (protected)
// POST /api/v1/loans/check-eligibility
//
// LoanEligibilityRequest:
//   loanType        @NotNull  LoanType enum
//   requestedAmount @DecimalMin("10000") @DecimalMax("100000000")
//   tenureMonths    @Min(3) @Max(360)
//   purpose         optional string
// ================================================================

import { useState, useEffect } from 'react';
import type { FormEvent } from 'react';
import useLoanEligibility  from '../hooks/useLoanEligibility';
import TrackingService     from '../services/tracking.service';
import { LOAN_TYPE_LABELS, TRACK_EVENTS } from '../utils/constants';
import { formatCurrency, formatPercent, calculateEmi, formatTenure } from '../utils/formatters';
import { validateLoanAmount, validateTenure } from '../utils/scoreHelpers';
import type { LoanEligibilityRequest, LenderResult } from '../types/loan.types';
import type { LoanType } from '../types/loan.types';
import './LoanEligibilityPage.css';

// ── Approval pill helper ───────────────────────────────────────
const approvalPillClass = (prob: number) =>
  prob >= 70 ? 'loan-page__approval-pill--high' :
  prob >= 40 ? 'loan-page__approval-pill--med' :
               'loan-page__approval-pill--low';

// ── Apply Now button ──────────────────────────────────────────
// Priority: 1. direct applyUrl from lender record
//           2. affiliate tracked link (if partnership exists)
//           3. fallback Google search
const ApplyButton = ({
  entityId,
  approvalProbability,
  applyUrl,
}: {
  entityId: string;
  approvalProbability: number;
  applyUrl?: string;
}) => {
  const [loading, setLoading] = useState(false);

  const handleApply = async () => {
    setLoading(true);
    TrackingService.trackEvent({
      eventType: TRACK_EVENTS.APPLY_CLICK,
      entityId,
      page: '/loan',
    });

    // Use direct lender apply URL if configured — no API call needed
    if (applyUrl) {
      window.open(applyUrl, '_blank', 'noopener,noreferrer');
      setLoading(false);
      return;
    }

    // Fall back to affiliate tracking link
    try {
      const link = await TrackingService.getAffiliateLink(entityId, 'LOAN', approvalProbability);
      if (link.hasPartnership && link.trackedUrl) {
        window.open(link.trackedUrl, '_blank', 'noopener,noreferrer');
      } else {
        window.open(
          `https://www.google.com/search?q=${encodeURIComponent(entityId + ' loan apply')}`,
          '_blank', 'noopener,noreferrer'
        );
      }
    } catch {
      // silent fallback
    } finally {
      setLoading(false);
    }
  };

  return (
    <button className="btn btn-primary btn-sm" onClick={handleApply} disabled={loading}>
      {loading ? <><span className="spinner" /> Opening…</> : 'Apply Now →'}
    </button>
  );
};

// ── Lender card ────────────────────────────────────────────────
const LenderCard = ({ lender }: { lender: LenderResult }) => (
  <div className={`loan-page__lender-card ${
    lender.isEligible
      ? 'loan-page__lender-card--eligible'
      : 'loan-page__lender-card--ineligible'
  }`}>
    <div className="loan-page__lender-header">
      {lender.logoUrl && (
        <img
          src={lender.logoUrl}
          alt={lender.lenderName}
          style={{ height: 26, width: 'auto', objectFit: 'contain', borderRadius: 4, flexShrink: 0 }}
          onError={e => { (e.target as HTMLImageElement).style.display = 'none'; }}
        />
      )}
      <span className="loan-page__lender-name">{lender.lenderName}</span>
      <span className={`loan-page__approval-pill ${approvalPillClass(lender.approvalProbability)}`}>
        {lender.approvalProbability}% approval
      </span>
    </div>

    <div className="loan-page__lender-metrics">
      <div className="loan-page__lender-metric">
        <span className="loan-page__lender-metric-label">Interest Rate</span>
        <span className="loan-page__lender-metric-value">
          {formatPercent(lender.minInterestRate)}–{formatPercent(lender.maxInterestRate)}
        </span>
      </div>
      <div className="loan-page__lender-metric">
        <span className="loan-page__lender-metric-label">Est. EMI</span>
        <span className="loan-page__lender-metric-value">
          {formatCurrency(lender.estimatedEmi, true)}
        </span>
      </div>
      <div className="loan-page__lender-metric">
        <span className="loan-page__lender-metric-label">Total Interest</span>
        <span className="loan-page__lender-metric-value">
          {formatCurrency(lender.totalInterestPayable, true)}
        </span>
      </div>
      <div className="loan-page__lender-metric">
        <span className="loan-page__lender-metric-label">Processing Fee</span>
        <span className="loan-page__lender-metric-value">
          {formatPercent(lender.processingFeePercent)}
        </span>
      </div>
    </div>

    {/* Failure reasons */}
    {lender.failureReasons.length > 0 && (
      <div className="loan-page__lender-tags">
        {lender.failureReasons.map((r, i) => (
          <span key={i} className="loan-page__lender-tag loan-page__lender-tag--reason">
            {r}
          </span>
        ))}
      </div>
    )}

    {/* Improvement tips */}
    {lender.improvementTips.length > 0 && (
      <div className="loan-page__lender-tags">
        {lender.improvementTips.map((t, i) => (
          <span key={i} className="loan-page__lender-tag loan-page__lender-tag--tip">
            💡 {t}
          </span>
        ))}
      </div>
    )}

    {/* Apply Now — only for eligible lenders */}
    {lender.isEligible && (
      <div style={{ paddingTop: 'var(--space-3)', borderTop: '1px solid var(--color-border-subtle)' }}>
        <ApplyButton
          entityId={lender.lenderId}
          approvalProbability={lender.approvalProbability}
          applyUrl={lender.applyUrl}
        />
      </div>
    )}
  </div>
);

// ── Page ───────────────────────────────────────────────────────
const LoanEligibilityPage = () => {
  const { result, isChecking, error, fieldErrors, checkEligibility, reset } = useLoanEligibility();

  useEffect(() => {
    TrackingService.trackEvent({ eventType: TRACK_EVENTS.PAGE_VIEW, page: '/loan' });
  }, []);

  const [form, setForm] = useState<LoanEligibilityRequest>({
    loanType:        'PERSONAL_LOAN' as LoanType,
    requestedAmount: '' as unknown as number,
    tenureMonths:    '' as unknown as number,
    purpose:         '',
  });

  const [localErrors, setLocalErrors] = useState<Record<string, string>>({});

  const set = (key: keyof LoanEligibilityRequest, value: string | number) => {
    setForm(f => ({ ...f, [key]: value }));
    setLocalErrors(e => ({ ...e, [key]: '' }));
  };

  // Live EMI estimate
  const liveEmi = form.requestedAmount && form.tenureMonths
    ? calculateEmi(Number(form.requestedAmount), 12, Number(form.tenureMonths))
    : 0;

  const validate = (): boolean => {
    const errs: Record<string, string> = {};
    const amtErr = validateLoanAmount(Number(form.requestedAmount));
    const tenErr = validateTenure(Number(form.tenureMonths));
    if (!form.loanType)  errs.loanType = 'Select a loan type';
    if (amtErr) errs.requestedAmount = amtErr;
    if (tenErr) errs.tenureMonths    = tenErr;
    setLocalErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    if (!validate()) return;
    checkEligibility({
      ...form,
      requestedAmount: Number(form.requestedAmount),
      tenureMonths:    Number(form.tenureMonths),
    });
  };

  // Merge backend field errors
  const mergedErrors = { ...localErrors, ...fieldErrors };

  return (
    <div className="loan-page">

      <div className="loan-page__header">
        <p className="loan-page__eyebrow">Loan Tools</p>
        <h1 className="loan-page__title">Loan Eligibility Check</h1>
        <p className="loan-page__sub">
          See which lenders will likely approve your application — before you apply.
        </p>
      </div>

      <div className="loan-page__layout">

        {/* ── Left: Form ─────────────────────────────── */}
        <div className="loan-page__form-card">
          <p className="loan-page__form-title">Loan Details</p>

          <form className="loan-page__form-grid" onSubmit={handleSubmit}>

            <div className="form-group">
              <label className="form-label">
                Loan Type <span className="required">*</span>
              </label>
              <select
                className={`form-input${mergedErrors.loanType ? ' input-error' : ''}`}
                value={form.loanType}
                onChange={e => set('loanType', e.target.value)}
              >
                {(Object.entries(LOAN_TYPE_LABELS) as [string, string][]).map(([val, label]) => (
                  <option key={val} value={val}>{label}</option>
                ))}
              </select>
              {mergedErrors.loanType && (
                <span className="form-error">{mergedErrors.loanType}</span>
              )}
            </div>

            <div className="form-group">
              <label className="form-label">
                Loan Amount (₹) <span className="required">*</span>
              </label>
              <input
                className={`form-input${mergedErrors.requestedAmount ? ' input-error' : ''}`}
                type="number"
                min={10000}
                placeholder="e.g. 500000"
                value={form.requestedAmount}
                onChange={e => set('requestedAmount', e.target.value)}
              />
              {mergedErrors.requestedAmount && (
                <span className="form-error">{mergedErrors.requestedAmount}</span>
              )}
            </div>

            <div className="form-group">
              <label className="form-label">
                Tenure (months) <span className="required">*</span>
              </label>
              <input
                className={`form-input${mergedErrors.tenureMonths ? ' input-error' : ''}`}
                type="number"
                min={1}
                max={360}
                placeholder="e.g. 36"
                value={form.tenureMonths}
                onChange={e => set('tenureMonths', e.target.value)}
              />
              {mergedErrors.tenureMonths ? (
                <span className="form-error">{mergedErrors.tenureMonths}</span>
              ) : form.tenureMonths ? (
                <span style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)' }}>
                  {formatTenure(Number(form.tenureMonths))}
                </span>
              ) : null}
            </div>

            <div className="form-group">
              <label className="form-label">Purpose</label>
              <input
                className="form-input"
                placeholder="e.g. Home renovation (optional)"
                value={form.purpose}
                onChange={e => set('purpose', e.target.value)}
              />
            </div>

            {/* Live EMI preview */}
            {liveEmi > 0 && (
              <div className="loan-page__emi-preview">
                <span className="loan-page__emi-label">Estimated EMI (at 12%)</span>
                <span className="loan-page__emi-value">{formatCurrency(liveEmi)}/mo</span>
                <span className="loan-page__emi-interest">
                  Actual rate depends on lender & profile
                </span>
              </div>
            )}

            {error && (
              <div className="alert alert-error">{error}</div>
            )}

            {result ? (
              <button
                type="button"
                className="btn btn-secondary btn-full"
                onClick={reset}
              >
                ← New Check
              </button>
            ) : (
              <button
                type="submit"
                className="btn btn-primary btn-full btn-lg"
                disabled={isChecking}
              >
                {isChecking
                  ? <><span className="spinner" /> Checking…</>
                  : 'Check Eligibility →'}
              </button>
            )}

          </form>
        </div>

        {/* ── Right: Results ────────────────────────── */}
        <div className="loan-page__results">

          {!result ? (
            <div className="loan-page__results-empty">
              <span className="loan-page__results-empty-icon">🏦</span>
              <h3 className="loan-page__results-empty-title">
                Your lender matches will appear here
              </h3>
              <p className="loan-page__results-empty-hint">
                Fill in the loan details and check eligibility to see results.
              </p>
            </div>
          ) : (
            <>
              {/* Summary bar */}
              <div className="loan-page__summary-bar">
                <h2 className="loan-page__summary-bar-title">Results</h2>
                <span className="badge badge-success">
                  {result.eligibleLenders.length} Eligible
                </span>
                <span className="badge badge-danger">
                  {result.ineligibleLenders.length} Not Eligible
                </span>
              </div>

              {/* User profile snapshot used for this check */}
              <div className="loan-page__snapshot">
                <div className="loan-page__snapshot-item">
                  <span className="loan-page__snapshot-label">Your FOIR</span>
                  <span className="loan-page__snapshot-value">
                    {formatPercent(result.userFoir)}
                  </span>
                </div>
                <div className="loan-page__snapshot-item">
                  <span className="loan-page__snapshot-label">CIBIL Score</span>
                  <span className="loan-page__snapshot-value">
                    {result.userCreditScore
                      ? `${result.userCreditScore} ${result.userCreditScoreRange ? `(${result.userCreditScoreRange})` : ''}`
                      : 'Not set'}
                  </span>
                </div>
                <div className="loan-page__snapshot-item">
                  <span className="loan-page__snapshot-label">Monthly Income</span>
                  <span className="loan-page__snapshot-value">
                    {formatCurrency(result.userMonthlyIncome, true)}
                  </span>
                </div>
                <div className="loan-page__snapshot-item">
                  <span className="loan-page__snapshot-label">Loan Requested</span>
                  <span className="loan-page__snapshot-value">
                    {formatCurrency(result.requestedAmount, true)}
                  </span>
                </div>
              </div>

              {/* Overall suggestion */}
              {result.overallSuggestion && (
                <div className="loan-page__suggestion">
                  {result.overallSuggestion}
                </div>
              )}

              {/* Credit score warning */}
              {result.creditScoreWarning && (
                <div className="loan-page__cs-warning">
                  ⚠ {result.creditScoreWarning}
                </div>
              )}

              {/* Eligible lenders */}
              {result.eligibleLenders.length > 0 && (
                <>
                  <p className="loan-page__results-section-title">
                    Eligible Lenders — likely to approve
                  </p>
                  <div className="loan-page__lender-list">
                    {result.eligibleLenders.map(l => (
                      <LenderCard key={l.lenderId} lender={l} />
                    ))}
                  </div>
                </>
              )}

              {/* Ineligible lenders */}
              {result.ineligibleLenders.length > 0 && (
                <>
                  <p className="loan-page__results-section-title">
                    Other Lenders — improve profile to qualify
                  </p>
                  <div className="loan-page__lender-list">
                    {result.ineligibleLenders.map(l => (
                      <LenderCard key={l.lenderId} lender={l} />
                    ))}
                  </div>
                </>
              )}
            </>
          )}
        </div>

      </div>
    </div>
  );
};

export default LoanEligibilityPage;