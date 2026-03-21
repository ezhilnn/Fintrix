// ================================================================
// useConsent.ts
// Initialises consent status on mount.
// Used by ConsentGate to determine if app should be blocked.
// ================================================================

import { useEffect } from 'react';
import useConsentStore from '../store/consentStore';

const useConsent = () => {
  const store = useConsentStore();

  useEffect(() => {
    if (!store.status) {
      store.initConsent();
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return store;
};

export default useConsent;