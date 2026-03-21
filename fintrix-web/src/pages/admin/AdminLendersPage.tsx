// ================================================================
// AdminLendersPage.tsx  —  /admin/lenders  (v2 — all fields shown)
// GET  /api/v1/admin/lenders
// PUT  /api/v1/admin/lenders/{id}      → update rates/fees
// POST /api/v1/admin/lenders/{id}/toggle → activate/deactivate
//
// All AdminLender fields displayed:
//   Table: name, logoUrl, loanType, rate range, processingFee,
//          minCreditScore, maxFoir, minMonthlyIncome, isActive
//   Expanded row: minAge, maxAge, minEmploymentYears,
//                 allowedEmploymentTypes, minLoanAmount, maxLoanAmount
// ================================================================

import { useEffect, useState, Fragment } from 'react';
import AdminService   from '../../services/admin.service';
import { formatPercent, formatCurrency } from '../../utils/formatters';
import type { AdminLender } from '../../types/api.types';
import './AdminLendersPage.css';

const AdminLendersPage = () => {
  const [lenders, setLenders]         = useState<AdminLender[]>([]);
  const [page, setPage]               = useState(0);
  const [total, setTotal]             = useState(0);
  const [totalPages, setTotalPages]   = useState(1);
  const [loading, setLoading]         = useState(true);
  const [error, setError]             = useState<string | null>(null);
  const [editing, setEditing]         = useState<string | null>(null);
  const [editBuf, setEditBuf]         = useState<Partial<AdminLender>>({});
  const [expanded, setExpanded]       = useState<string | null>(null);

  const load = (p: number) => {
    setLoading(true);
    AdminService.getLenders(p)
      .then(paged => {
        setLenders(paged.content);
        setTotal(paged.totalElements);
        setTotalPages(paged.totalPages);
        setPage(paged.number);
      })
      .catch(e => setError(e.message))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(0); }, []);

  const startEdit = (l: AdminLender) => {
    setEditing(l.id);
    setEditBuf({
      minInterestRate:    l.minInterestRate,
      maxInterestRate:    l.maxInterestRate,
      processingFeePercent: l.processingFeePercent,
      minCreditScore:     l.minCreditScore,
      maxFoir:            l.maxFoir,
      minMonthlyIncome:   l.minMonthlyIncome,
    });
  };

  const saveEdit = async (id: string) => {
    try {
      const updated = await AdminService.updateLender(id, editBuf);
      setLenders(ls => ls.map(l => l.id === id ? updated : l));
      setEditing(null);
    } catch (e: unknown) { setError((e as Error).message); }
  };

  const toggle = async (id: string) => {
    try {
      const updated = await AdminService.toggleLender(id);
      setLenders(ls => ls.map(l => l.id === id ? updated : l));
    } catch (e: unknown) { setError((e as Error).message); }
  };

  const COLS = 9; // number of <th> columns for colspan

  return (
    <>
      <div className="admin-page__header">
        <h1 className="admin-page__title">Lenders</h1>
      </div>

      {error && <div className="alert alert-error" style={{ marginBottom: 'var(--space-4)' }}>{error}</div>}

      <div className="admin-page__actions">
        <span className="admin-page__count">{total} lenders total</span>
      </div>

      {loading ? <div className="spinner spinner-lg" /> : (
        <div className="admin-table-wrap">
          <table className="admin-table">
            <thead>
              <tr>
                <th></th>{/* logo */}
                <th>Name</th>
                <th>Loan Type</th>
                <th>Rate Range</th>
                <th>Proc. Fee</th>
                <th>Min Credit</th>
                <th>Max FOIR</th>
                <th>Min Income</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {lenders.map(l => {
                const isEd  = editing  === l.id;
                const isExp = expanded === l.id;
                const buf   = editBuf;
                return (
                  <Fragment key={l.id}>
                    <tr>
                      {/* Logo */}
                      <td style={{ width: 40 }}>
                        {l.logoUrl
                          ? <img src={l.logoUrl} alt={l.name}
                              style={{ height: 24, width: 'auto', objectFit: 'contain', borderRadius: 4 }}
                              onError={e => { (e.target as HTMLImageElement).style.display = 'none'; }} />
                          : <span style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)' }}>—</span>
                        }
                      </td>

                      <td className="admin-table__name">{l.name}</td>
                      <td>{l.loanType.replace('_', ' ')}</td>

                      {/* Rate range — editable */}
                      <td className="admin-table__mono">
                        {isEd ? (
                          <span style={{ display: 'flex', gap: 4, alignItems: 'center' }}>
                            <input type="number" className="form-input" style={{ width: 60 }}
                              value={buf.minInterestRate ?? ''} step="0.1"
                              onChange={e => setEditBuf(b => ({ ...b, minInterestRate: Number(e.target.value) }))} />
                            –
                            <input type="number" className="form-input" style={{ width: 60 }}
                              value={buf.maxInterestRate ?? ''} step="0.1"
                              onChange={e => setEditBuf(b => ({ ...b, maxInterestRate: Number(e.target.value) }))} />
                          </span>
                        ) : `${formatPercent(l.minInterestRate)} – ${formatPercent(l.maxInterestRate)}`}
                      </td>

                      {/* Processing fee — editable */}
                      <td className="admin-table__mono">
                        {isEd ? (
                          <input type="number" className="form-input" style={{ width: 70 }}
                            value={buf.processingFeePercent ?? ''} step="0.01"
                            onChange={e => setEditBuf(b => ({ ...b, processingFeePercent: Number(e.target.value) }))} />
                        ) : formatPercent(l.processingFeePercent)}
                      </td>

                      {/* Min credit score — editable */}
                      <td className="admin-table__mono">
                        {isEd ? (
                          <input type="number" className="form-input" style={{ width: 70 }}
                            value={buf.minCreditScore ?? ''}
                            onChange={e => setEditBuf(b => ({ ...b, minCreditScore: Number(e.target.value) }))} />
                        ) : l.minCreditScore}
                      </td>

                      {/* Max FOIR — editable */}
                      <td className="admin-table__mono">
                        {isEd ? (
                          <input type="number" className="form-input" style={{ width: 70 }}
                            value={buf.maxFoir ?? ''} step="0.01"
                            onChange={e => setEditBuf(b => ({ ...b, maxFoir: Number(e.target.value) }))} />
                        ) : formatPercent(l.maxFoir)}
                      </td>

                      {/* Min income — editable */}
                      <td className="admin-table__mono">
                        {isEd ? (
                          <input type="number" className="form-input" style={{ width: 90 }}
                            value={buf.minMonthlyIncome ?? ''}
                            onChange={e => setEditBuf(b => ({ ...b, minMonthlyIncome: Number(e.target.value) }))} />
                        ) : formatCurrency(l.minMonthlyIncome, true)}
                      </td>

                      <td>
                        <span className={`badge ${l.isActive ? 'badge-success' : 'badge-danger'}`}>
                          {l.isActive ? 'Active' : 'Inactive'}
                        </span>
                      </td>

                      <td>
                        <span style={{ display: 'flex', gap: 'var(--space-2)', flexWrap: 'wrap' }}>
                          {isEd ? (
                            <>
                              <button className="btn btn-primary btn-sm" onClick={() => saveEdit(l.id)}>Save</button>
                              <button className="btn btn-ghost btn-sm" onClick={() => setEditing(null)}>Cancel</button>
                            </>
                          ) : (
                            <>
                              <button className="btn btn-secondary btn-sm" onClick={() => startEdit(l)}>Edit</button>
                              <button
                                className={`btn btn-sm ${l.isActive ? 'btn-danger' : 'btn-ghost'}`}
                                onClick={() => toggle(l.id)}
                              >
                                {l.isActive ? 'Disable' : 'Enable'}
                              </button>
                              <button
                                className="btn btn-ghost btn-sm"
                                onClick={() => setExpanded(isExp ? null : l.id)}
                              >
                                {isExp ? '▲' : '▼'}
                              </button>
                            </>
                          )}
                        </span>
                      </td>
                    </tr>

                    {/* ── Expanded detail row — shows remaining fields ── */}
                    {isExp && (
                      <tr key={`${l.id}-detail`}>
                        <td colSpan={COLS + 1} style={{
                          padding: 'var(--space-4) var(--space-6)',
                          background: 'var(--color-bg-elevated)',
                          borderBottom: '1px solid var(--color-border-subtle)',
                        }}>
                          <div style={{
                            display: 'grid',
                            gridTemplateColumns: 'repeat(auto-fill, minmax(180px, 1fr))',
                            gap: 'var(--space-4)',
                          }}>
                            <div>
                              <p style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: 4 }}>Age Range</p>
                              <p style={{ fontFamily: 'var(--font-mono)', fontSize: 'var(--text-base)', color: 'var(--color-text-secondary)' }}>
                                {l.minAge} – {l.maxAge} yrs
                              </p>
                            </div>
                            <div>
                              <p style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: 4 }}>Min Employment</p>
                              <p style={{ fontFamily: 'var(--font-mono)', fontSize: 'var(--text-base)', color: 'var(--color-text-secondary)' }}>
                                {l.minEmploymentYears != null ? `${l.minEmploymentYears} yr${l.minEmploymentYears !== 1 ? 's' : ''}` : '—'}
                              </p>
                            </div>
                            <div>
                              <p style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: 4 }}>Loan Range</p>
                              <p style={{ fontFamily: 'var(--font-mono)', fontSize: 'var(--text-base)', color: 'var(--color-text-secondary)' }}>
                                {l.minLoanAmount ? formatCurrency(l.minLoanAmount, true) : '—'}
                                {' – '}
                                {l.maxLoanAmount ? formatCurrency(l.maxLoanAmount, true) : '—'}
                              </p>
                            </div>
                            <div style={{ gridColumn: 'span 2' }}>
                              <p style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: 4 }}>Allowed Employment Types</p>
                              <p style={{ fontSize: 'var(--text-sm)', color: 'var(--color-text-secondary)' }}>
                                {l.allowedEmploymentTypes
                                  ? l.allowedEmploymentTypes.split(',').map(t => (
                                      <span key={t} className="badge badge-info" style={{ marginRight: 'var(--space-1)', fontSize: 'var(--text-xs)' }}>
                                        {t.trim()}
                                      </span>
                                    ))
                                  : '—'}
                              </p>
                            </div>
                          </div>
                        </td>
                      </tr>
                    )}
                  </Fragment>
                );
              })}
            </tbody>
          </table>
        </div>
      )}

      {totalPages > 1 && (
        <div className="admin-page__pagination">
          <button className="btn btn-secondary btn-sm" disabled={page === 0} onClick={() => load(page - 1)}>← Prev</button>
          <span style={{ fontSize: 'var(--text-sm)', color: 'var(--color-text-muted)', alignSelf: 'center' }}>
            Page {page + 1} of {totalPages}
          </span>
          <button className="btn btn-secondary btn-sm" disabled={page + 1 >= totalPages} onClick={() => load(page + 1)}>Next →</button>
        </div>
      )}
    </>
  );
};

export default AdminLendersPage;