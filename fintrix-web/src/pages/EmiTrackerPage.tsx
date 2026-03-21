// ================================================================
// EmiTrackerPage.tsx  —  /emi-tracker  (protected)
//
// POST   /api/v1/emi-tracker      → add EMI
// GET    /api/v1/emi-tracker      → list (auto on mount)
// DELETE /api/v1/emi-tracker/{id} → remove
//
// EmiTrackerRequest fields:
//   loanName*        @NotBlank
//   lenderName       optional
//   loanType         optional free-text
//   principalAmount* @DecimalMin("1000")
//   emiAmount*       @DecimalMin("100")
//   dueDateOfMonth*  @Min(1) @Max(31)
//   startDate*       LocalDate
//   endDate*         LocalDate
//   reminderDaysBefore @Min(1)@Max(10), default 3
// ================================================================

import { useState } from 'react';
import type { FormEvent } from 'react';
import useEmiTracker          from '../hooks/useEmiTracker';
import { formatCurrency, formatDate } from '../utils/formatters';
import type { EmiTrackerRequest, EmiTrackerResponse } from '../types/api.types';
import './EmiTrackerPage.css';

// ── EMI card ───────────────────────────────────────────────────
const EmiCard = ({
  emi,
  onDelete,
}: {
  emi: EmiTrackerResponse;
  onDelete: (id: string) => void;
}) => (
  <div className={`emi-page__card${emi.isDueSoon ? ' emi-page__card--due-soon' : ''}`}>
    <div className="emi-page__card-header">
      <div>
        <p className="emi-page__card-name">{emi.loanName}</p>
        {emi.lenderName && (
          <p className="emi-page__card-lender">{emi.lenderName}</p>
        )}
      </div>
      {emi.loanType && (
        <span className="badge badge-info">{emi.loanType}</span>
      )}
    </div>

    <div className="emi-page__card-metrics">
      <div className="emi-page__card-metric">
        <span className="emi-page__card-metric-label">EMI / Month</span>
        <span className="emi-page__card-metric-value">
          {formatCurrency(emi.emiAmount)}
        </span>
      </div>
      <div className="emi-page__card-metric">
        <span className="emi-page__card-metric-label">Principal</span>
        <span className="emi-page__card-metric-value">
          {formatCurrency(emi.principalAmount, true)}
        </span>
      </div>
      <div className="emi-page__card-metric">
        <span className="emi-page__card-metric-label">Remaining</span>
        <span className="emi-page__card-metric-value">
          {emi.remainingEmis ?? '—'} EMIs
        </span>
      </div>
      <div className="emi-page__card-metric">
        <span className="emi-page__card-metric-label">Ends</span>
        <span className="emi-page__card-metric-value">
          {formatDate(emi.endDate)}
        </span>
      </div>
    </div>

    <div className="emi-page__card-footer">
      <span className="emi-page__due-label">
        {emi.dueDateLabel ?? `Due on ${emi.dueDateOfMonth}th of every month`}
      </span>
      <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--space-3)' }}>
        {emi.isDueSoon && (
          <span className="emi-page__due-soon-pill">⚠ Due Soon</span>
        )}
        <button
          className="btn btn-ghost btn-sm"
          onClick={() => onDelete(emi.id)}
          aria-label={`Remove ${emi.loanName}`}
        >
          Remove
        </button>
      </div>
    </div>
  </div>
);

