// ================================================================
// AdminCardsPage.tsx  —  /admin/cards  (v2 — all fields shown)
// GET    /api/v1/admin/cards
// POST   /api/v1/admin/cards
// PUT    /api/v1/admin/cards/{id}
// DELETE /api/v1/admin/cards/{id} → soft deactivate
//
// All AdminCard fields:
//   Table: logo, bankName, cardName, cardCategory, rewardType,
//          minCreditScore, minMonthlyIncome, joiningFee, annualFee, isActive
//   Expanded row: minAge, maxAge, allowedEmploymentTypes,
//                 annualFeeWaiverCondition, rewardRate,
//                 welcomeBenefit, keyBenefits[]
// ================================================================

import { useEffect, useState, Fragment } from 'react';
import AdminService   from '../../services/admin.service';
import { formatCurrency } from '../../utils/formatters';
import type { AdminCard } from '../../types/api.types';
import './AdminCardsPage.css';

const EMPTY_CARD: Partial<AdminCard> = {
  bankName: '', cardName: '', cardCategory: 'ENTRY_LEVEL',
  rewardType: 'CASHBACK', minCreditScore: 650,
  minMonthlyIncome: 25000, minAge: 21, maxAge: 65,
  joiningFee: 0, annualFee: 0,
  cardNetwork: 'Visa', interestRate: 42.00,
  fuelSurchargeWaiver: false, internationalUsage: true,
  applyUrl: '',
};

