// ================================================================
// admin.service.ts
//
// All endpoints require role=ADMIN.
// Backend enforces via @PreAuthorize("hasRole('ADMIN')")
// Frontend additionally guards with AdminRoute component.
//
// GET    /api/v1/admin/dashboard
// GET    /api/v1/admin/lenders
// PUT    /api/v1/admin/lenders/{id}
// POST   /api/v1/admin/lenders/{id}/toggle
// GET    /api/v1/admin/cards
// POST   /api/v1/admin/cards
// PUT    /api/v1/admin/cards/{id}
// DELETE /api/v1/admin/cards/{id}
// GET    /api/v1/admin/fraud/keywords
// POST   /api/v1/admin/fraud/keywords
// DELETE /api/v1/admin/fraud/keywords/{id}
// ================================================================

import { get, post, put, del } from './api.client';
import { API } from '../utils/constants';
import type {
  AdminDashboardStats,
  AdminLender,
  AdminCard,
  FraudKeyword,
  Paged,
} from '../types/api.types';

const AdminService = {

  // ── Dashboard KPIs ──────────────────────────────────────────
  getStats(): Promise<AdminDashboardStats> {
    return get<AdminDashboardStats>(API.ADMIN_DASHBOARD);
  },

  // ── Lenders ─────────────────────────────────────────────────
  getLenders(page = 0, size = 20): Promise<Paged<AdminLender>> {
    return get<Paged<AdminLender>>(API.ADMIN_LENDERS, { page, size });
  },

  createLender(data: Partial<AdminLender>): Promise<AdminLender> {
    return post<AdminLender>(API.ADMIN_LENDERS, data);
  },

  updateLender(id: string, data: Partial<AdminLender>): Promise<AdminLender> {
    return put<AdminLender>(API.ADMIN_LENDER_UPDATE(id), data);
  },

  toggleLender(id: string): Promise<AdminLender> {
    return post<AdminLender>(API.ADMIN_LENDER_TOGGLE(id));
  },

  deleteLender(id: string): Promise<void> {
    return del<void>(API.ADMIN_LENDER_DELETE(id));
  },

  // ── Cards ────────────────────────────────────────────────────
  getCards(page = 0, size = 20): Promise<Paged<AdminCard>> {
    return get<Paged<AdminCard>>(API.ADMIN_CARDS, { page, size });
  },

  addCard(data: Partial<AdminCard>): Promise<AdminCard> {
    return post<AdminCard>(API.ADMIN_CARDS, data);
  },

  updateCard(id: string, data: Partial<AdminCard>): Promise<AdminCard> {
    return put<AdminCard>(API.ADMIN_CARD_UPDATE(id), data);
  },

  deactivateCard(id: string): Promise<void> {
    return del<void>(API.ADMIN_CARD_DELETE(id));
  },

  // ── Fraud Keywords ───────────────────────────────────────────
  getKeywords(): Promise<FraudKeyword[]> {
    return get<FraudKeyword[]>(API.ADMIN_FRAUD_KEYWORDS);
  },

  addKeyword(data: Omit<FraudKeyword, 'id' | 'isActive' | 'createdAt'>): Promise<FraudKeyword> {
    return post<FraudKeyword>(API.ADMIN_FRAUD_KEYWORDS, data);
  },

  deactivateKeyword(id: string): Promise<void> {
    return del<void>(API.ADMIN_FRAUD_KEYWORD_DEL(id));
  },
};

export default AdminService;