// ================================================================
// DashboardPage.tsx  —  /dashboard
// GET /api/v1/bff/dashboard → DashboardResponse
//
// DashboardResponse now includes financialMetrics (DashboardFinancialMetrics)
// with 9 sections — this page renders all of them.
// ================================================================

import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import useDashboardStore from '../store/dashboardStore';
import { ROUTES } from '../utils/constants';
import { formatCurrency } from '../utils/formatters';
import { getHealthScoreBand, riskLevelColor } from '../utils/scoreHelpers';
import type { RiskLevel } from '../types/financialProfile.types';
import type {
  ScoreTrend,
  DashboardFinancialAlert,
  DashboardFinancialMetrics,
  DashboardEmiSummary,
  DashboardResponse,
} from '../types/api.types';
import './DashboardPage.css';

// ── Score ring SVG ────────────────────────────────────────────
const ScoreRing = ({ score, color }: { score: number; color: string }) => {
  const r      = 44;
  const circ   = 2 * Math.PI * r;
  const offset = circ - (score / 100) * circ;
  return (
    <svg className="dashboard-page__score-ring" viewBox="0 0 120 120">
      <circle className="dashboard-page__score-ring-bg" cx="60" cy="60" r={r} />
      <circle
        className="dashboard-page__score-ring-fill"
        cx="60" cy="60" r={r}
        strokeDasharray={circ}
        strokeDashoffset={offset}
        stroke={color}
      />
    </svg>
  );
};

// ── Sub-score bar ─────────────────────────────────────────────
const SubScoreBar = ({ label, value }: { label: string; value?: number }) => {
  const v = value ?? 0;
  const color = v >= 70 ? 'var(--color-brand-primary)' : v >= 40 ? 'var(--color-warning)' : 'var(--color-danger)';
  return (
    <div className="dashboard-page__sub-score">
      <span className="dashboard-page__sub-score-label">{label}</span>
      <div className="dashboard-page__sub-score-bar">
        <div className="dashboard-page__sub-score-fill" style={{ width: `${v}%`, background: color }} />
      </div>
      <span className="dashboard-page__sub-score-val" style={{ color }}>{v}</span>
    </div>
  );
};

// ── Sparkline trend chart ─────────────────────────────────────
const ScoreTrendChart = ({ trends }: { trends: ScoreTrend[] }) => {
  if (trends.length < 2) return null;
  const W = 280, H = 60, PAD = 8;
  const min = Math.max(0,   Math.min(...trends.map(t => t.score)) - 10);
  const max = Math.min(100, Math.max(...trends.map(t => t.score)) + 10);
  const xs  = trends.map((_, i) => PAD + (i / Math.max(trends.length - 1, 1)) * (W - PAD * 2));
  const ys  = trends.map(t => H - PAD - ((t.score - min) / (max - min || 1)) * (H - PAD * 2));
  const linePath = xs.map((x, i) => `${i === 0 ? 'M' : 'L'}${x.toFixed(1)},${ys[i].toFixed(1)}`).join(' ');
  const areaPath = `${linePath} L${xs[xs.length-1].toFixed(1)},${(H-PAD).toFixed(1)} L${xs[0].toFixed(1)},${(H-PAD).toFixed(1)} Z`;
  return (
    <svg viewBox={`0 0 ${W} ${H}`} style={{ width: '100%', height: H }}>
      <defs>
        <linearGradient id="trendGrad" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stopColor="var(--color-brand-primary)" stopOpacity="0.3" />
          <stop offset="100%" stopColor="var(--color-brand-primary)" stopOpacity="0" />
        </linearGradient>
      </defs>
      <path d={areaPath} fill="url(#trendGrad)" />
      <path d={linePath} fill="none" stroke="var(--color-brand-primary)" strokeWidth="2" strokeLinecap="round" />
      {xs.map((x, i) => (
        <circle key={i} cx={x} cy={ys[i]} r="3" fill="var(--color-brand-primary)" />
      ))}
    </svg>
  );
};

