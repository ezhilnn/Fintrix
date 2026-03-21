// ================================================================
// AdminFraudPage.tsx  —  /admin/fraud
// GET    /api/v1/admin/fraud/keywords
// POST   /api/v1/admin/fraud/keywords
// DELETE /api/v1/admin/fraud/keywords/{id} → soft deactivate
// ================================================================

import { useEffect, useState } from 'react';
import AdminService  from '../../services/admin.service';
import { formatDate } from '../../utils/formatters';
import type { FraudKeyword } from '../../types/api.types';
import './AdminFraudPage.css';

const RISK_LEVELS = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];
const FRAUD_TYPES = ['PONZI', 'ADVANCE_FEE', 'FAKE_INVESTMENT', 'UNREGISTERED_BROKER', 'CHIT_FUND_SCAM', 'OTHER'];

const riskBadge = (r: string) => ({
  LOW:      'badge-success',
  MEDIUM:   'badge-warning',
  HIGH:     'badge-orange',
  CRITICAL: 'badge-danger',
}[r] ?? 'badge-info');

const AdminFraudPage = () => {
  const [keywords, setKeywords]   = useState<FraudKeyword[]>([]);
  const [loading, setLoading]     = useState(true);
  const [error, setError]         = useState<string | null>(null);
  const [newKw, setNewKw]         = useState({
    keyword: '', riskLevel: 'MEDIUM', fraudType: 'OTHER', description: '',
  });

  useEffect(() => {
    AdminService.getKeywords()
      .then(setKeywords)
      .catch(e => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  const handleAdd = async () => {
    if (!newKw.keyword.trim()) return;
    try {
      const created = await AdminService.addKeyword(newKw);
      setKeywords(ks => [created, ...ks]);
      setNewKw({ keyword: '', riskLevel: 'MEDIUM', fraudType: 'OTHER', description: '' });
    } catch (e: unknown) { setError((e as Error).message); }
  };

  const deactivate = async (id: string) => {
    try {
      await AdminService.deactivateKeyword(id);
      setKeywords(ks => ks.map(k => k.id === id ? { ...k, isActive: false } : k));
    } catch (e: unknown) { setError((e as Error).message); }
  };

  return (
    <>
      <div className="admin-page__header">
        <h1 className="admin-page__title">Fraud Keywords</h1>
      </div>

      {error && <div className="alert alert-error" style={{ marginBottom: 'var(--space-4)' }}>{error}</div>}

      {/* Add keyword form */}
      <div className="admin-fraud__add">
        <div className="admin-fraud__add-field">
          <label className="form-label">Keyword *</label>
          <input className="form-input" placeholder="e.g. guaranteed returns"
            value={newKw.keyword} onChange={e => setNewKw(k => ({ ...k, keyword: e.target.value }))} />
        </div>
        <div className="admin-fraud__add-field">
          <label className="form-label">Risk Level</label>
          <select className="form-input" value={newKw.riskLevel}
            onChange={e => setNewKw(k => ({ ...k, riskLevel: e.target.value }))}>
            {RISK_LEVELS.map(r => <option key={r} value={r}>{r}</option>)}
          </select>
        </div>
        <div className="admin-fraud__add-field">
          <label className="form-label">Fraud Type</label>
          <select className="form-input" value={newKw.fraudType}
            onChange={e => setNewKw(k => ({ ...k, fraudType: e.target.value }))}>
            {FRAUD_TYPES.map(t => <option key={t} value={t}>{t.replace(/_/g,' ')}</option>)}
          </select>
        </div>
        <div className="admin-fraud__add-field" style={{ flex: 2 }}>
          <label className="form-label">Description</label>
          <input className="form-input" placeholder="Optional context"
            value={newKw.description} onChange={e => setNewKw(k => ({ ...k, description: e.target.value }))} />
        </div>
        <button className="btn btn-primary" onClick={handleAdd} style={{ flexShrink: 0 }}>
          + Add
        </button>
      </div>

      <div className="admin-page__actions">
        <span className="admin-page__count">{keywords.length} keywords</span>
      </div>

      {loading ? <div className="spinner spinner-lg" /> : (
        <div className="admin-table-wrap">
          <table className="admin-table">
            <thead>
              <tr>
                <th>Keyword</th><th>Risk Level</th><th>Fraud Type</th>
                <th>Description</th><th>Added</th><th>Status</th><th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {keywords.map(kw => (
                <tr key={kw.id}>
                  <td className="admin-table__name">{kw.keyword}</td>
                  <td><span className={`badge ${riskBadge(kw.riskLevel)}`}>{kw.riskLevel}</span></td>
                  <td>{kw.fraudType.replace(/_/g, ' ')}</td>
                  <td style={{ maxWidth: 200, color: 'var(--color-text-muted)', fontSize: 'var(--text-xs)' }}>
                    {kw.description ?? '—'}
                  </td>
                  <td style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)' }}>
                    {formatDate(kw.createdAt)}
                  </td>
                  <td>
                    <span className={`badge ${kw.isActive ? 'badge-success' : 'badge-danger'}`}>
                      {kw.isActive ? 'Active' : 'Inactive'}
                    </span>
                  </td>
                  <td>
                    {kw.isActive && (
                      <button className="btn btn-danger btn-sm" onClick={() => deactivate(kw.id)}>
                        Disable
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </>
  );
};

export default AdminFraudPage;