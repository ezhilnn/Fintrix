// ================================================================
// ConsentGate.tsx
//
// Wraps the entire app. If consentStore.isConsentRequired = true,
// renders a full-screen blocking modal that cannot be dismissed.
// User must grant DATA_PROCESSING to proceed.
//
// Also grants CREDIT_CHECK automatically alongside DATA_PROCESSING
// since the app's core features require both.
// ================================================================

import useConsentStore from '../../store/consentStore';
import './ConsentGate.css';

interface ConsentGateProps {
  children: React.ReactNode;
}

const CONSENT_ITEMS = [
  {
    icon: '🔒',
    label: 'Data Processing',
    desc:  'We process your financial data locally to generate loan eligibility, card recommendations, and health scores. Your data is never sold.',
  },
  {
    icon: '📊',
    label: 'Credit Check Authorization',
    desc:  'We use your self-reported CIBIL score to match you with lenders. No hard inquiry is made on your credit report.',
  },
  {
    icon: '🤝',
    label: 'Partner Referrals',
    desc:  'When you click "Apply Now", your anonymised profile is shared with the selected lender or card issuer only.',
  },
];

const ConsentGate = ({ children }: ConsentGateProps) => {
  const { isConsentRequired, isGranting, error, grant } = useConsentStore();

  const handleAccept = async () => {
    // Grant mandatory consent types together
    await grant('DATA_PROCESSING');
    await grant('CREDIT_CHECK');
  };

  // App unlocked — render children normally
  if (!isConsentRequired) return <>{children}</>;

  return (
    <>
      {/* Render children behind the overlay so layout is ready */}
      <div style={{ visibility: 'hidden', pointerEvents: 'none' }}>{children}</div>

      <div className="consent-gate">
        <div className="consent-gate__card">

          <div className="consent-gate__logo">Fintrix<span>.</span></div>

          <h2 className="consent-gate__title">
            Before we begin
          </h2>

          <p className="consent-gate__body">
            Fintrix analyses your financial profile to give you personalised
            loan eligibility, credit card matches, and a health score.
            Here's how we handle your data:
          </p>

          <div className="consent-gate__items">
            {CONSENT_ITEMS.map(item => (
              <div key={item.label} className="consent-gate__item">
                <span className="consent-gate__item-icon">{item.icon}</span>
                <div className="consent-gate__item-text">
                  <span className="consent-gate__item-label">{item.label}</span>
                  <span className="consent-gate__item-desc">{item.desc}</span>
                </div>
              </div>
            ))}
          </div>

          <div className="consent-gate__divider" />

          <p className="consent-gate__legal">
            By continuing you agree to Fintrix's processing of your financial
            data as described above, in accordance with applicable data
            protection regulations. You can withdraw non-essential consent
            at any time from your profile settings.
          </p>

          <div className="consent-gate__actions">
            {error && <p className="consent-gate__error">{error}</p>}
            <button
              className="btn btn-primary btn-full btn-lg"
              onClick={handleAccept}
              disabled={isGranting}
            >
              {isGranting
                ? <><span className="spinner" /> Saving consent…</>
                : 'I understand — continue to Fintrix →'}
            </button>
          </div>

        </div>
      </div>
    </>
  );
};

export default ConsentGate;