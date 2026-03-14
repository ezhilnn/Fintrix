// ================================================================
// DashboardPage.tsx  —  /dashboard  (protected)
// Single BFF call: GET /api/v1/bff/dashboard
// Returns: userProfile + financialProfile + healthScore
// ================================================================

import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import useDashboardStore from '../store/dashboardStore';
import { ROUTES } from '../utils/constants';
import { formatCurrency, formatPercent, formatDate } from '../utils/formatters';
import { getHealthScoreBand, riskLevelColor } from '../utils/scoreHelpers';
import type { RiskLevel } from '../types/financialProfile.types';
import type { ScoreTrend } from '../types/api.types';
import './DashboardPage.css';

// ── SVG ring progress ──────────────────────────────────────────
const ScoreRing = ({ score, color }: { score: number; color: string }) => {
  const r          = 50;
  const circ       = 2 * Math.PI * r;
  const offset     = circ - (score / 100) * circ;
  return (
    <svg className="dashboard-page__score-ring" viewBox="0 0 120 120">
      <circle className="dashboard-page__score-ring-bg" cx="60" cy="60" r={r} />
      <circle
        className="dashboard-page__score-ring-fill"
        cx="60" cy="60" r={r}
        stroke={color}
        strokeDasharray={circ}
        strokeDashoffset={offset}
      />
    </svg>
  );
};

// ── Sub score bar ──────────────────────────────────────────────
const SubScoreBar = ({ label, value }: { label: string; value: number }) => {
  const color =
    value >= 70 ? 'var(--color-success)' :
    value >= 40 ? 'var(--color-warning)' :
    'var(--color-danger)';
  return (
    <div className="dashboard-page__sub-score-row">
      <span className="dashboard-page__sub-score-label">{label}</span>
      <div className="dashboard-page__sub-score-bar-wrap">
        <div
          className="dashboard-page__sub-score-bar"
          style={{ width: `${value}%`, background: color }}
        />
      </div>
      <span className="dashboard-page__sub-score-val">{value}</span>
    </div>
  );
};

// ── Score trend SVG sparkline ──────────────────────────────────
const ScoreTrendChart = ({ trends }: { trends: ScoreTrend[] }) => {
  if (!trends || trends.length === 0) {
    return (
      <div className="dashboard-page__trend-empty">
        Score history will appear after your first weekly recalculation
      </div>
    );
  }

  const W = 400, H = 120, PAD = 16;
  const min = Math.max(0,  Math.min(...trends.map(t => t.score)) - 10);
  const max = Math.min(100, Math.max(...trends.map(t => t.score)) + 10);
  const xs = trends.map((_, i) => PAD + (i / Math.max(trends.length - 1, 1)) * (W - PAD * 2));
  const ys = trends.map(t  => H - PAD - ((t.score - min) / (max - min || 1)) * (H - PAD * 2));

  const linePath = xs.map((x, i) => `${i === 0 ? 'M' : 'L'}${x.toFixed(1)},${ys[i].toFixed(1)}`).join(' ');
  const areaPath = `${linePath} L${xs[xs.length-1].toFixed(1)},${(H-PAD).toFixed(1)} L${xs[0].toFixed(1)},${(H-PAD).toFixed(1)} Z`;

  const latest = trends[trends.length - 1];
  const prev   = trends.length > 1 ? trends[trends.length - 2] : null;
  const delta  = prev ? latest.score - prev.score : 0;

  return (
    <div className="dashboard-page__trend-chart">
      <svg viewBox={`0 0 ${W} ${H}`} preserveAspectRatio="none" style={{ width: '100%', height: '100%' }}>
        <defs>
          <linearGradient id="trendGrad" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%"   stopColor="var(--color-brand-primary)" stopOpacity="0.18" />
            <stop offset="100%" stopColor="var(--color-brand-primary)" stopOpacity="0" />
          </linearGradient>
        </defs>
        {/* Area fill */}
        <path d={areaPath} fill="url(#trendGrad)" />
        {/* Line */}
        <path d={linePath} fill="none" stroke="var(--color-brand-primary)" strokeWidth="2" strokeLinejoin="round" strokeLinecap="round" />
        {/* Dots */}
        {xs.map((x, i) => (
          <circle key={i} cx={x} cy={ys[i]} r="3" fill="var(--color-brand-primary)" />
        ))}
        {/* Score labels on first + last */}
        {[0, trends.length - 1].map(i => (
          <text
            key={i}
            x={xs[i]}
            y={ys[i] - 8}
            textAnchor="middle"
            fontSize="10"
            fill="var(--color-text-muted)"
          >
            {trends[i].score}
          </text>
        ))}
      </svg>
      {delta !== 0 && (
        <div style={{
          position: 'absolute', bottom: 0, right: 0,
          fontSize: 'var(--text-xs)', fontFamily: 'var(--font-mono)',
          color: delta > 0 ? 'var(--color-success)' : 'var(--color-danger)',
          fontWeight: 'var(--font-semibold)',
        }}>
          {delta > 0 ? '▲' : '▼'} {Math.abs(delta)} pts
        </div>
      )}
    </div>
  );
};

