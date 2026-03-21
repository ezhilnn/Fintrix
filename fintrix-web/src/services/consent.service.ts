// ================================================================
// consent.service.ts
//
// GET  /api/v1/consent          → get current consent status
// POST /api/v1/consent/grant    → grant a consent type
// POST /api/v1/consent/withdraw → withdraw a consent type
//
// Critical flow:
//   1. After login → getStatus()
//   2. If dataProcessing = false → show ConsentGate modal
//   3. User grants DATA_PROCESSING → app unlocks
// ================================================================

import { get, post } from './api.client';
import { API } from '../utils/constants';
import type { ConsentStatusResponse, ConsentRequest, ConsentType } from '../types/api.types';

const ConsentService = {

  getStatus(): Promise<ConsentStatusResponse> {
    return get<ConsentStatusResponse>(API.CONSENT);
  },

  grant(consentType: ConsentType): Promise<void> {
    const request: ConsentRequest = { consentType };
    return post<void>(API.CONSENT_GRANT, request);
  },

  withdraw(consentType: ConsentType): Promise<void> {
    return post<void>(`${API.CONSENT_WITHDRAW}?consentType=${consentType}`);
  },
};

export default ConsentService;