// ── Alert card ────────────────────────────────────────────────
const AlertCard = ({ alert }: { alert: DashboardFinancialAlert }) => (
  <div style={{
    padding: 'var(--space-3) var(--space-4)',
    borderRadius: 'var(--radius-md)',
    border: `1px solid ${alert.severity === 'DANGER' ? 'var(--color-danger-border)' : alert.severity === 'WARNING' ? 'var(--color-warning-border)' : 'var(--color-border-subtle)'}`,
    background: alert.severity === 'DANGER' ? 'var(--color-danger-bg)' : alert.severity === 'WARNING' ? 'var(--color-warning-bg)' : 'var(--color-bg-elevated)',
    display: 'flex', gap: 'var(--space-3)',
  }}>
    <span style={{ fontSize: '1.1rem', flexShrink: 0 }}>{alert.icon}</span>
    <div style={{ flex: 1, minWidth: 0 }}>
      <p style={{ fontSize: 'var(--text-sm)', fontWeight: 'var(--font-semibold)', color: alert.severity === 'DANGER' ? 'var(--color-danger-text)' : alert.severity === 'WARNING' ? 'var(--color-warning)' : 'var(--color-text-primary)', marginBottom: 2 }}>
        {alert.title}
      </p>
      <p style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-secondary)', lineHeight: 'var(--leading-normal)', marginBottom: 2 }}>
        {alert.message}
      </p>
      <p style={{ fontSize: 'var(--text-xs)', color: 'var(--color-brand-primary)', fontWeight: 'var(--font-medium)' }}>
        → {alert.action}
      </p>
    </div>
  </div>
);

// ── Section card wrapper ──────────────────────────────────────
const Section = ({ title, children }: { title: string; children: React.ReactNode }) => (
  <div className="dashboard-page__section-card">
    <p className="dashboard-page__section-card-title">{title}</p>
    {children}
  </div>
);

// ── Metric row item ───────────────────────────────────────────
const Metric = ({ label, value, sub, color }: { label: string; value: React.ReactNode; sub?: string; color?: string }) => (
  <div className="dashboard-page__stat">
    <span className="dashboard-page__stat-label">{label}</span>
    <span className="dashboard-page__stat-value" style={color ? { color } : undefined}>{value}</span>
    {sub && <span style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)' }}>{sub}</span>}
  </div>
);

// ── Readiness badge ───────────────────────────────────────────
const ReadinessBadge = ({ label }: { label?: string }) => {
  if (!label) return null;
  const isReady  = label.startsWith('✅');
  const isBorder = label.startsWith('🟡');
  return (
    <div style={{
      padding: 'var(--space-2) var(--space-3)',
      borderRadius: 'var(--radius-md)',
      background: isReady ? 'rgba(16,185,129,0.06)' : isBorder ? 'rgba(234,179,8,0.06)' : 'rgba(239,68,68,0.06)',
      border: `1px solid ${isReady ? 'var(--color-border-brand)' : isBorder ? 'var(--color-warning-border)' : 'var(--color-danger-border)'}`,
      fontSize: 'var(--text-sm)',
      color: isReady ? 'var(--color-brand-primary)' : isBorder ? 'var(--color-warning)' : 'var(--color-danger-text)',
    }}>
      {label}
    </div>
  );
};

// ══════════════════════════════════════════════════════════════
// Main page
// ══════════════════════════════════════════════════════════════

