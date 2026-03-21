// ================================================================
// ConsentPage.tsx  —  /consent  (protected)
// Manage all consent types. DATA_PROCESSING is mandatory (locked on).
// Users can toggle optional consents here.
// ================================================================

import { useEffect }   from 'react';
import useConsentStore from '../store/consentStore';
import type { ConsentType } from '../types/api.types';
import './ConsentPage.css';

const CONSENT_ITEMS: {
  type: ConsentType;
  label: string;
  desc: string;
  mandatory: boolean;
}[] = [
  {
    type:      'DATA_PROCESSING',
    label:     'Data Processing',
    desc:      'Required to process your financial profile and generate loan eligibility, card recommendations, and health scores.',
    mandatory: true,
  },
  {
    type:      'CREDIT_CHECK',
    label:     'Credit Check Authorization',
    desc:      'Allows us to use your self-reported CIBIL score when matching you with lenders. Re-confirmation required every 6 months.',
    mandatory: false,
  },
  {
    type:      'MARKETING',
    label:     'Marketing Communications',
    desc:      'Receive personalised offers, product updates, and financial tips via push notifications and email.',
    mandatory: false,
  },
  {
    type:      'THIRD_PARTY_SHARE',
    label:     'Partner Data Sharing',
    desc:      'Share anonymised profile data with selected lenders and card issuers only when you click "Apply Now".',
    mandatory: false,
  },
];

const ConsentPage = () => {
  const { status, isLoading, isGranting, initConsent, grant, withdraw } =
    useConsentStore();

  useEffect(() => {
    // Only init if not already loaded
    if (!status) {
      initConsent();
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  if (isLoading || !status) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', padding: 'var(--space-20)' }}>
        <div className="spinner spinner-lg" />
      </div>
    );
  }

  const getValue = (type: ConsentType): boolean => ({
    DATA_PROCESSING:  !!status.dataProcessing,
    MARKETING:        !!status.marketing,
    CREDIT_CHECK:     !!status.creditCheck,
    THIRD_PARTY_SHARE: !!status.thirdPartyShare,
  }[type]);

  const handleToggle = (type: ConsentType, current: boolean) => {
    if (current) {
      withdraw(type);
    } else {
      grant(type);
    }
  };

  return (
    <div className="consent-page">

      <div className="consent-page__header">
        <p className="consent-page__eyebrow">Privacy & Data</p>
        <h1 className="consent-page__title">Consent Settings</h1>
        <p className="consent-page__sub">
          Control how Fintrix uses your data. Mandatory consents are required
          for the app to function. Optional consents can be changed at any time.
        </p>
      </div>

      <div className="consent-page__list">
        {CONSENT_ITEMS.map(item => {
          const isOn = getValue(item.type);
          return (
            <div key={item.type} className="consent-page__item">
              <div className="consent-page__item-text">
                <p className="consent-page__item-label">{item.label}</p>
                <p className="consent-page__item-desc">{item.desc}</p>
                {item.mandatory && (
                  <p className="consent-page__item-mandatory">
                    Mandatory — cannot be withdrawn
                  </p>
                )}
              </div>

              <label className="consent-page__toggle">
                <input
                  type="checkbox"
                  checked={isOn}
                  disabled={item.mandatory || isGranting}
                  onChange={() => handleToggle(item.type, isOn)}
                />
                <span className="consent-page__toggle-track" />
              </label>
            </div>
          );
        })}
      </div>

    </div>
  );
};

export default ConsentPage;