// ── Raw health metrics row ─────────────────────────────────────
const RawMetricsRow = ({ health }: { health: { foir: number; creditScore?: number; creditScoreRange?: string; creditUtilization: number; savingsRate: number } }) => (
  <div className="dashboard-page__raw-metrics">
    <div className="dashboard-page__raw-metric">
      <span className="dashboard-page__raw-metric-label">FOIR</span>
      <span className="dashboard-page__raw-metric-value">{formatPercent(health.foir)}</span>
    </div>
    <div className="dashboard-page__raw-metric">
      <span className="dashboard-page__raw-metric-label">CIBIL</span>
      <span className="dashboard-page__raw-metric-value">{health.creditScore ?? '—'}</span>
    </div>
    <div className="dashboard-page__raw-metric">
      <span className="dashboard-page__raw-metric-label">Utilization</span>
      <span className="dashboard-page__raw-metric-value">{formatPercent(health.creditUtilization)}</span>
    </div>
    <div className="dashboard-page__raw-metric">
      <span className="dashboard-page__raw-metric-label">Savings Rate</span>
      <span className="dashboard-page__raw-metric-value">{formatPercent(health.savingsRate)}</span>
    </div>
  </div>
);


const QUICK_ACTIONS = [
  { icon: '🏦', title: 'Check Loan Eligibility', desc: 'See which lenders will approve you', route: ROUTES.LOAN },
  { icon: '💳', title: 'Credit Card Match',       desc: 'Find cards suited to your profile',  route: ROUTES.CREDIT_CARD },
  { icon: '🔍', title: 'Fraud Check',             desc: 'Verify investment companies',         route: ROUTES.FRAUD_CHECK },
  { icon: '📊', title: 'Financial Profile',       desc: 'Update your income and EMI details',  route: ROUTES.FINANCIAL_PROFILE },
];