const AdminCardsPage = () => {
  const [cards, setCards]           = useState<AdminCard[]>([]);
  const [page, setPage]             = useState(0);
  const [total, setTotal]           = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading]       = useState(true);
  const [error, setError]           = useState<string | null>(null);
  const [showAdd, setShowAdd]       = useState(false);
  const [newCard, setNewCard]       = useState<Partial<AdminCard>>(EMPTY_CARD);
  const [editing, setEditing]       = useState<string | null>(null);
  const [editBuf, setEditBuf]       = useState<Partial<AdminCard>>({});
  const [expanded, setExpanded]     = useState<string | null>(null);

  const load = (p: number) => {
    setLoading(true);
    AdminService.getCards(p)
      .then(paged => {
        setCards(paged.content);
        setTotal(paged.totalElements);
        setTotalPages(paged.totalPages);
        setPage(paged.number);
      })
      .catch(e => setError(e.message))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(0); }, []);

  const handleAdd = async () => {
    try {
      const created = await AdminService.addCard(newCard);
      setCards(cs => [created, ...cs]);
      setNewCard(EMPTY_CARD);
      setShowAdd(false);
      setTotal(t => t + 1);
    } catch (e: unknown) { setError((e as Error).message); }
  };

  const saveEdit = async (id: string) => {
    try {
      const updated = await AdminService.updateCard(id, editBuf);
      setCards(cs => cs.map(c => c.id === id ? updated : c));
      setEditing(null);
    } catch (e: unknown) { setError((e as Error).message); }
  };

  const deactivate = async (id: string) => {
    if (!window.confirm('Deactivate this card?')) return;
    try {
      await AdminService.deactivateCard(id);
      setCards(cs => cs.map(c => c.id === id ? { ...c, isActive: false } : c));
    } catch (e: unknown) { setError((e as Error).message); }
  };

  const setNew  = (k: keyof AdminCard, v: string | number) => setNewCard(c => ({ ...c, [k]: v }));
  const setEdit = (k: keyof AdminCard, v: string | number) => setEditBuf(b => ({ ...b, [k]: v }));

  const COLS = 10;

  return (
    <>
      <div className="admin-page__header">
        <h1 className="admin-page__title">Credit Cards</h1>
      </div>

      {error && <div className="alert alert-error" style={{ marginBottom: 'var(--space-4)' }}>{error}</div>}

      {showAdd && (
        <div className="admin-cards__add-form">
          <p className="admin-cards__add-title">Add New Card</p>
          <div className="admin-cards__add-grid">
            {(['bankName','cardName'] as const).map(k => (
              <div key={k} className="form-group">
                <label className="form-label">{k === 'bankName' ? 'Bank Name' : 'Card Name'}</label>
                <input className="form-input" value={newCard[k] ?? ''}
                  onChange={e => setNew(k, e.target.value)} />
              </div>
            ))}
            <div className="form-group">
              <label className="form-label">Min Credit Score</label>
              <input type="number" className="form-input" value={newCard.minCreditScore ?? ''}
                onChange={e => setNew('minCreditScore', Number(e.target.value))} />
            </div>
            <div className="form-group">
              <label className="form-label">Min Monthly Income (₹)</label>
              <input type="number" className="form-input" value={newCard.minMonthlyIncome ?? ''}
                onChange={e => setNew('minMonthlyIncome', Number(e.target.value))} />
            </div>
            <div className="form-group">
              <label className="form-label">Annual Fee (₹)</label>
              <input type="number" className="form-input" value={newCard.annualFee ?? ''}
                onChange={e => setNew('annualFee', Number(e.target.value))} />
            </div>
            <div className="form-group">
              <label className="form-label">Joining Fee (₹)</label>
              <input type="number" className="form-input" value={newCard.joiningFee ?? ''}
                onChange={e => setNew('joiningFee', Number(e.target.value))} />
            </div>
            <div className="form-group">
              <label className="form-label">Reward Rate</label>
              <input className="form-input" placeholder="e.g. 1.5% on all spends"
                value={newCard.rewardRate ?? ''} onChange={e => setNew('rewardRate', e.target.value)} />
            </div>
            <div className="form-group">
              <label className="form-label">Annual Fee Waiver</label>
              <input className="form-input" placeholder="e.g. Spend ₹1.5L in a year"
                value={newCard.annualFeeWaiverCondition ?? ''}
                onChange={e => setNew('annualFeeWaiverCondition', e.target.value)} />
            </div>
            <div className="form-group">
              <label className="form-label">Apply URL</label>
              <input className="form-input" placeholder="https://bank.com/apply-card"
                value={newCard.applyUrl ?? ''} onChange={e => setNew('applyUrl', e.target.value)} />
            </div>
            <div className="form-group">
              <label className="form-label">Card Network</label>
              <select className="form-input" value={newCard.cardNetwork ?? 'Visa'}
                onChange={e => setNew('cardNetwork', e.target.value)}>
                {['Visa','Mastercard','Amex','RuPay'].map(n => <option key={n} value={n}>{n}</option>)}
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">Interest Rate (%)</label>
              <input type="number" className="form-input" step="0.1"
                value={newCard.interestRate ?? 42} onChange={e => setNew('interestRate', Number(e.target.value))} />
            </div>
            <div className="form-group">
              <label className="form-label">Welcome Benefit</label>
              <input className="form-input" placeholder="e.g. ₹500 gift voucher"
                value={newCard.welcomeBenefit ?? ''}
                onChange={e => setNew('welcomeBenefit', e.target.value)} />
            </div>
            <div className="form-group">
              <label className="form-label">Lounge Access</label>
              <input className="form-input" placeholder="e.g. 2 domestic visits/quarter"
                value={newCard.loungeAccess ?? ''}
                onChange={e => setNew('loungeAccess', e.target.value)} />
            </div>
          </div>
          <div style={{ display: 'flex', gap: 'var(--space-3)' }}>
            <button className="btn btn-primary" onClick={handleAdd}>Add Card</button>
            <button className="btn btn-ghost" onClick={() => setShowAdd(false)}>Cancel</button>
          </div>
        </div>
      )}

      <div className="admin-page__actions">
        <span className="admin-page__count">{total} cards total</span>
        {!showAdd && (
          <button className="btn btn-primary btn-sm" onClick={() => setShowAdd(true)}>+ Add Card</button>
        )}
      </div>

      {loading ? <div className="spinner spinner-lg" /> : (
        <div className="admin-table-wrap">
          <table className="admin-table">
            <thead>
              <tr>
                <th></th>{/* logo */}
                <th>Bank</th><th>Card Name</th><th>Category</th>
                <th>Min Credit</th><th>Min Income</th>
                <th>Annual Fee</th><th>Joining Fee</th>
                <th>Status</th><th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {cards.map(c => {
                const isEd  = editing  === c.id;
                const isExp = expanded === c.id;
                return (
                  <Fragment key={c.id}>
                    {/* ── Main table row ─────────────────────── */}
                    <tr>
                      <td style={{ width: 40 }}>
                        {c.logoUrl
                          ? <img src={c.logoUrl} alt={c.bankName}
                              style={{ height: 24, width: 'auto', objectFit: 'contain', borderRadius: 4 }}
                              onError={e => { (e.target as HTMLImageElement).style.display = 'none'; }} />
                          : <span style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)' }}>—</span>
                        }
                      </td>
                      <td className="admin-table__name">{c.bankName}</td>
                      <td>{c.cardName}</td>
                      <td><span className="badge badge-info">{c.cardCategory}</span></td>
                      <td className="admin-table__mono">{c.minCreditScore}</td>
                      <td className="admin-table__mono">{formatCurrency(c.minMonthlyIncome, true)}</td>
                      <td className="admin-table__mono">{formatCurrency(c.annualFee)}</td>
                      <td className="admin-table__mono">{formatCurrency(c.joiningFee)}</td>
                      <td><span className={`badge ${c.isActive ? 'badge-success' : 'badge-danger'}`}>{c.isActive ? 'Active' : 'Inactive'}</span></td>
                      <td>
                        <span style={{ display: 'flex', gap: 'var(--space-1)', flexWrap: 'wrap' }}>
                          <button className="btn btn-secondary btn-sm"
                            onClick={() => { setEditing(isEd ? null : c.id); setEditBuf({ ...c }); }}>
                            {isEd ? 'Close' : 'Edit'}
                          </button>
                          {c.isActive && (
                            <button className="btn btn-danger btn-sm" onClick={() => deactivate(c.id)}>Disable</button>
                          )}
                          <button className="btn btn-ghost btn-sm"
                            onClick={() => setExpanded(isExp ? null : c.id)}>
                            {isExp ? '▲' : '▼'}
                          </button>
                        </span>
                      </td>
                    </tr>

                    {/* ── Full edit form row — all fields ───── */}
                    {isEd && (
                      <tr key={`${c.id}-edit`}>
                        <td colSpan={10} style={{
                          padding: 'var(--space-5) var(--space-6)',
                          background: 'var(--color-bg-elevated)',
                          borderBottom: '1px solid var(--color-border-subtle)',
                        }}>
                          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(180px, 1fr))', gap: 'var(--space-3)', marginBottom: 'var(--space-4)' }}>
                            {/* Identity */}
                            <div className="form-group"><label className="form-label">Bank Name</label>
                              <input className="form-input" value={editBuf.bankName ?? c.bankName} onChange={e => setEdit('bankName', e.target.value)} /></div>
                            <div className="form-group"><label className="form-label">Card Name</label>
                              <input className="form-input" value={editBuf.cardName ?? c.cardName} onChange={e => setEdit('cardName', e.target.value)} /></div>
                            <div className="form-group"><label className="form-label">Logo URL</label>
                              <input className="form-input" placeholder="https://..." value={editBuf.logoUrl ?? c.logoUrl ?? ''} onChange={e => setEdit('logoUrl', e.target.value)} /></div>
                            <div className="form-group"><label className="form-label">Apply URL</label>
                              <input className="form-input" placeholder="https://bank.com/apply-card" value={editBuf.applyUrl ?? c.applyUrl ?? ''} onChange={e => setEdit('applyUrl', e.target.value)} /></div>
                            {/* Card specs */}
                            <div className="form-group"><label className="form-label">Card Network</label>
                              <select className="form-input" value={editBuf.cardNetwork ?? c.cardNetwork ?? 'Visa'} onChange={e => setEdit('cardNetwork', e.target.value)}>
                                {['Visa','Mastercard','Amex','RuPay'].map(n => <option key={n} value={n}>{n}</option>)}
                              </select></div>
                            <div className="form-group"><label className="form-label">Interest Rate (%)</label>
                              <input type="number" className="form-input" step="0.1" value={editBuf.interestRate ?? c.interestRate ?? 42} onChange={e => setEdit('interestRate', Number(e.target.value))} /></div>
                            {/* Eligibility */}
                            <div className="form-group"><label className="form-label">Min Credit Score</label>
                              <input type="number" className="form-input" value={editBuf.minCreditScore ?? c.minCreditScore} onChange={e => setEdit('minCreditScore', Number(e.target.value))} /></div>
                            <div className="form-group"><label className="form-label">Min Monthly Income (₹)</label>
                              <input type="number" className="form-input" value={editBuf.minMonthlyIncome ?? c.minMonthlyIncome} onChange={e => setEdit('minMonthlyIncome', Number(e.target.value))} /></div>
                            <div className="form-group"><label className="form-label">Min Age</label>
                              <input type="number" className="form-input" value={editBuf.minAge ?? c.minAge} onChange={e => setEdit('minAge', Number(e.target.value))} /></div>
                            <div className="form-group"><label className="form-label">Max Age</label>
                              <input type="number" className="form-input" value={editBuf.maxAge ?? c.maxAge} onChange={e => setEdit('maxAge', Number(e.target.value))} /></div>
                            {/* Fees */}
                            <div className="form-group"><label className="form-label">Annual Fee (₹)</label>
                              <input type="number" className="form-input" value={editBuf.annualFee ?? c.annualFee} onChange={e => setEdit('annualFee', Number(e.target.value))} /></div>
                            <div className="form-group"><label className="form-label">Joining Fee (₹)</label>
                              <input type="number" className="form-input" value={editBuf.joiningFee ?? c.joiningFee} onChange={e => setEdit('joiningFee', Number(e.target.value))} /></div>
                            <div className="form-group"><label className="form-label">Annual Fee Waiver</label>
                              <input className="form-input" placeholder="Spend ₹1.5L/year" value={editBuf.annualFeeWaiverCondition ?? c.annualFeeWaiverCondition ?? ''} onChange={e => setEdit('annualFeeWaiverCondition', e.target.value)} /></div>
                            {/* Rewards */}
                            <div className="form-group"><label className="form-label">Reward Rate</label>
                              <input className="form-input" placeholder="1.5% cashback" value={editBuf.rewardRate ?? c.rewardRate ?? ''} onChange={e => setEdit('rewardRate', e.target.value)} /></div>
                            <div className="form-group"><label className="form-label">Welcome Benefit</label>
                              <input className="form-input" placeholder="₹500 gift voucher" value={editBuf.welcomeBenefit ?? c.welcomeBenefit ?? ''} onChange={e => setEdit('welcomeBenefit', e.target.value)} /></div>
                            <div className="form-group"><label className="form-label">Lounge Access</label>
                              <input className="form-input" placeholder="2 domestic/quarter" value={editBuf.loungeAccess ?? c.loungeAccess ?? ''} onChange={e => setEdit('loungeAccess', e.target.value)} /></div>
                            <div className="form-group"><label className="form-label">Allowed Employment</label>
                              <input className="form-input" placeholder="SALARIED,GOVERNMENT" value={editBuf.allowedEmploymentTypes ?? c.allowedEmploymentTypes ?? ''} onChange={e => setEdit('allowedEmploymentTypes', e.target.value)} /></div>
                          </div>
                          <span style={{ display: 'flex', gap: 'var(--space-3)' }}>
                            <button className="btn btn-primary btn-sm" onClick={() => saveEdit(c.id)}>Save Changes</button>
                            <button className="btn btn-ghost btn-sm" onClick={() => setEditing(null)}>Cancel</button>
                          </span>
                        </td>
                      </tr>
                    )}

                    {/* ── Expanded detail row — all remaining fields ── */}
                    {isExp && (
                      <tr key={`${c.id}-detail`}>
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
                              <p style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: 4 }}>Reward Type</p>
                              <span className="badge badge-brand">{c.rewardType}</span>
                            </div>
                            <div>
                              <p style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: 4 }}>Reward Rate</p>
                              <p style={{ fontSize: 'var(--text-sm)', color: 'var(--color-text-secondary)' }}>{c.rewardRate ?? '—'}</p>
                            </div>
                            <div>
                              <p style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: 4 }}>Card Network</p>
                              <span className="badge badge-info">{c.cardNetwork ?? '—'}</span>
                            </div>
                            <div>
                              <p style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: 4 }}>Interest Rate</p>
                              <p style={{ fontFamily: 'var(--font-mono)', fontSize: 'var(--text-sm)', color: 'var(--color-text-secondary)' }}>{c.interestRate != null ? `${c.interestRate}% p.a.` : '—'}</p>
                            </div>
                            <div>
                              <p style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: 4 }}>Age Range</p>
                              <p style={{ fontFamily: 'var(--font-mono)', fontSize: 'var(--text-sm)', color: 'var(--color-text-secondary)' }}>
                                {c.minAge} – {c.maxAge} yrs
                              </p>
                            </div>
                            <div>
                              <p style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: 4 }}>Annual Fee Waiver</p>
                              <p style={{ fontSize: 'var(--text-sm)', color: 'var(--color-text-secondary)' }}>{c.annualFeeWaiverCondition ?? '—'}</p>
                            </div>
                            <div>
                              <p style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: 4 }}>Welcome Benefit</p>
                              <p style={{ fontSize: 'var(--text-sm)', color: 'var(--color-text-secondary)' }}>{c.welcomeBenefit ?? '—'}</p>
                            </div>
                            <div>
                              <p style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: 4 }}>Fuel Waiver</p>
                              <span className={`badge ${c.fuelSurchargeWaiver ? 'badge-success' : 'badge-info'}`}>{c.fuelSurchargeWaiver ? 'Yes' : 'No'}</span>
                            </div>
                            <div>
                              <p style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: 4 }}>International</p>
                              <span className={`badge ${c.internationalUsage ? 'badge-success' : 'badge-danger'}`}>{c.internationalUsage ? 'Yes' : 'No'}</span>
                            </div>
                            <div>
                              <p style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: 4 }}>Lounge Access</p>
                              <p style={{ fontSize: 'var(--text-sm)', color: 'var(--color-text-secondary)' }}>{c.loungeAccess ?? '—'}</p>
                            </div>
                            <div>
                              <p style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: 4 }}>Apply URL</p>
                              <p style={{ fontSize: 'var(--text-xs)', color: 'var(--color-brand-primary)', wordBreak: 'break-all' }}>{c.applyUrl ?? '—'}</p>
                            </div>
                            {c.allowedEmploymentTypes && (
                              <div>
                                <p style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: 4 }}>Employment Types</p>
                                <p style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-secondary)' }}>
                                  {c.allowedEmploymentTypes.split(',').map(t => (
                                    <span key={t} className="badge badge-info" style={{ marginRight: 'var(--space-1)', fontSize: 'var(--text-xs)' }}>
                                      {t.trim()}
                                    </span>
                                  ))}
                                </p>
                              </div>
                            )}
                            {c.keyBenefits && c.keyBenefits.length > 0 && (
                              <div style={{ gridColumn: 'span 3' }}>
                                <p style={{ fontSize: 'var(--text-xs)', color: 'var(--color-text-disabled)', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: 4 }}>Key Benefits</p>
                                <ul style={{ paddingLeft: 'var(--space-4)', fontSize: 'var(--text-sm)', color: 'var(--color-text-secondary)' }}>
                                  {c.keyBenefits.map((b, i) => <li key={i}>{b}</li>)}
                                </ul>
                              </div>
                            )}
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

export default AdminCardsPage;