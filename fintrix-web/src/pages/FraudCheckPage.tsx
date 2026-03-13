// ================================================================
// FraudCheckPage.tsx  —  /fraud-check  (protected)
// POST /api/v1/fraud/check
// GET  /api/v1/fraud/my-alerts  (history, loaded on mount)
//
// FraudCheckRequest:
//   entityName  @NotBlank
//   entityType  @NotNull  EntityType enum
//
// FraudCheckResponse:
//   isSafe, severity, isSebiRegistered, isRbiRegistered,
//   redFlags[], safetyTips[], verdict, regulatorCheckUrl?
// ================================================================

import { useState } from 'react';
import type { FormEvent } from 'react';
import useFraudCheck from '../hooks/useFraudCheck';
import { ENTITY_TYPE_LABELS, ALERT_SEVERITY_LABELS } from '../utils/constants';
import type { FraudCheckRequest, FraudCheckResponse, EntityType, AlertSeverity } from '../types/api.types';
import './FraudCheckPage.css';

// ── Verdict helpers ────────────────────────────────────────────
const verdictVariant = (r: FraudCheckResponse) =>
  r.isSafe              ? 'safe'   :
  r.severity === 'LOW'  ? 'warn'   :
  r.severity === 'MEDIUM' ? 'warn' :
  'danger';

const verdictIcon = (r: FraudCheckResponse) =>
  r.isSafe ? '✅' :
  r.severity === 'LOW' || r.severity === 'MEDIUM' ? '⚠️' : '🚨';

const severityBadgeClass = (s: AlertSeverity) =>
  s === 'LOW'      ? 'badge-success' :
  s === 'MEDIUM'   ? 'badge-warning' :
  s === 'HIGH'     ? 'badge-orange'  :
  'badge-danger';

const historyIcon = (r: FraudCheckResponse) =>
  r.isSafe ? '✅' :
  r.severity === 'LOW' ? '🟡' :
  r.severity === 'MEDIUM' ? '🟠' : '🔴';

// ── Verdict card ───────────────────────────────────────────────
const VerdictCard = ({ result }: { result: FraudCheckResponse }) => {
  const variant = verdictVariant(result);
  return (
    <div className={`fraud-page__verdict-card fraud-page__verdict-card--${variant}`}>
      <div className="fraud-page__verdict-header">
        <span className="fraud-page__verdict-icon">{verdictIcon(result)}</span>
        <div className="fraud-page__verdict-entity">
          <span className="fraud-page__verdict-name">{result.entityName}</span>
          <span className="fraud-page__verdict-type">
            {ENTITY_TYPE_LABELS[result.entityType as EntityType] ?? result.entityType}
          </span>
        </div>
        <span className={`badge ${severityBadgeClass(result.severity as AlertSeverity)}`}>
          {ALERT_SEVERITY_LABELS[result.severity as AlertSeverity] ?? result.severity}
        </span>
      </div>

      <p className={`fraud-page__verdict-text fraud-page__verdict-text--${variant}`}>
        {result.verdict}
      </p>

      {/* Regulator registrations */}
      <div className="fraud-page__reg-row">
        <div className={`fraud-page__reg-badge ${
          result.isSebiRegistered ? 'fraud-page__reg-badge--yes' : 'fraud-page__reg-badge--no'
        }`}>
          {result.isSebiRegistered ? '✓' : '✗'} SEBI Registered
        </div>
        <div className={`fraud-page__reg-badge ${
          result.isRbiRegistered ? 'fraud-page__reg-badge--yes' : 'fraud-page__reg-badge--no'
        }`}>
          {result.isRbiRegistered ? '✓' : '✗'} RBI Registered
        </div>
      </div>

      {/* Red flags */}
      {result.redFlags.length > 0 && (
        <div className="fraud-page__red-flags">
          {result.redFlags.map((f, i) => (
            <div key={i} className="fraud-page__red-flag">
              <span className="fraud-page__red-flag-icon">⛔</span>
              <span>{f}</span>
            </div>
          ))}
        </div>
      )}

      {/* Safety tips */}
      {result.safetyTips.length > 0 && (
        <div className="fraud-page__safety-tips">
          {result.safetyTips.map((t, i) => (
            <div key={i} className="fraud-page__safety-tip">
              <span className="fraud-page__safety-tip-dot" />
              <span>{t}</span>
            </div>
          ))}
        </div>
      )}

      {/* Regulator verify URL */}
      {result.regulatorCheckUrl && (
        <a
          className="fraud-page__reg-url"
          href={result.regulatorCheckUrl}
          target="_blank"
          rel="noopener noreferrer"
        >
          🔗 Verify on regulator website →
        </a>
      )}
    </div>
  );
};

