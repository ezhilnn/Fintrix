// ================================================================
// consentStore.ts
//
// Critical: if dataProcessing = false the ConsentGate component
// blocks the entire app until the user grants it.
//
// Flow:
//   1. initConsent() called after login (in authStore.handleOAuthSuccess)
//   2. If status.dataProcessing = false → isConsentRequired = true
//   3. ConsentGate renders as a blocking modal
//   4. User grants DATA_PROCESSING → isConsentRequired = false → app unlocks
// ================================================================

import { create } from 'zustand';
import ConsentService from '../services/consent.service';
import type { ConsentStatusResponse, ConsentType } from '../types/api.types';
import type { ApiError } from '../types/api.types';

interface ConsentState {
  status:             ConsentStatusResponse | null;
  isConsentRequired:  boolean;   // true if DATA_PROCESSING not granted
  isLoading:          boolean;
  isGranting:         boolean;
  error:              string | null;

  initConsent:   () => Promise<void>;
  grant:         (type: ConsentType) => Promise<void>;
  withdraw:      (type: ConsentType) => Promise<void>;
}

const useConsentStore = create<ConsentState>((set, get) => ({
  status:            null,
  isConsentRequired: false,
  isLoading:         false,
  isGranting:        false,
  error:             null,

  initConsent: async () => {
    set({ isLoading: true });
    try {
      const status = await ConsentService.getStatus();
      set({
        status,
        isConsentRequired: !status.dataProcessing,
        isLoading: false,
      });
    } catch {
      // If consent check fails, don't block the app — assume consented
      set({ isLoading: false, isConsentRequired: false });
    }
  },

  grant: async (type) => {
    set({ isGranting: true, error: null });
    try {
      await ConsentService.grant(type);
      // Re-fetch to get latest status
      const status = await ConsentService.getStatus();
      set({
        status,
        isConsentRequired: !status.dataProcessing,
        isGranting: false,
      });
    } catch (err) {
      set({ isGranting: false, error: (err as ApiError).message });
    }
  },

  withdraw: async (type) => {
    try {
      await ConsentService.withdraw(type);
      const status = await ConsentService.getStatus();
      set({ status, isConsentRequired: !status.dataProcessing });
    } catch (err) {
      set({ error: (err as ApiError).message });
    }
  },
}));

export default useConsentStore;