// ── Page ───────────────────────────────────────────────────────
const EmiTrackerPage = () => {
  const {
    emis, isLoading, isAdding, error, fieldErrors,
    totalMonthlyEmi, dueSoonCount,
    addEmi, deleteEmi, clearErrors,
  } = useEmiTracker();

  const today = new Date().toISOString().slice(0, 10);

  const [form, setForm] = useState<EmiTrackerRequest>({
    loanName:           '',
    lenderName:         '',
    loanType:           '',
    principalAmount:    '' as unknown as number,
    emiAmount:          '' as unknown as number,
    dueDateOfMonth:     '' as unknown as number,
    startDate:          today,
    endDate:            '',
    reminderDaysBefore: 3,
  });

  const set = (key: keyof EmiTrackerRequest, val: string | number) => {
    setForm(f => ({ ...f, [key]: val }));
  };

  const [localErrors, setLocalErrors] = useState<Record<string, string>>({});

  const validate = (): boolean => {
    const e: Record<string, string> = {};
    if (!form.loanName.trim())    e.loanName        = 'Loan name is required';
    if (!form.principalAmount)    e.principalAmount  = 'Principal amount is required';
    if (Number(form.principalAmount) < 1000) e.principalAmount = 'Minimum ₹1,000';
    if (!form.emiAmount)          e.emiAmount        = 'EMI amount is required';
    if (Number(form.emiAmount) < 100) e.emiAmount    = 'Minimum ₹100';
    if (!form.dueDateOfMonth)     e.dueDateOfMonth   = 'Due date is required';
    if (!form.startDate)          e.startDate        = 'Start date is required';
    if (!form.endDate)            e.endDate          = 'End date is required';
    setLocalErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    clearErrors();
    if (!validate()) return;
    addEmi(
      {
        ...form,
        principalAmount:    Number(form.principalAmount),
        emiAmount:          Number(form.emiAmount),
        dueDateOfMonth:     Number(form.dueDateOfMonth),
        reminderDaysBefore: Number(form.reminderDaysBefore),
      },
      () => {
        // Reset form on success
        setForm({
          loanName: '', lenderName: '', loanType: '',
          principalAmount: '' as unknown as number,
          emiAmount: '' as unknown as number,
          dueDateOfMonth: '' as unknown as number,
          startDate: today, endDate: '',
          reminderDaysBefore: 3,
        });
        setLocalErrors({});
      },
    );
  };

  const merged = { ...localErrors, ...fieldErrors };

  return (
    <div className="emi-page">

      <div className="emi-page__header">
        <p className="emi-page__eyebrow">Finance Tools</p>
        <h1 className="emi-page__title">EMI Tracker</h1>
        <p className="emi-page__sub">
          Track all your active loan EMIs and get reminders before due dates.
        </p>
      </div>

      {/* ── Summary ──────────────────────────────────────── */}
      <div className="emi-page__summary">
        <div className="emi-page__summary-card">
          <span className="emi-page__summary-label">Active EMIs</span>
          <span className="emi-page__summary-value">{emis.length}</span>
        </div>
        <div className="emi-page__summary-card">
          <span className="emi-page__summary-label">Total / Month</span>
          <span className="emi-page__summary-value">
            {formatCurrency(totalMonthlyEmi, true)}
          </span>
        </div>
        <div className="emi-page__summary-card">
          <span className="emi-page__summary-label">Due Soon</span>
          <span className={`emi-page__summary-value${dueSoonCount > 0 ? ' emi-page__summary-value--warn' : ''}`}>
            {dueSoonCount}
          </span>
        </div>
      </div>

      <div className="emi-page__layout">

        {/* ── Add EMI form ───────────────────────────────── */}
        <div className="emi-page__form-card">
          <p className="emi-page__form-title">Add EMI</p>
          <form className="emi-page__form" onSubmit={handleSubmit}>

            <div className="form-group">
              <label className="form-label">Loan Name *</label>
              <input
                className={`form-input${merged.loanName ? ' input-error' : ''}`}
                placeholder="e.g. HDFC Home Loan"
                value={form.loanName}
                onChange={e => set('loanName', e.target.value)}
              />
              {merged.loanName && <span className="form-error">{merged.loanName}</span>}
            </div>

            <div className="form-grid">
              <div className="form-group">
                <label className="form-label">Lender</label>
                <input
                  className="form-input"
                  placeholder="e.g. HDFC Bank"
                  value={form.lenderName}
                  onChange={e => set('lenderName', e.target.value)}
                />
              </div>
              <div className="form-group">
                <label className="form-label">Loan Type</label>
                <input
                  className="form-input"
                  placeholder="e.g. Home Loan"
                  value={form.loanType}
                  onChange={e => set('loanType', e.target.value)}
                />
              </div>
            </div>

            <div className="form-grid">
              <div className="form-group">
                <label className="form-label">Principal (₹) *</label>
                <input
                  className={`form-input${merged.principalAmount ? ' input-error' : ''}`}
                  type="number" min={1000}
                  placeholder="e.g. 2500000"
                  value={form.principalAmount}
                  onChange={e => set('principalAmount', e.target.value)}
                />
                {merged.principalAmount && <span className="form-error">{merged.principalAmount}</span>}
              </div>
              <div className="form-group">
                <label className="form-label">EMI Amount (₹) *</label>
                <input
                  className={`form-input${merged.emiAmount ? ' input-error' : ''}`}
                  type="number" min={100}
                  placeholder="e.g. 22000"
                  value={form.emiAmount}
                  onChange={e => set('emiAmount', e.target.value)}
                />
                {merged.emiAmount && <span className="form-error">{merged.emiAmount}</span>}
              </div>
            </div>

            <div className="form-group">
              <label className="form-label">Due Date of Month (1–31) *</label>
              <input
                className={`form-input${merged.dueDateOfMonth ? ' input-error' : ''}`}
                type="number" min={1} max={31}
                placeholder="e.g. 5"
                value={form.dueDateOfMonth}
                onChange={e => set('dueDateOfMonth', e.target.value)}
              />
              {merged.dueDateOfMonth && <span className="form-error">{merged.dueDateOfMonth}</span>}
            </div>

            <div className="form-grid">
              <div className="form-group">
                <label className="form-label">Start Date *</label>
                <input
                  className={`form-input${merged.startDate ? ' input-error' : ''}`}
                  type="date"
                  value={form.startDate}
                  onChange={e => set('startDate', e.target.value)}
                />
                {merged.startDate && <span className="form-error">{merged.startDate}</span>}
              </div>
              <div className="form-group">
                <label className="form-label">End Date *</label>
                <input
                  className={`form-input${merged.endDate ? ' input-error' : ''}`}
                  type="date"
                  value={form.endDate}
                  onChange={e => set('endDate', e.target.value)}
                />
                {merged.endDate && <span className="form-error">{merged.endDate}</span>}
              </div>
            </div>

            <div className="form-group">
              <label className="form-label">Remind me (days before due)</label>
              <input
                className="form-input"
                type="number" min={1} max={10}
                value={form.reminderDaysBefore}
                onChange={e => set('reminderDaysBefore', e.target.value)}
              />
            </div>

            {error && <div className="alert alert-error">{error}</div>}

            <button
              type="submit"
              className="btn btn-primary btn-full btn-lg"
              disabled={isAdding}
            >
              {isAdding
                ? <><span className="spinner" /> Adding…</>
                : '+ Add EMI'}
            </button>
          </form>
        </div>

        {/* ── EMI list ───────────────────────────────────── */}
        <div className="emi-page__list">
          <div className="emi-page__list-header">
            <span className="emi-page__list-title">
              Your EMIs — {emis.length}
            </span>
          </div>

          {isLoading && (
            <div style={{ display: 'flex', justifyContent: 'center', padding: 'var(--space-12)' }}>
              <div className="spinner spinner-lg" />
            </div>
          )}

          {!isLoading && emis.length === 0 && (
            <div className="emi-page__empty">
              <span className="emi-page__empty-icon">💳</span>
              <h3 className="emi-page__empty-title">No EMIs tracked yet</h3>
              <p className="emi-page__empty-hint">
                Add your active loan EMIs to track due dates and get reminders.
              </p>
            </div>
          )}

          {emis.map(emi => (
            <EmiCard key={emi.id} emi={emi} onDelete={deleteEmi} />
          ))}
        </div>

      </div>
    </div>
  );
};

export default EmiTrackerPage;