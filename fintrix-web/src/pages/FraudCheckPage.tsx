// ================================================================
// FraudCheckPage.tsx  —  /fraud-check
//
// Two tabs — completely independent features:
//
// Tab 1: Entity Check (existing, unchanged)
//   POST /api/v1/fraud/check
//   User enters a company name + type
//   Checks SEBI/RBI registry + keyword match on name
//
// Tab 2: Message Scan (NEW)
//   POST /api/v1/fraud/scan
//   User pastes any text: WhatsApp, SMS, email, investment pitch
//   Backend scans 200+ keywords with fuzzy matching
//   Returns per-keyword breakdown with explanations
// ================================================================

import { useState, useEffect } from 'react';
import type { FormEvent } from 'react';
import useFraudCheck  from '../hooks/useFraudCheck';
import FraudService   from '../services/fraud.service';
import { ENTITY_TYPE_LABELS, ALERT_SEVERITY_LABELS } from '../utils/constants';
import type {
  FraudCheckRequest, FraudCheckResponse, EntityType, AlertSeverity,
  KeywordScanRequest, KeywordScanResponse, KeywordMatch, ContentTypeOption,
} from '../types/api.types';
import './FraudCheckPage.css';

// ── Shared severity helpers ────────────────────────────────────
const severityBadgeClass = (s: string) => ({
  SAFE:       'badge-success',
  UNVERIFIED: 'badge-warning',
  LOW:        'badge-info',
  MEDIUM:     'badge-warning',
  HIGH:       'badge-orange',
  CRITICAL:   'badge-danger',
}[s] ?? 'badge-info');

const verdictVariant = (r: FraudCheckResponse) => {
  if (r.isSafe || r.severity === 'SAFE') return 'safe';
  if (r.severity === 'LOW' || r.severity === 'MEDIUM' || r.severity === 'UNVERIFIED') return 'warn';
  return 'danger';
};

const historyIcon = (r: FraudCheckResponse) => ({
  SAFE: '✅', UNVERIFIED: '⚠️', LOW: '🟡', MEDIUM: '🟠', HIGH: '🔴', CRITICAL: '🚨',
}[r.severity] ?? '🔔');

// ── Keyword constants (mirrors SebiRegistrationRule + RbiNbfcRule) ──
const SEBI_KEYWORDS = [
  { kw: 'guaranteed return', label: 'Guaranteed Return' },
  { kw: 'guaranteed profit', label: 'Guaranteed Profit' },
  { kw: 'risk free',         label: 'Risk Free'         },
  { kw: 'double your money', label: 'Double Your Money' },
  { kw: 'triple your money', label: 'Triple Your Money' },
  { kw: '100% returns',      label: '100% Returns'      },
  { kw: 'whatsapp trading',  label: 'WhatsApp Trading'  },
  { kw: 'telegram trading',  label: 'Telegram Trading'  },
  { kw: 'ponzi',             label: 'Ponzi'              },
  { kw: 'mlm investment',    label: 'MLM Investment'    },
  { kw: 'multi level',       label: 'Multi Level'       },
  { kw: 'chain scheme',      label: 'Chain Scheme'      },
  { kw: 'assured return',    label: 'Assured Return'    },
  { kw: 'fixed return',      label: 'Fixed Return'      },
];

const RBI_KEYWORDS = [
  { kw: 'advance fee',               label: 'Advance Fee'               },
  { kw: 'processing fee advance',    label: 'Processing Fee Advance'    },
  { kw: 'registration fee',          label: 'Registration Fee'          },
  { kw: 'insurance fee before loan', label: 'Insurance Fee Before Loan' },
  { kw: 'rbi approved loan',         label: 'RBI Approved Loan'         },
  { kw: 'rbi certified',             label: 'RBI Certified'             },
  { kw: 'instant loan no documents', label: 'Instant Loan No Docs'      },
  { kw: 'no cibil check loan',       label: 'No CIBIL Check Loan'       },
  { kw: 'loan guaranteed approval',  label: 'Loan Guaranteed Approval'  },
  { kw: 'loan without kyc',          label: 'Loan Without KYC'          },
];

