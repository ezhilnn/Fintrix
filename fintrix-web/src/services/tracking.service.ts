// ================================================================
// tracking.service.ts
//
// POST /api/v1/tracking/event           → log frontend event (fire-and-forget)
// GET  /api/v1/tracking/affiliate-link  → get tracked apply URL
//
// Usage pattern:
//   TrackingService.trackEvent({ eventType: 'CARD_VIEW', entityId: card.cardId });
//   const link = await TrackingService.getAffiliateLink(cardId, 'CARD', 85);
//   if (link.hasPartnership) window.open(link.trackedUrl);
// ================================================================

import { get, post } from './api.client';
import { API } from '../utils/constants';
import type { TrackEventRequest, AffiliateClickResponse } from '../types/api.types';

const TrackingService = {

  // Fire-and-forget — swallow errors silently so tracking never breaks the UI
  trackEvent(request: TrackEventRequest): void {
    post<void>(API.TRACK_EVENT, request).catch(() => {/* silent */});
  },

  getAffiliateLink(
    entityId: string,
    productType: string,     // 'LOAN' | 'CARD'
    approvalProbability?: number,
  ): Promise<AffiliateClickResponse> {
    return get<AffiliateClickResponse>(API.AFFILIATE_LINK, {
      entityId,
      productType,
      ...(approvalProbability !== undefined && { approvalProbability }),
    });
  },
};

export default TrackingService;