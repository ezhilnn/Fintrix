// ================================================================
// emi.service.ts
//
// POST   /api/v1/emi-tracker         → add EMI
// GET    /api/v1/emi-tracker         → list my EMIs
// DELETE /api/v1/emi-tracker/{id}    → remove EMI
// ================================================================

import { get, post, del } from './api.client';
import { API } from '../utils/constants';
import type { EmiTrackerRequest, EmiTrackerResponse } from '../types/api.types';

const EmiService = {

  addEmi(request: EmiTrackerRequest): Promise<EmiTrackerResponse> {
    return post<EmiTrackerResponse>(API.EMI_TRACKER, request);
  },

  getMyEmis(): Promise<EmiTrackerResponse[]> {
    return get<EmiTrackerResponse[]>(API.EMI_TRACKER);
  },

  deleteEmi(id: string): Promise<void> {
    return del<void>(API.EMI_TRACKER_DELETE(id));
  },
};

export default EmiService;