const DashboardPage = () => {
  const navigate = useNavigate();
  const { dashboard, isFetching, fetchError, isRecomputing, fetchDashboard, recomputeScore } =
    useDashboardStore();

  useEffect(() => {
    fetchDashboard();
  }, [fetchDashboard]);

  // ── Loading ─────────────────────────────────────────────────
  if (isFetching && !dashboard) {
    return (
      <div className="dashboard-page">
        <div className="dashboard-page__error">
          <div className="spinner spinner-lg" />
        </div>
      </div>
    );
  }

  // ── Error ───────────────────────────────────────────────────
  if (fetchError) {
    return (
      <div className="dashboard-page">
        <div className="dashboard-page__error">
          <h2 className="dashboard-page__error-title">Could not load dashboard</h2>
          <p className="dashboard-page__error-msg">{fetchError}</p>
          <button className="btn btn-primary" onClick={fetchDashboard}>Retry</button>
        </div>
      </div>
    );
  }

  const user    = dashboard?.userProfile;
  const health  = dashboard?.healthScore;
  const profile = dashboard?.financialProfile;
  const score   = health?.overallScore ?? 0;
  const band    = getHealthScoreBand(score);
  const ringColor = riskLevelColor((health?.riskLevel ?? 'MEDIUM') as RiskLevel);

  const firstName = user?.fullName?.split(' ')[0] ?? 'there';

  return (
    <div className="dashboard-page">

      {/* ── Header ─────────────────────────────────────── */}
      <div className="dashboard-page__header">
        <h1 className="dashboard-page__greeting">
          Good day, <em>{firstName}</em>
        </h1>
        <p className="dashboard-page__sub">
          Here's your financial health at a glance.
        </p>
      </div>

      {/* ── Setup banner ───────────────────────────────── */}
      {!dashboard?.isFinancialProfileComplete && (
        <div className="dashboard-page__setup-banner">
          <div className="dashboard-page__setup-text">
            <span className="dashboard-page__setup-title">
              Complete your financial profile
            </span>
            <span className="dashboard-page__setup-hint">
              Add income and EMI details to unlock loan eligibility checks
            </span>
          </div>
          <button
            className="btn btn-primary btn-sm"
            onClick={() => navigate(ROUTES.FINANCIAL_PROFILE)}
          >
            Complete now →
          </button>
        </div>
      )}

      {/* ── First-score celebration ─────────────────────── */}
      {health?.isFirstScore && (
        <div className="dashboard-page__first-score">
          <span className="dashboard-page__first-score-icon">🎉</span>
          <div className="dashboard-page__first-score-text">
            <span className="dashboard-page__first-score-title">
              Your first financial health score is ready!
            </span>
            <span className="dashboard-page__first-score-sub">
              We'll recalculate it weekly and track your progress over time.
            </span>
          </div>
        </div>
      )}

      {/* ── Quick actions row ──────────────────────────── */}
      <div className="dashboard-page__grid">
        <div className="dashboard-page__actions-row">
          {QUICK_ACTIONS.map(a => (
            <div
              key={a.title}
              className="dashboard-page__action-card"
              onClick={() => navigate(a.route)}
              role="button"
              tabIndex={0}
              onKeyDown={e => e.key === 'Enter' && navigate(a.route)}
            >
              <span className="dashboard-page__action-icon">{a.icon}</span>
              <span className="dashboard-page__action-title">{a.title}</span>
              <span className="dashboard-page__action-desc">{a.desc}</span>
            </div>
          ))}
        </div>

        {/* ── Score column ─────────────────────────────── */}
        <div className="dashboard-page__score-col">

          {/* Health score ring */}
          <div className="dashboard-page__score-card">
            <div className="dashboard-page__score-ring-wrap">
              <ScoreRing score={score} color={ringColor} />
              <div className="dashboard-page__score-number">
                <span className="dashboard-page__score-value">{score}</span>
                <span className="dashboard-page__score-max">/100</span>
              </div>
            </div>
            <span className="dashboard-page__score-label">{band.label}</span>
            <span
              className="dashboard-page__score-risk"
              style={{ color: ringColor }}
            >
              {health?.riskLabel ?? health?.riskLevel ?? '—'}
            </span>

            {/* Sub score breakdown */}
            {health && (
              <div className="dashboard-page__sub-scores">
                <SubScoreBar label="Debt Burden"  value={health.debtBurdenScore} />
                <SubScoreBar label="Savings Rate" value={health.savingsRateScore} />
                <SubScoreBar label="Credit Score" value={health.creditScoreComponent} />
                <SubScoreBar label="Utilization"  value={health.utilizationScore} />
              </div>
            )}

            <button
              className="btn btn-secondary btn-sm btn-full"
              onClick={recomputeScore}
              disabled={isRecomputing}
            >
              {isRecomputing
                ? <><span className="spinner" /> Refreshing…</>
                : '↻ Refresh Score'}
            </button>

            {/* Raw metric values from FinancialHealthResponse */}
            {health && (
              <RawMetricsRow health={health} />
            )}
          </div>

          {/* Key stats */}
          {profile && (
            <div className="dashboard-page__section-card">
              <p className="dashboard-page__section-card-title">Key Metrics</p>
              <div className="dashboard-page__stats-row">
                <div className="dashboard-page__stat">
                  <span className="dashboard-page__stat-label">FOIR</span>
                  <span className="dashboard-page__stat-value">
                    {profile.foir ? formatPercent(Number(profile.foir)) : '—'}
                  </span>
                </div>
                <div className="dashboard-page__stat">
                  <span className="dashboard-page__stat-label">Credit Score</span>
                  <span className="dashboard-page__stat-value">
                    {profile.creditScore ?? '—'}
                  </span>
                </div>
                <div className="dashboard-page__stat">
                  <span className="dashboard-page__stat-label">Monthly Income</span>
                  <span className="dashboard-page__stat-value">
                    {profile.monthlyIncome
                      ? formatCurrency(Number(profile.monthlyIncome), true)
                      : '—'}
                  </span>
                </div>
                <div className="dashboard-page__stat">
                  <span className="dashboard-page__stat-label">Savings</span>
                  <span className="dashboard-page__stat-value">
                    {profile.monthlySavings
                      ? formatCurrency(Number(profile.monthlySavings), true)
                      : '—'}
                  </span>
                </div>
              </div>
            </div>
          )}
        </div>

        {/* ── Middle column ────────────────────────────── */}
        <div className="dashboard-page__mid-col">

          {/* Risk warnings */}
          {health && health.riskWarnings.length > 0 && (
            <div className="dashboard-page__section-card">
              <p className="dashboard-page__section-card-title">Risk Warnings</p>
              <div className="dashboard-page__warnings">
                {health.riskWarnings.map((w, i) => (
                  <div key={i} className="dashboard-page__warning-item">
                    <span className="dashboard-page__warning-icon">⚠</span>
                    <span>{w}</span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Next action prompt from BFF */}
          {dashboard?.nextActionPrompt && (
            <div className="dashboard-page__next-action">
              <strong>Next step:</strong> {dashboard.nextActionPrompt}
            </div>
          )}
        </div>

        {/* ── Right column ─────────────────────────────── */}
        <div className="dashboard-page__right-col">

          {/* Improvement tips */}
          {health && health.improvementTips.length > 0 && (
            <div className="dashboard-page__section-card">
              <p className="dashboard-page__section-card-title">
                How to Improve Your Score
              </p>
              <div className="dashboard-page__tips">
                {health.improvementTips.map((tip, i) => (
                  <div key={i} className="dashboard-page__tip-item">
                    <span className="dashboard-page__tip-bullet" />
                    <span>{tip}</span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Score trend chart */}
          {health && (
            <div className="dashboard-page__trend-card">
              <div className="dashboard-page__trend-header">
                <span className="dashboard-page__trend-title">Score History</span>
                {health.scoredOn && (
                  <span className="dashboard-page__trend-meta">
                    Last updated {formatDate(health.scoredOn)}
                  </span>
                )}
              </div>
              <ScoreTrendChart trends={health.scoreTrend} />
            </div>
          )}
        </div>

      </div>
    </div>
  );
};

export default DashboardPage;