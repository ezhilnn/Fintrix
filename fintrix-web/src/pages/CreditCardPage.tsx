// ================================================================
// CreditCardPage.tsx  —  /credit-cards  (protected)
// POST /api/v1/credit-cards/recommendations (body optional)
//
// Optional filters:
//   preferredRewardType  RewardType enum
//   topSpendingCategory  string
//   preferNoAnnualFee    boolean
//
// Response lists:
//   recommendedCards   — top matches (show first)
//   otherEligibleCards — eligible but lower preference match
//   futureCards        — not yet eligible (motivational)
// ================================================================

import { useState } from 'react';
import useCreditCardRecommendation from '../hooks/useCreditCardRecommendation';
import { REWARD_TYPE_LABELS } from '../utils/constants';
import { formatCurrency } from '../utils/formatters';
import type { CardResult, CardRecommendationRequest } from '../types/api.types';
import './CreditCardPage.css';

const SPEND_CATS = ['FOOD', 'SHOPPING', 'TRAVEL', 'FUEL', 'ENTERTAINMENT', 'HEALTHCARE'];

// ── Individual card tile ───────────────────────────────────────
const CardTile = ({
  card,
  variant = 'default',
}: {
  card: CardResult;
  variant?: 'recommended' | 'future' | 'default';
}) => (
  <div className={`card-page__card-tile${
    variant === 'recommended' ? ' card-page__card-tile--recommended' :
    variant === 'future'      ? ' card-page__card-tile--future'      : ''
  }`}>
    {/* Header */}
    <div className="card-page__tile-header">
      <div className="card-page__tile-names">
        <span className="card-page__tile-bank">{card.bankName}</span>
        <span className="card-page__tile-card-name">{card.cardName}</span>
        <div style={{ display: 'flex', gap: 'var(--space-2)', marginTop: 4, flexWrap: 'wrap' }}>
          <span className="badge badge-info" style={{ alignSelf: 'flex-start' }}>
            {card.cardCategory.replace('_', ' ')}
          </span>
          {card.rewardType && card.rewardType !== 'NONE' && (
            <span className="badge badge-brand" style={{ alignSelf: 'flex-start' }}>
              {card.rewardType.replace('_', ' ')}
            </span>
          )}
        </div>
      </div>
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end', gap: 'var(--space-2)' }}>
        {card.logoUrl && (
          <img
            src={card.logoUrl}
            alt={card.bankName}
            style={{ height: 28, width: 'auto', objectFit: 'contain', borderRadius: 4 }}
            onError={e => { (e.target as HTMLImageElement).style.display = 'none'; }}
          />
        )}
        <div className="card-page__tile-prob">
          <span className="card-page__tile-prob-val">{card.approvalProbability}%</span>
          <span className="card-page__tile-prob-label">approval</span>
        </div>
      </div>
    </div>

    {/* Fees */}
    <div className="card-page__tile-fees">
      <div className="card-page__tile-fee">
        <span className="card-page__tile-fee-label">Joining Fee</span>
        <span className="card-page__tile-fee-value">
          {card.joiningFee === 0 ? 'Free' : formatCurrency(card.joiningFee)}
        </span>
      </div>
      <div className="card-page__tile-fee">
        <span className="card-page__tile-fee-label">Annual Fee</span>
        <span className="card-page__tile-fee-value">
          {card.annualFee === 0 ? 'Free' : formatCurrency(card.annualFee)}
          {card.annualFeeWaiverCondition && (
            <span style={{ fontSize: 'var(--text-xs)', color: 'var(--color-brand-primary)' }}>
              {' '}(waivable)
            </span>
          )}
        </span>
      </div>
      {card.rewardRate && (
        <div className="card-page__tile-fee">
          <span className="card-page__tile-fee-label">Reward Rate</span>
          <span className="card-page__tile-fee-value">{card.rewardRate}</span>
        </div>
      )}
    </div>

    {/* Key benefits */}
    {card.keyBenefits.length > 0 && (
      <div className="card-page__tile-benefits">
        {card.keyBenefits.slice(0, 4).map((b, i) => (
          <div key={i} className="card-page__tile-benefit">
            <span className="card-page__tile-benefit-dot" />
            <span>{b}</span>
          </div>
        ))}
      </div>
    )}

    {/* Welcome / joining benefit */}
    {card.welcomeBenefit && (
      <div style={{
        fontSize: 'var(--text-xs)',
        padding: '6px 10px',
        background: 'rgba(16,185,129,0.06)',
        border: '1px solid var(--color-border-brand)',
        borderRadius: 'var(--radius-md)',
        color: 'var(--color-brand-primary)',
      }}>
        🎁 {card.welcomeBenefit}
      </div>
    )}

    {/* Match reason */}
    {card.matchReason && (
      <p className="card-page__tile-match">✦ {card.matchReason}</p>
    )}

    {/* Failure reasons (future cards) */}
    {card.failureReasons.length > 0 && (
      <div className="card-page__tile-reasons">
        {card.failureReasons.map((r, i) => (
          <span key={i} className="card-page__tile-reason">{r}</span>
        ))}
      </div>
    )}
  </div>
);