const DashboardPage = () => {
  const navigate = useNavigate();
  const { dashboard, isFetching, fetchError, isRecomputing, fetchDashboard, recomputeScore } =
    useDashboardStore();

  useEffect(() => {
    if (!dashboard) fetchDashboard();
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const user    = dashboard?.userProfile;
  const profile = dashboard?.financialProfile;
  const health  = dashboard?.healthScore;
  const m       = dashboard?.financialMetrics as DashboardFinancialMetrics | undefined;

  const score    = health?.overallScore ?? m?.overallHealthScore ?? 0;
  const band     = getHealthScoreBand(score);
  const ringColor = riskLevelColor(((health?.riskLevel ?? m?.riskLevel ?? 'MEDIUM') as RiskLevel));
  const firstName = user?.fullName?.split(' ')[0] ?? 'there';

  // ── Loading ──────────────────────────────────────────────────
  if (isFetching && !dashboard) {
    return (
      <div className="dashboard-page__loading">
        <div className="spinner spinner-lg" />
      </div>
    );
  }

  if (fetchError) {
    return (
      <div className="dashboard-page">
        <div className="alert alert-error">
          Failed to load dashboard: {fetchError}
          <button className="btn btn-primary btn-sm" style={{ marginLeft: 'var(--space-3)' }} onClick={fetchDashboard}>Retry</button>
        </div>
      </div>
    );
  }

  return (
    <div className="dashboard-page">

      {/* ── Page header ──────────────────────────────── */}
      <div className="dashboard-page__header">
        <p className="dashboard-page__eyebrow">
          {new Date().toLocaleDateString('en-IN', { weekday: 'long', day: 'numeric', month: 'long' })}
        </p>
        <h1 className="dashboard-page__title">Hello, {firstName} 👋</h1>
        {dashboard?.nextActionPrompt && (
          <p className="dashboard-page__sub">{dashboard.nextActionPrompt}</p>
        )}
      </div>

      {/* ── Setup banners ────────────────────────────── */}
      {dashboard && !dashboard.isProfileComplete && (
        <div className="dashboard-page__setup-banner">
          <div className="dashboard-page__setup-text">
            <span className="dashboard-page__setup-title">Complete your basic profile</span>
            <span className="dashboard-page__setup-hint">Add your name, age, and city to get started</span>
          </div>
          <button className="btn btn-primary btn-sm" onClick={() => navigate(ROUTES.USER_PROFILE)}>Complete now →</button>
        </div>
      )}
      {dashboard?.isProfileComplete && !dashboard?.isFinancialProfileComplete && (
        <div className="dashboard-page__setup-banner">
          <div className="dashboard-page__setup-text">
            <span className="dashboard-page__setup-title">Complete your financial profile</span>
            <span className="dashboard-page__setup-hint">Add income and EMI details to unlock all features</span>
          </div>
          <button className="btn btn-primary btn-sm" onClick={() => navigate(ROUTES.FINANCIAL_PROFILE)}>Complete now →</button>
        </div>
      )}

      {/* ── Smart alerts (Section 8) — top priority ──── */}
      {m?.alerts && m.alerts.length > 0 && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-2)', marginBottom: 'var(--space-5)' }}>
          {m.alerts.map((alert: DashboardFinancialAlert, i: number) => <AlertCard key={i} alert={alert} />)}
        </div>
      )}

      {/* ── First score celebration ───────────────────── */}
      {health?.isFirstScore && (
        <div className="dashboard-page__first-score">
          🎉 Your first financial health score has been calculated!
        </div>
      )}

      {/* ── Main grid ────────────────────────────────── */}
      <div className="dashboard-page__grid">

        {/* ══ LEFT COLUMN ══════════════════════════════ */}
        <div className="dashboard-page__left-col">

          {/* Score card */}
          <div className="dashboard-page__score-card">
            {health || m?.overallHealthScore ? (
              <>
                <div className="dashboard-page__score-ring-wrap">
                  <ScoreRing score={score} color={ringColor} />
                  <div className="dashboard-page__score-number">
                    <span className="dashboard-page__score-value">{score}</span>
                    <span className="dashboard-page__score-max">/100</span>
                  </div>
                </div>
                <span className="dashboard-page__score-label">{band.label}</span>
                <span className="dashboard-page__score-risk" style={{ color: ringColor }}>
                  {m?.riskLabel ?? health?.riskLabel ?? health?.riskLevel ?? '—'}
                </span>

                {/* Score trend delta */}
                {m?.scoreTrendLabel && (
                  <p style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-muted)', textAlign: 'center' }}>
                    {m.scoreTrendLabel}
                  </p>
                )}

                {/* Sub-scores */}
                <div className="dashboard-page__sub-scores">
                  <SubScoreBar label="Debt Burden"  value={health?.debtBurdenScore    ?? m?.debtBurdenScore} />
                  <SubScoreBar label="Savings Rate" value={health?.savingsRateScore   ?? m?.savingsRateScore} />
                  <SubScoreBar label="Credit Score" value={health?.creditScoreComponent ?? m?.creditScoreComponent} />
                  <SubScoreBar label="Utilization"  value={health?.utilizationScore   ?? m?.creditUtilizationScore} />
                </div>

                {/* Trend sparkline */}
                {(health?.scoreTrend ?? m?.scoreTrendHistory) && (
                  <ScoreTrendChart trends={
                    health?.scoreTrend?.length
                      ? health.scoreTrend
                      : (m?.scoreTrendHistory?.map((t: { scoredOn: string; score: number; riskLevel: string }) => ({ scoredOn: t.scoredOn, score: t.score, riskLevel: t.riskLevel as RiskLevel })) ?? [])
                  } />
                )}

                <button className="btn btn-secondary btn-sm btn-full" onClick={recomputeScore} disabled={isRecomputing}>
                  {isRecomputing ? <><span className="spinner" /> Recalculating…</> : '↻ Recalculate Score'}
                </button>
              </>
            ) : (
              <>
                <div className="dashboard-page__score-ring-wrap">
                  <ScoreRing score={0} color="var(--color-border-muted)" />
                  <div className="dashboard-page__score-number">
                    <span className="dashboard-page__score-value" style={{ color: 'var(--color-text-disabled)' }}>—</span>
                  </div>
                </div>
                <span className="dashboard-page__score-label" style={{ color: 'var(--color-text-muted)' }}>Not calculated yet</span>
                <p style={{ fontSize: 'var(--text-sm)', color: 'var(--color-text-muted)', textAlign: 'center', lineHeight: 'var(--leading-normal)', fontWeight: 'var(--font-light)' }}>
                  {dashboard?.isFinancialProfileComplete
                    ? 'Your profile is ready. Calculate your financial health score.'
                    : 'Complete your financial profile first.'}
                </p>
                <button className="btn btn-primary btn-sm btn-full" onClick={recomputeScore}
                  disabled={isRecomputing || !dashboard?.isFinancialProfileComplete}>
                  {isRecomputing ? <><span className="spinner" /> Calculating…</> : '📊 Calculate My Score'}
                </button>
              </>
            )}
          </div>

          {/* Section 9: Product readiness */}
          {m && (m.loanReadinessLabel || m.cardUpgradeLabel) && (
            <Section title="Product Readiness">
              <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-2)' }}>
                {m.loanReadinessLabel && (
                  <div>
                    <p style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: 4 }}>Loan Eligibility</p>
                    <ReadinessBadge label={m.loanReadinessLabel} />
                  </div>
                )}
                {m.cardUpgradeLabel && (
                  <div>
                    <p style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: 4 }}>Card Upgrade</p>
                    <ReadinessBadge label={m.cardUpgradeLabel} />
                  </div>
                )}
              </div>
            </Section>
          )}

          {/* Section 7: Activity summary */}
{/*           
          {m && (
            <Section title="Your Activity">
              <div className="dashboard-page__stats-row">
                <Metric label="Loan Checks"   value={m.totalLoanChecks ?? 0} />
                <Metric label="Card Checks"   value={m.totalCardChecks ?? 0} />
                <Metric label="Fraud Checks"  value={m.totalFraudChecks ?? 0} />
                <Metric label="Apply Clicks"  value={m.totalAffiliateClicks ?? 0} />
              </div>
              {m.lastActivityLabel && (
                <p style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)', marginTop: 'var(--space-2)' }}>
                  {m.lastActivityLabel}
                </p>
              )}
            </Section>
          )} */}
        </div>

        {/* ══ MIDDLE COLUMN ══════════════════════════════ */}
        <div className="dashboard-page__mid-col">

          {/* Section 2: Income & cash flow */}
          {m && (
            <Section title="Income & Cash Flow">
              <div className="dashboard-page__stats-row">
                <Metric label="Monthly Income"   value={formatCurrency(m.monthlyIncome, true)} />
                <Metric label="Monthly Expenses" value={formatCurrency(m.monthlyExpenses ?? 0, true)} />
                <Metric label="EMI Total"         value={formatCurrency(m.existingEmiTotal ?? 0, true)} />
                <Metric
                  label="Monthly Savings"
                  value={formatCurrency(m.monthlySavings ?? 0, true)}
                  color={(m.monthlySavings ?? 0) < 0 ? 'var(--color-danger)' : undefined}
                />
                <Metric
                  label="Disposable"
                  value={formatCurrency(m.disposableIncome ?? 0, true)}
                  color={(m.disposableIncome ?? 0) < 0 ? 'var(--color-danger)' : undefined}
                />
                <Metric label="Savings Rate" value={`${m.savingsRatePercent ?? 0}%`}
                  sub={m.savingsRateLabel} />
              </div>
              {m.cashFlowStatus && (
                <div style={{ marginTop: 'var(--space-3)', display: 'flex', alignItems: 'center', gap: 'var(--space-2)' }}>
                  <span style={{
                    fontSize: 'var(--text-xs)', fontWeight: 'var(--font-semibold)',
                    padding: '2px 10px', borderRadius: 'var(--radius-full)',
                    background: m.cashFlowStatus === 'POSITIVE' ? 'rgba(16,185,129,0.1)' : m.cashFlowStatus === 'TIGHT' ? 'rgba(234,179,8,0.1)' : 'rgba(239,68,68,0.1)',
                    color: m.cashFlowStatus === 'POSITIVE' ? 'var(--color-brand-primary)' : m.cashFlowStatus === 'TIGHT' ? 'var(--color-warning)' : 'var(--color-danger)',
                  }}>
                    {m.cashFlowStatus === 'POSITIVE' ? '✓ Cash flow positive' : m.cashFlowStatus === 'TIGHT' ? '⚠ Cash flow tight' : '⛔ Cash flow negative'}
                  </span>
                </div>
              )}
            </Section>
          )}

          {/* Section 3: Debt profile */}
          {m && (
            <Section title="Debt Profile">
              <div className="dashboard-page__stats-row">
                <Metric label="FOIR" value={`${(m.foir ?? 0).toFixed(1)}%`} sub={m.foirLabel}
                  color={(m.foir ?? 0) > 50 ? 'var(--color-danger)' : (m.foir ?? 0) > 40 ? 'var(--color-warning)' : undefined} />
                <Metric label="DTI"  value={`${(m.dti ?? 0).toFixed(1)}%`} sub={m.dtiLabel}
                  color={(m.dti ?? 0) > 50 ? 'var(--color-danger)' : (m.dti ?? 0) > 43 ? 'var(--color-warning)' : undefined} />
                <Metric label="Active Loans" value={m.numberOfActiveLoans ?? 0} sub={m.loanBurdenLabel} />
                {m.maxAdditionalEmiCapacity != null && (
                  <Metric label="EMI Capacity"
                    value={formatCurrency(m.maxAdditionalEmiCapacity, true)}
                    sub={m.additionalEmiLabel} />
                )}
              </div>
              {/* DTI vs FOIR insight */}
              {m.dtiVsFoirInsight && (
                <div style={{
                  marginTop: 'var(--space-3)', padding: 'var(--space-3)',
                  background: 'var(--color-bg-input)', borderRadius: 'var(--radius-md)',
                  fontSize: 'var(--text-xs)', color: 'var(--color-text-secondary)',
                  lineHeight: 'var(--leading-normal)',
                }}>
                  {m.dtiVsFoirInsight}
                </div>
              )}
              {/* DTI Range badge */}
              {m.dtiRange && (
                <div style={{ marginTop: 'var(--space-2)', display: 'flex', gap: 'var(--space-2)', alignItems: 'center' }}>
                  <span style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)' }}>DTI Range:</span>
                  <span className={`badge ${
                    m.dtiRange === 'LOW'      ? 'badge-success' :
                    m.dtiRange === 'MODERATE' ? 'badge-warning' :
                    m.dtiRange === 'HIGH'     ? 'badge-orange'  : 'badge-danger'
                  }`}>{m.dtiRange}</span>
                </div>
              )}
            </Section>
          )}

          {/* Fallback key metrics if no financialMetrics (old backend) */}
          {!m && profile && (
            <Section title="Key Metrics">
              <div className="dashboard-page__stats-row">
                <Metric label="FOIR" value={profile.foir ? `${Number(profile.foir).toFixed(1)}%` : '—'} />
                <Metric label="DTI"  value={profile.dti  ? `${Number(profile.dti).toFixed(1)}%`  : '—'} />
                <Metric label="Credit Score" value={profile.creditScore ?? '—'} />
                <Metric label="Monthly Income" value={profile.monthlyIncome ? formatCurrency(Number(profile.monthlyIncome), true) : '—'} />
                <Metric label="Savings" value={profile.monthlySavings != null ? formatCurrency(Number(profile.monthlySavings), true) : '—'}
                  color={Number(profile.monthlySavings) < 0 ? 'var(--color-danger)' : undefined} />
                {profile.numberOfActiveLoans != null && (
                  <Metric label="Active Loans" value={profile.numberOfActiveLoans} />
                )}
              </div>
            </Section>
          )}

          {/* Risk warnings from health score */}
          {health?.riskWarnings && health.riskWarnings.length > 0 && !m?.alerts?.length && (
            <Section title="Risk Warnings">
              <div className="dashboard-page__warnings">
                {health.riskWarnings.map((w, i) => (
                  <div key={i} className="dashboard-page__warning-item">
                    <span className="dashboard-page__warning-icon">⚠</span>
                    <span>{w}</span>
                  </div>
                ))}
              </div>
            </Section>
          )}

          {/* Improvement tips */}
          {health?.improvementTips && health.improvementTips.length > 0 && (
            <Section title="Improvement Tips">
              <div className="dashboard-page__tips">
                {health.improvementTips.map((t, i) => (
                  <div key={i} className="dashboard-page__tip-item">
                    <span className="dashboard-page__tip-dot" />
                    <span>{t}</span>
                  </div>
                ))}
              </div>
            </Section>
          )}
        </div>

        {/* ══ RIGHT COLUMN ══════════════════════════════ */}
        <div className="dashboard-page__right-col">

          {/* Section 5: Credit profile */}
          {m && (
            <Section title="Credit Profile">
              <div className="dashboard-page__stats-row">
                <Metric
                  label="Credit Score"
                  value={m.creditScore ?? '—'}
                  sub={m.creditScoreLabel}
                  color={
                    (m.creditScore ?? 0) >= 750 ? 'var(--color-brand-primary)' :
                    (m.creditScore ?? 0) >= 700 ? undefined :
                    (m.creditScore ?? 0) >= 650 ? 'var(--color-warning)' : 'var(--color-danger)'
                  }
                />
                {m.creditScoreRange && (
                  <div className="dashboard-page__stat">
                    <span className="dashboard-page__stat-label">Score Range</span>
                    <span className={`badge ${
                      m.creditScoreRange === 'EXCELLENT' ? 'badge-success' :
                      m.creditScoreRange === 'GOOD'      ? 'badge-brand'   :
                      m.creditScoreRange === 'FAIR'      ? 'badge-warning' : 'badge-danger'
                    }`}>{m.creditScoreRange}</span>
                  </div>
                )}
                <Metric label="Utilization"
                  value={`${Number(m.currentCreditUtilization ?? 0).toFixed(1)}%`}
                  sub={m.utilizationLabel}
                  color={Number(m.currentCreditUtilization ?? 0) > 50 ? 'var(--color-danger)' : undefined} />
                <Metric label="Cards"         value={m.numberOfCreditCards ?? 0} />
                <Metric label="Credit Limit"  value={formatCurrency(m.totalCreditLimit ?? 0, true)} />
              </div>
              {m.creditScoreTip && (
                <div style={{ marginTop: 'var(--space-3)', fontSize: 'var(--text-xs)', color: 'var(--color-brand-primary)', padding: 'var(--space-2) var(--space-3)', background: 'rgba(16,185,129,0.05)', borderRadius: 'var(--radius-md)', border: '1px solid var(--color-border-brand)' }}>
                  💡 {m.creditScoreTip}
                </div>
              )}
            </Section>
          )}

          {/* Section 1: Employment */}
          {m && (
            <Section title="Employment">
              <div className="dashboard-page__stats-row">
                <Metric label="Employment"  value={m.employmentTypeLabel ?? m.employmentType ?? '—'} />
                {m.yearsOfExperience != null && (
                  <Metric label="Experience" value={m.experienceLabel ?? `${m.yearsOfExperience} yrs`} />
                )}
              </div>
            </Section>
          )}

          {/* Section 4: Upcoming EMIs */}
          {m && m.upcomingEmis && m.upcomingEmis.length > 0 && (
            <Section title={`Upcoming EMIs (${m.totalTrackedEmis ?? 0} tracked · ₹${formatCurrency(m.totalMonthlyEmiCommitment ?? 0, true)}/mo)`}>
              <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-2)' }}>
                {m.upcomingEmis.map((emi: DashboardEmiSummary, i: number) => (
                  <div key={i} style={{
                    display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start',
                    padding: 'var(--space-2) var(--space-3)',
                    background: emi.isDueSoon ? 'var(--color-warning-bg)' : 'var(--color-bg-input)',
                    border: `1px solid ${emi.isDueSoon ? 'var(--color-warning-border)' : 'var(--color-border-subtle)'}`,
                    borderRadius: 'var(--radius-md)',
                  }}>
                    <div>
                      <p style={{ fontSize: 'var(--text-sm)', fontWeight: 'var(--font-medium)', color: 'var(--color-text-primary)' }}>
                        {emi.isDueSoon && '⏰ '}{emi.loanName}
                      </p>
                      {emi.lenderName && (
                        <p style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-muted)' }}>{emi.lenderName}</p>
                      )}
                      <p style={{ fontSize: 'var(--text-xs)', color: emi.isDueSoon ? 'var(--color-warning)' : 'var(--color-text-disabled)' }}>
                        {emi.dueDateLabel}
                      </p>
                    </div>
                    <div style={{ textAlign: 'right', flexShrink: 0 }}>
                      <p style={{ fontFamily: 'var(--font-mono)', fontSize: 'var(--text-sm)', fontWeight: 'var(--font-semibold)', color: 'var(--color-text-primary)' }}>
                        {formatCurrency(emi.emiAmount, true)}
                      </p>
                      {emi.remainingEmis != null && (
                        <p style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)' }}>
                          {emi.remainingEmis} left
                        </p>
                      )}
                    </div>
                  </div>
                ))}
              </div>
              {(m.emisDueSoon ?? 0) > 0 && (
                <p style={{ fontSize: 'var(--text-xs)', color: 'var(--color-warning)', marginTop: 'var(--space-2)', fontWeight: 'var(--font-semibold)' }}>
                  ⏰ {m.emisDueSoon} EMI{(m.emisDueSoon ?? 0) > 1 ? 's' : ''} due within 3 days
                </p>
              )}
            </Section>
          )}

          {/* Quick actions */}
          <Section title="Quick Actions">
            <div className="dashboard-page__quick-actions">
              {[
                { label: '🏦 Check Loan Eligibility', route: ROUTES.LOAN },
                { label: '💳 Credit Card Recommendations', route: ROUTES.CREDIT_CARD },
                { label: '🔍 Fraud Check', route: ROUTES.FRAUD_CHECK },
                { label: '📊 EMI Tracker', route: ROUTES.EMI_TRACKER },
              ].map(({ label, route }) => (
                <button key={route} className="btn btn-ghost btn-full" style={{ justifyContent: 'flex-start', textAlign: 'left' }}
                  onClick={() => navigate(route)}>
                  {label}
                </button>
              ))}
            </div>
          </Section>
        </div>
      </div>
    </div>
  );
};

export default DashboardPage;