const detectKeywords = (name: string) => {
  const lower = name.toLowerCase();
  return {
    sebi: SEBI_KEYWORDS.filter(k => lower.includes(k.kw)),
    rbi:  RBI_KEYWORDS.filter(k => lower.includes(k.kw)),
  };
};

// ══════════════════════════════════════════════════════════════════
// TAB 1 — Entity Check (existing — zero changes to logic)
// ══════════════════════════════════════════════════════════════════

const VerdictCard = ({ result }: { result: FraudCheckResponse }) => {
  const variant = verdictVariant(result);
  return (
    <div className={`fraud-page__verdict-card fraud-page__verdict-card--${variant}`}>
      <div className="fraud-page__verdict-header">
        <div className="fraud-page__verdict-entity">
          <span className="fraud-page__verdict-name">{result.entityName}</span>
          <span className="fraud-page__verdict-type">
            {ENTITY_TYPE_LABELS[result.entityType as EntityType] ?? result.entityType}
          </span>
        </div>
        <span className={`badge ${severityBadgeClass(result.severity)}`}>
          {result.severityLabel ?? (ALERT_SEVERITY_LABELS[result.severity] ?? result.severity)}
        </span>
      </div>

      <p className={`fraud-page__verdict-text fraud-page__verdict-text--${variant}`}>
        {result.verdict}
      </p>

      {(result.registrationNumber || result.regulatorName) && (
        <div style={{
          display: 'flex', gap: 'var(--space-3)', flexWrap: 'wrap',
          padding: 'var(--space-3) var(--space-4)',
          background: 'rgba(16,185,129,0.06)',
          border: '1px solid var(--color-border-brand)',
          borderRadius: 'var(--radius-md)',
        }}>
          {result.regulatorName && (
            <div>
              <span style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)', textTransform: 'uppercase' }}>Regulator</span>
              <p style={{ fontFamily: 'var(--font-mono)', fontSize: 'var(--text-base)', color: 'var(--color-brand-primary)', fontWeight: 'var(--font-semibold)' }}>
                {result.regulatorName}
              </p>
            </div>
          )}
          {result.registrationNumber && (
            <div>
              <span style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)', textTransform: 'uppercase' }}>Registration No.</span>
              <p style={{ fontFamily: 'var(--font-mono)', fontSize: 'var(--text-base)', color: 'var(--color-text-primary)', fontWeight: 'var(--font-semibold)' }}>
                {result.registrationNumber}
              </p>
            </div>
          )}
        </div>
      )}

      <div className="fraud-page__reg-row">
        {result.isSebiRegistered != null && (
          <div className={`fraud-page__reg-badge ${result.isSebiRegistered ? 'fraud-page__reg-badge--yes' : 'fraud-page__reg-badge--no'}`}>
            {result.isSebiRegistered ? '✓' : '✗'} SEBI Registered
          </div>
        )}
        {result.isRbiRegistered != null && (
          <div className={`fraud-page__reg-badge ${result.isRbiRegistered ? 'fraud-page__reg-badge--yes' : 'fraud-page__reg-badge--no'}`}>
            {result.isRbiRegistered ? '✓' : '✗'} RBI Registered
          </div>
        )}
        {result.isSebiRegistered == null && result.isRbiRegistered == null && (
          <div className="fraud-page__reg-badge fraud-page__reg-badge--no">
            ✗ Not found in any regulator registry
          </div>
        )}
      </div>

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

      {result.regulatorCheckUrl && (
        <a className="fraud-page__reg-url" href={result.regulatorCheckUrl} target="_blank" rel="noopener noreferrer">
          🔗 Verify on official regulator website →
        </a>
      )}
    </div>
  );
};