// ── Page ───────────────────────────────────────────────────────
const CreditCardPage = () => {
  const { result, isFetching, error, applyFilters } = useCreditCardRecommendation();

  const [filters, setFilters] = useState<CardRecommendationRequest>({
    preferredRewardType:  '',
    topSpendingCategory:  '',
    preferNoAnnualFee:    false,
  });

  const setFilter = (key: keyof CardRecommendationRequest, value: string | boolean) => {
    const updated = { ...filters, [key]: value };
    setFilters(updated);
    applyFilters(updated);
  };

  return (
    <div className="card-page">

      <div className="card-page__header">
        <p className="card-page__eyebrow">Credit Tools</p>
        <h1 className="card-page__title">Credit Card Recommendations</h1>
        <p className="card-page__sub">
          Cards matched to your income, credit score, and spending habits.
        </p>
      </div>

      {/* ── Filters ──────────────────────────────────── */}
      <div className="card-page__filters">

        <div className="card-page__filter-group">
          <label className="card-page__filter-label">Reward Preference</label>
          <select
            className="form-input"
            value={filters.preferredRewardType}
            onChange={e => setFilter('preferredRewardType', e.target.value)}
          >
            <option value="">Any reward type</option>
            {(Object.entries(REWARD_TYPE_LABELS) as [string, string][]).map(([val, label]) => (
              <option key={val} value={val}>{label}</option>
            ))}
          </select>
        </div>

        <div className="card-page__filter-group">
          <label className="card-page__filter-label">Top Spend Category</label>
          <select
            className="form-input"
            value={filters.topSpendingCategory}
            onChange={e => setFilter('topSpendingCategory', e.target.value)}
          >
            <option value="">Any category</option>
            {SPEND_CATS.map(c => (
              <option key={c} value={c}>{c}</option>
            ))}
          </select>
        </div>

        <div className="card-page__filter-group">
          <label className="card-page__filter-label">Annual Fee</label>
          <label className="card-page__filter-checkbox-row">
            <input
              type="checkbox"
              checked={!!filters.preferNoAnnualFee}
              onChange={e => setFilter('preferNoAnnualFee', e.target.checked)}
            />
            <span className="card-page__filter-checkbox-label">No annual fee only</span>
          </label>
        </div>

      </div>

      {/* ── Content ──────────────────────────────────── */}
      {isFetching && (
        <div className="card-page__loading">
          <div className="spinner spinner-lg" />
        </div>
      )}

      {error && (
        <div className="alert alert-error">{error}</div>
      )}

      {result && !isFetching && (
        <>
          {result.overallTip && (
            <div className="card-page__tip">💡 {result.overallTip}</div>
          )}

          {result.multipleCardWarning && (
            <div className="card-page__multi-warn">
              ⚠ {result.multipleCardWarning}
            </div>
          )}

          {/* Recommended cards */}
          {result.recommendedCards.length > 0 && (
            <>
              <p className="card-page__section-title">
                Top Matches for You — {result.recommendedCards.length} card{result.recommendedCards.length !== 1 ? 's' : ''}
              </p>
              <div className="card-page__grid">
                {result.recommendedCards.map(c => (
                  <CardTile key={c.cardId} card={c} variant="recommended" />
                ))}
              </div>
            </>
          )}

          {/* Other eligible */}
          {result.otherEligibleCards.length > 0 && (
            <>
              <p className="card-page__section-title">
                Also Eligible — {result.otherEligibleCards.length} more
              </p>
              <div className="card-page__grid">
                {result.otherEligibleCards.map(c => (
                  <CardTile key={c.cardId} card={c} />
                ))}
              </div>
            </>
          )}

          {/* Future goal cards */}
          {result.futureCards.length > 0 && (
            <>
              <p className="card-page__section-title">
                Future Goals — improve profile to unlock
              </p>
              <div className="card-page__grid">
                {result.futureCards.map(c => (
                  <CardTile key={c.cardId} card={c} variant="future" />
                ))}
              </div>
            </>
          )}

          {/* All lists empty */}
          {result.recommendedCards.length === 0 &&
            result.otherEligibleCards.length === 0 &&
            result.futureCards.length === 0 && (
              <div className="card-page__empty">
                <span className="card-page__empty-icon">💳</span>
                <h3 className="card-page__empty-title">
                  No cards found for current filters
                </h3>
              </div>
            )}
        </>
      )}
    </div>
  );
};

export default CreditCardPage;