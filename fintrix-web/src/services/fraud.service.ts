// ================================================================
// fraud.service.ts
//
// Mirrors FraudCheckController.java — /api/v1/fraud
//
// Feature 1 — Entity Check:
//   POST /api/v1/fraud/check      → checkEntity()
//   GET  /api/v1/fraud/my-alerts  → getMyAlerts()
//
// Feature 2 — Keyword Scan (NEW):
//   POST /api/v1/fraud/scan              → scanText()
//   GET  /api/v1/fraud/scan/content-types → getContentTypes()
//   User pastes any text (WhatsApp, SMS, email, pitch)
//   Backend scans for 200+ fraud keywords with fuzzy matching
// ================================================================

import { get, post } from './api.client';
import { API } from '../utils/constants';
import type {
  FraudCheckRequest,
  FraudCheckResponse,
  KeywordScanRequest,
  KeywordScanResponse,
  ContentTypeOption,
} from '../types/api.types';

const FraudService = {

  // ── Feature 1: Entity Registry Check ─────────────────────────
  checkEntity(request: FraudCheckRequest): Promise<FraudCheckResponse> {
    return post<FraudCheckResponse>(API.FRAUD_CHECK, request);
  },

  getMyAlerts(): Promise<FraudCheckResponse[]> {
    return get<FraudCheckResponse[]>(API.FRAUD_ALERTS);
  },

  // ── Feature 2: Free-text Keyword Scan (NEW) ───────────────────
  // Paste any message text — returns per-keyword breakdown
  scanText(request: KeywordScanRequest): Promise<KeywordScanResponse> {
    return post<KeywordScanResponse>(API.FRAUD_SCAN, request);
  },

  // Returns content type options for the dropdown
  getContentTypes(): Promise<ContentTypeOption[]> {
    return get<ContentTypeOption[]>(API.FRAUD_CONTENT_TYPES);
  },
};

export default FraudService;