const EntityCheckTab = () => {
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

  const validate = () => {
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
  const liveWarning = form.entityName.length >= 3 ? detectKeywords(form.entityName) : null;

  return (
    <div className="fraud-page__layout">
      {/* Left: Form */}
      <div className="fraud-page__search-card">
        <p className="fraud-page__search-title">Check an Entity</p>
        <form className="fraud-page__form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Company / Entity Name <span className="required">*</span></label>
            <input
              className={`form-input${mergedErrors.entityName ? ' input-error' : ''}`}
              placeholder="e.g. Zerodha, HDFC Bank"
              value={form.entityName}
              onChange={e => set('entityName', e.target.value)}
            />
            {mergedErrors.entityName && <span className="form-error">{mergedErrors.entityName}</span>}
            {liveWarning && [...liveWarning.sebi, ...liveWarning.rbi].length > 0 && (
              <div className="fraud-page__keyword-warning">
                <p className="fraud-page__keyword-warning-title">⚠ Suspicious phrase detected:</p>
                {[...liveWarning.sebi, ...liveWarning.rbi].map(h => (
                  <div key={h.kw}>• "{h.kw}" — known fraud pattern</div>
                ))}
              </div>
            )}
          </div>

          <div className="form-group">
            <label className="form-label">Entity Type <span className="required">*</span></label>
            <select
              className={`form-input${mergedErrors.entityType ? ' input-error' : ''}`}
              value={form.entityType}
              onChange={e => set('entityType', e.target.value)}
            >
              {(Object.entries(ENTITY_TYPE_LABELS) as [string, string][]).map(([val, label]) => (
                <option key={val} value={val}>{label}</option>
              ))}
            </select>
            {mergedErrors.entityType && <span className="form-error">{mergedErrors.entityType}</span>}
          </div>

          <div style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)', padding: 'var(--space-3)', background: 'var(--color-bg-input)', borderRadius: 'var(--radius-md)' }}>
            Checks against: SEBI registry · RBI NBFC list · fraud keyword database
          </div>

          {checkError && <div className="alert alert-error">{checkError}</div>}

          {result ? (
            <button type="button" className="btn btn-secondary btn-full" onClick={resetResult}>← Check Another</button>
          ) : (
            <button type="submit" className="btn btn-primary btn-full btn-lg" disabled={isChecking}>
              {isChecking ? <><span className="spinner" /> Checking…</> : '🔍 Check Now'}
            </button>
          )}
        </form>
      </div>

      {/* Right: Results + History */}
      <div className="fraud-page__results">
        {result && <VerdictCard result={result} />}

        {!result && (
          <>
            <div className="fraud-page__empty">
              <span className="fraud-page__empty-icon">🔍</span>
              <h3 className="fraud-page__empty-title">Enter an entity to verify</h3>
              <p className="fraud-page__empty-hint">We cross-check against SEBI, RBI registries and our fraud keyword database.</p>
            </div>

            <div className="fraud-page__patterns">
              <p className="fraud-page__patterns-title">Known Fraud Patterns</p>
              <div className="fraud-page__patterns-section">
                <p className="fraud-page__patterns-section-label">🚨 Investment / SEBI — critical risk phrases</p>
                <div className="fraud-page__patterns-tags">
                  {SEBI_KEYWORDS.map(k => (
                    <span key={k.kw} className="fraud-page__pattern-tag fraud-page__pattern-tag--critical"
                      onClick={() => set('entityName', k.label)} title={`Click to search: "${k.label}"`}>
                      {k.label}
                    </span>
                  ))}
                </div>
              </div>
              <div className="fraud-page__patterns-section">
                <p className="fraud-page__patterns-section-label">⚠ Loan / RBI — critical risk phrases</p>
                <div className="fraud-page__patterns-tags">
                  {RBI_KEYWORDS.map(k => (
                    <span key={k.kw} className="fraud-page__pattern-tag fraud-page__pattern-tag--warn"
                      onClick={() => set('entityName', k.label)} title={`Click to search: "${k.label}"`}>
                      {k.label}
                    </span>
                  ))}
                </div>
              </div>
              <p className="fraud-page__pattern-note">
                Click any tag to pre-fill the search. Additional keywords are checked server-side.
              </p>
            </div>
          </>
        )}

        {history.length > 0 && (
          <>
            <p className="fraud-page__history-title">Past Checks — {history.length}</p>
            <div className="fraud-page__history-list">
              {history.map((h, i) => (
                <div key={i} className="fraud-page__history-item">
                  <span className="fraud-page__history-icon">{historyIcon(h)}</span>
                  <div className="fraud-page__history-info">
                    <p className="fraud-page__history-name">{h.entityName}</p>
                    <p className="fraud-page__history-type">{ENTITY_TYPE_LABELS[h.entityType as EntityType] ?? h.entityType}</p>
                  </div>
                  <span className={`badge ${severityBadgeClass(h.severity as AlertSeverity)}`}>
                    {h.severityLabel ?? (ALERT_SEVERITY_LABELS[h.severity] ?? h.severity)}
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
  );
};

// ══════════════════════════════════════════════════════════════════
// TAB 2 — Message / Text Scan (NEW)
// ══════════════════════════════════════════════════════════════════

const riskVariant = (risk: string) => {
  if (risk === 'SAFE') return 'safe';
  if (risk === 'LOW' || risk === 'MEDIUM') return 'warn';
  return 'danger';
};

const riskIcon = (risk: string) => ({
  SAFE: '✅', LOW: '🔵', MEDIUM: '🟡', HIGH: '🔴', CRITICAL: '⛔',
}[risk] ?? '⚠️');

const matchRiskBadge = (level: string) => ({
  LOW: 'badge-info', MEDIUM: 'badge-warning', HIGH: 'badge-orange', CRITICAL: 'badge-danger',
}[level] ?? 'badge-info');

const MatchCard = ({ match, index }: { match: KeywordMatch; index: number }) => (
  <div style={{
    padding: 'var(--space-4)',
    background: 'var(--color-bg-elevated)',
    border: '1px solid var(--color-border-subtle)',
    borderRadius: 'var(--radius-md)',
    borderLeft: `3px solid var(--color-${match.riskLevel === 'CRITICAL' || match.riskLevel === 'HIGH' ? 'danger' : 'warning'})`,
  }}>
    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: 'var(--space-2)', marginBottom: 'var(--space-2)', flexWrap: 'wrap' }}>
      <div style={{ display: 'flex', gap: 'var(--space-2)', alignItems: 'center', flexWrap: 'wrap' }}>
        <span style={{ fontFamily: 'var(--font-mono)', fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)' }}>#{index + 1}</span>
        <code style={{ fontSize: 'var(--text-sm)', fontFamily: 'var(--font-mono)', color: 'var(--color-text-primary)', background: 'var(--color-bg-input)', padding: '2px 8px', borderRadius: 'var(--radius-sm)' }}>
          {match.keyword}
        </code>
        <span className={`badge ${matchRiskBadge(match.riskLevel)}`} style={{ fontSize: 'var(--text-xs)' }}>
          {match.riskLevel}
        </span>
        <span style={{ fontSize: 'var(--text-xs)', padding: '2px 8px', borderRadius: 'var(--radius-full)', background: match.matchType === 'FUZZY' ? 'rgba(234,179,8,0.1)' : 'rgba(16,185,129,0.1)', color: match.matchType === 'FUZZY' ? 'var(--color-warning)' : 'var(--color-brand-primary)', border: `1px solid ${match.matchType === 'FUZZY' ? 'var(--color-warning-border)' : 'var(--color-border-brand)'}` }}>
          {match.matchType === 'FUZZY' ? '≈ Fuzzy match' : '= Exact match'}
        </span>
      </div>
      <span className="badge badge-info" style={{ fontSize: 'var(--text-xs)' }}>{match.fraudType.replace(/_/g, ' ')}</span>
    </div>

    {/* Matched phrase from original text */}
    {match.matchedPhrase && (
      <blockquote style={{
        margin: 'var(--space-2) 0',
        padding: 'var(--space-2) var(--space-3)',
        borderLeft: '3px solid var(--color-border-brand)',
        background: 'var(--color-bg-input)',
        borderRadius: '0 var(--radius-sm) var(--radius-sm) 0',
        fontSize: 'var(--text-sm)',
        color: 'var(--color-text-muted)',
        fontStyle: 'italic',
      }}>
        "{match.matchedPhrase}"
      </blockquote>
    )}

    <p style={{ fontSize: 'var(--text-sm)', color: 'var(--color-text-secondary)', lineHeight: 'var(--leading-normal)' }}>
      {match.explanation}
    </p>
  </div>
);

const MessageScanTab = () => {
  const [contentTypes, setContentTypes]     = useState<ContentTypeOption[]>([]);
  const [form, setForm]                     = useState<KeywordScanRequest>({ text: '', contentType: 'OTHER' });
  const [isScanning, setIsScanning]         = useState(false);
  const [scanResult, setScanResult]         = useState<KeywordScanResponse | null>(null);
  const [scanError, setScanError]           = useState<string | null>(null);
  const [charCount, setCharCount]           = useState(0);

  useEffect(() => {
    FraudService.getContentTypes()
      .then(setContentTypes)
      .catch(() => {
        // Fallback static list if endpoint unavailable
        setContentTypes([
          { value: 'WHATSAPP_MESSAGE',  label: 'WhatsApp Message'           },
          { value: 'SMS',               label: 'SMS / Text Message'         },
          { value: 'EMAIL',             label: 'Email'                      },
          { value: 'SOCIAL_MEDIA_POST', label: 'Social Media Post'          },
          { value: 'WEBSITE_URL',       label: 'Website / URL'              },
          { value: 'PHONE_CALL_SCRIPT', label: 'Phone Call Script'          },
          { value: 'INVESTMENT_PITCH',  label: 'Investment Pitch / Brochure'},
          { value: 'LOAN_OFFER',        label: 'Loan Offer'                 },
          { value: 'JOB_OFFER',         label: 'Job Offer'                  },
          { value: 'OTHER',             label: 'Other'                      },
        ]);
      });
  }, []);

  const handleScan = async (e: FormEvent) => {
    e.preventDefault();
    if (!form.text.trim()) { setScanError('Paste some text to scan'); return; }
    setIsScanning(true);
    setScanError(null);
    setScanResult(null);
    try {
      const result = await FraudService.scanText(form);
      setScanResult(result);
    } catch (err: unknown) {
      setScanError((err as Error).message ?? 'Scan failed');
    } finally {
      setIsScanning(false);
    }
  };

  const reset = () => {
    setScanResult(null);
    setScanError(null);
    setForm({ text: '', contentType: 'OTHER' });
    setCharCount(0);
  };

  return (
    <div className="fraud-page__layout">
      {/* Left: Scan form */}
      <div className="fraud-page__search-card">
        <p className="fraud-page__search-title">Scan a Message or Text</p>
        <p style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-muted)', marginBottom: 'var(--space-4)', lineHeight: 'var(--leading-normal)' }}>
          Paste any suspicious text — WhatsApp message, SMS, investment pitch, email — and we scan it for 200+ fraud patterns using fuzzy matching.
        </p>

        <form className="fraud-page__form" onSubmit={handleScan}>
          <div className="form-group">
            <label className="form-label">Content Type</label>
            <select className="form-input" value={form.contentType}
              onChange={e => setForm(f => ({ ...f, contentType: e.target.value as KeywordScanRequest['contentType'] }))}>
              {contentTypes.map(ct => (
                <option key={ct.value} value={ct.value}>{ct.label}</option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label className="form-label">
              Paste Text to Scan <span className="required">*</span>
            </label>
            <textarea
              className="form-input"
              rows={8}
              maxLength={5000}
              placeholder={`Paste the suspicious message here...\n\nExamples:\n• "You have won ₹50 lakh lottery! Pay ₹500 processing fee..."\n• "Earn ₹5000/day from home — guaranteed returns!"\n• "KYC update required. Click here: http://..."`}
              value={form.text}
              onChange={e => {
                setForm(f => ({ ...f, text: e.target.value }));
                setCharCount(e.target.value.length);
              }}
              style={{ resize: 'vertical', minHeight: 160, fontFamily: 'var(--font-mono)', fontSize: 'var(--text-sm)' }}
            />
            <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
              <span style={{ fontSize: 'var(--text-xs)', color: charCount > 4500 ? 'var(--color-danger)' : 'var(--color-text-disabled)' }}>
                {charCount}/5000
              </span>
            </div>
          </div>

          {scanError && <div className="alert alert-error">{scanError}</div>}

          {scanResult ? (
            <button type="button" className="btn btn-secondary btn-full" onClick={reset}>← Scan Another</button>
          ) : (
            <button type="submit" className="btn btn-primary btn-full btn-lg" disabled={isScanning || !form.text.trim()}>
              {isScanning ? <><span className="spinner" /> Scanning…</> : '🔍 Scan for Fraud Patterns'}
            </button>
          )}
        </form>

        {/* What we scan for */}
        {!scanResult && (
          <div style={{ marginTop: 'var(--space-4)', padding: 'var(--space-3) var(--space-4)', background: 'var(--color-bg-input)', borderRadius: 'var(--radius-md)' }}>
            <p style={{ fontSize: 'var(--text-xs)', fontWeight: 'var(--font-semibold)', color: 'var(--color-text-muted)', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: 'var(--space-2)' }}>
              Detects
            </p>
            {[
              '💬 Task fraud — YouTube likes, app downloads, fake jobs',
              '🚔 Digital arrest scams — fake police/CBI/ED calls',
              '📈 Trading tips scams — Telegram/WhatsApp channels',
              '🏦 Loan fraud — advance fee, no-KYC instant loans',
              '🎰 Lottery & prize scams — KBC, lucky draw',
              '🔑 OTP & KYC phishing',
              '🌍 Job & visa scams',
              '🗣️ Hindi/Hinglish fraud phrases',
            ].map((item, i) => (
              <div key={i} style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-secondary)', marginBottom: 'var(--space-1)' }}>{item}</div>
            ))}
            <p style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)', marginTop: 'var(--space-2)', fontStyle: 'italic' }}>
              Uses fuzzy matching — catches typos like "guaranted returns"
            </p>
          </div>
        )}
      </div>

      {/* Right: Results */}
      <div className="fraud-page__results">
        {scanResult && (
          <>
            {/* Overall verdict */}
            <div className={`fraud-page__verdict-card fraud-page__verdict-card--${riskVariant(scanResult.overallRisk)}`}>
              <div className="fraud-page__verdict-header">
                <div className="fraud-page__verdict-entity">
                  <span className="fraud-page__verdict-name" style={{ fontSize: 'var(--text-xl)' }}>
                    {riskIcon(scanResult.overallRisk)} {scanResult.overallRisk}
                  </span>
                  <span className="fraud-page__verdict-type">{scanResult.contentTypeLabel}</span>
                </div>
                <div style={{ textAlign: 'right' }}>
                  <span style={{ fontFamily: 'var(--font-mono)', fontSize: 'var(--text-2xl)', fontWeight: 'var(--font-bold)', color: scanResult.isSafe ? 'var(--color-brand-primary)' : 'var(--color-danger)' }}>
                    {scanResult.totalMatchesFound}
                  </span>
                  <p style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-muted)' }}>
                    {scanResult.totalMatchesFound === 1 ? 'pattern found' : 'patterns found'}
                  </p>
                </div>
              </div>

              <p className={`fraud-page__verdict-text fraud-page__verdict-text--${riskVariant(scanResult.overallRisk)}`}>
                {scanResult.verdict}
              </p>

              {/* Scanned text preview */}
              {scanResult.scannedTextPreview && (
                <div style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)', fontStyle: 'italic', padding: 'var(--space-2) var(--space-3)', background: 'var(--color-bg-input)', borderRadius: 'var(--radius-sm)', fontFamily: 'var(--font-mono)' }}>
                  "{scanResult.scannedTextPreview}"
                </div>
              )}

              {/* Safety actions */}
              {scanResult.safetyActions.length > 0 && (
                <div className="fraud-page__safety-tips">
                  {scanResult.safetyActions.map((a, i) => (
                    <div key={i} className="fraud-page__safety-tip">
                      <span className="fraud-page__safety-tip-dot" />
                      <span>{a}</span>
                    </div>
                  ))}
                </div>
              )}

              {/* Report URL */}
              {scanResult.reportUrl && (
                <a className="fraud-page__reg-url" href={scanResult.reportUrl} target="_blank" rel="noopener noreferrer">
                  🚨 Report this fraud at sachet.rbi.org.in →
                </a>
              )}
            </div>

            {/* Per-keyword match breakdown */}
            {scanResult.matches.length > 0 && (
              <div style={{ marginTop: 'var(--space-5)' }}>
                <p style={{ fontSize: 'var(--text-xs)', fontWeight: 'var(--font-semibold)', letterSpacing: '0.05em', textTransform: 'uppercase', color: 'var(--color-text-muted)', marginBottom: 'var(--space-3)' }}>
                  Matched Patterns — {scanResult.matches.length} found (sorted by severity)
                </p>
                <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-3)' }}>
                  {scanResult.matches.map((m, i) => (
                    <MatchCard key={i} match={m} index={i} />
                  ))}
                </div>
              </div>
            )}
          </>
        )}

        {!scanResult && !isScanning && (
          <div className="fraud-page__empty">
            <span className="fraud-page__empty-icon" style={{ fontSize: '3rem' }}>💬</span>
            <h3 className="fraud-page__empty-title">Paste a suspicious message</h3>
            <p className="fraud-page__empty-hint">
              WhatsApp forwards, SMS, investment pitches, job offers — paste anything and we'll scan it instantly.
            </p>
          </div>
        )}
      </div>
    </div>
  );
};

// ══════════════════════════════════════════════════════════════════
// Main page — tab switcher
// ══════════════════════════════════════════════════════════════════

const FraudCheckPage = () => {
  const [activeTab, setActiveTab] = useState<'entity' | 'scan'>('entity');

  return (
    <div className="fraud-page">
      <div className="fraud-page__header">
        <p className="fraud-page__eyebrow">Safety Tools</p>
        <h1 className="fraud-page__title">Fraud & Scam Check</h1>
        <p className="fraud-page__sub">
          Verify companies against regulators, or scan any message for fraud patterns.
        </p>
      </div>

      {/* Tab switcher */}
      <div style={{
        display: 'flex', gap: 0,
        borderBottom: '1px solid var(--color-border-subtle)',
        marginBottom: 'var(--space-6)',
      }}>
        {([
          { key: 'entity', icon: '🏛', label: 'Entity Check',    desc: 'Verify a company by name' },
          { key: 'scan',   icon: '💬', label: 'Message Scan',    desc: 'Scan any text for scam patterns' },
        ] as const).map(tab => (
          <button
            key={tab.key}
            onClick={() => setActiveTab(tab.key)}
            style={{
              padding: 'var(--space-3) var(--space-5)',
              border: 'none',
              borderBottom: activeTab === tab.key
                ? '2px solid var(--color-brand-primary)'
                : '2px solid transparent',
              background: 'transparent',
              cursor: 'pointer',
              display: 'flex', flexDirection: 'column', alignItems: 'flex-start',
              gap: 2,
              transition: 'all var(--transition-fast)',
              marginBottom: -1,
            }}
          >
            <span style={{
              fontSize: 'var(--text-sm)', fontWeight: 'var(--font-semibold)',
              color: activeTab === tab.key ? 'var(--color-brand-primary)' : 'var(--color-text-muted)',
            }}>
              {tab.icon} {tab.label}
            </span>
            <span style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)' }}>
              {tab.desc}
            </span>
          </button>
        ))}
      </div>

      {activeTab === 'entity' ? <EntityCheckTab /> : <MessageScanTab />}
    </div>
  );
};

export default FraudCheckPage;