// ── Page ───────────────────────────────────────────────────────
const FraudCheckPage = () => {
  const {
    result, history, isChecking, isFetching,
    checkError, fieldErrors, checkEntity, resetResult,
  } = useFraudCheck();

  const [form, setForm] = useState<FraudCheckRequest>({
    entityName: '',
    entityType: 'LENDER' as EntityType,
  });

  const [localErrors, setLocalErrors] = useState<Record<string, string>>({});

  const set = (key: keyof FraudCheckRequest, value: string) => {
    setForm(f => ({ ...f, [key]: value }));
    setLocalErrors(e => ({ ...e, [key]: '' }));
  };

  const validate = (): boolean => {
    const errs: Record<string, string> = {};
    if (!form.entityName.trim()) errs.entityName = 'Entity name is required';
    if (!form.entityType)        errs.entityType  = 'Entity type is required';
    setLocalErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    if (!validate()) return;
    checkEntity(form);
  };

  const mergedErrors = { ...localErrors, ...fieldErrors };

  return (
    <div className="fraud-page">

      <div className="fraud-page__header">
        <p className="fraud-page__eyebrow">Safety Tools</p>
        <h1 className="fraud-page__title">Fraud & Scam Check</h1>
        <p className="fraud-page__sub">
          Verify investment companies, lenders, and brokers before you invest or borrow.
        </p>
      </div>

      <div className="fraud-page__layout">

        {/* ── Left: Search form ─────────────────────── */}
        <div className="fraud-page__search-card">
          <p className="fraud-page__search-title">Check an Entity</p>

          <form className="fraud-page__form" onSubmit={handleSubmit}>

            <div className="form-group">
              <label className="form-label">
                Company / Entity Name <span className="required">*</span>
              </label>
              <input
                className={`form-input${mergedErrors.entityName ? ' input-error' : ''}`}
                placeholder="e.g. Zerodha, HDFC Bank"
                value={form.entityName}
                onChange={e => set('entityName', e.target.value)}
              />
              {mergedErrors.entityName && (
                <span className="form-error">{mergedErrors.entityName}</span>
              )}
            </div>

            <div className="form-group">
              <label className="form-label">
                Entity Type <span className="required">*</span>
              </label>
              <select
                className={`form-input${mergedErrors.entityType ? ' input-error' : ''}`}
                value={form.entityType}
                onChange={e => set('entityType', e.target.value)}
              >
                {(Object.entries(ENTITY_TYPE_LABELS) as [string, string][]).map(([val, label]) => (
                  <option key={val} value={val}>{label}</option>
                ))}
              </select>
              {mergedErrors.entityType && (
                <span className="form-error">{mergedErrors.entityType}</span>
              )}
            </div>

            {checkError && (
              <div className="alert alert-error">{checkError}</div>
            )}

            {result ? (
              <button
                type="button"
                className="btn btn-secondary btn-full"
                onClick={resetResult}
              >
                ← Check Another
              </button>
            ) : (
              <button
                type="submit"
                className="btn btn-primary btn-full btn-lg"
                disabled={isChecking}
              >
                {isChecking
                  ? <><span className="spinner" /> Checking…</>
                  : '🔍 Check Now'}
              </button>
            )}

          </form>
        </div>

        {/* ── Right: Results + History ─────────────── */}
        <div className="fraud-page__results">

          {/* Verdict */}
          {result && <VerdictCard result={result} />}

          {/* Empty state — no result yet */}
          {!result && (
            <div className="fraud-page__empty">
              <span className="fraud-page__empty-icon">🔍</span>
              <h3 className="fraud-page__empty-title">Enter an entity to verify</h3>
              <p className="fraud-page__empty-hint">
                We cross-check against SEBI, RBI, and known fraud databases.
              </p>
            </div>
          )}

          {/* Past checks */}
          {history.length > 0 && (
            <>
              <p className="fraud-page__history-title">
                Past Checks — {history.length}
              </p>
              <div className="fraud-page__history-list">
                {history.map((h, i) => (
                  <div key={i} className="fraud-page__history-item">
                    <span className="fraud-page__history-icon">{historyIcon(h)}</span>
                    <div className="fraud-page__history-info">
                      <p className="fraud-page__history-name">{h.entityName}</p>
                      <p className="fraud-page__history-type">
                        {ENTITY_TYPE_LABELS[h.entityType as EntityType] ?? h.entityType}
                      </p>
                    </div>
                    <span className={`badge ${severityBadgeClass(h.severity as AlertSeverity)}`}>
                      {h.isSafe ? 'Safe' : (ALERT_SEVERITY_LABELS[h.severity as AlertSeverity] ?? h.severity)}
                    </span>
                  </div>
                ))}
              </div>
            </>
          )}

          {isFetching && history.length === 0 && (
            <div style={{ display: 'flex', justifyContent: 'center', padding: 'var(--space-8)' }}>
              <div className="spinner" />
            </div>
          )}
        </div>

      </div>
    </div>
  );
};

export default FraudCheckPage;