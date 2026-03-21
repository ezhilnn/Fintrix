// ================================================================
// AdminDashboardPage.tsx  —  /admin/dashboard
// GET /api/v1/admin/dashboard → AdminDashboardStats
// ================================================================

import { useEffect, useState } from 'react';
import AdminService            from '../../services/admin.service';
import { formatCurrency }      from '../../utils/formatters';
import type { AdminDashboardStats } from '../../types/api.types';
import './AdminDashboardPage.css';

const AdminDashboardPage = () => {
  const [stats, setStats]     = useState<AdminDashboardStats | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError]     = useState<string | null>(null);

  useEffect(() => {
    AdminService.getStats()
      .then(setStats)
      .catch(e => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  // Use ?? 0 on every field — backend may return null instead of 0
  // when there are no records yet (e.g. no affiliate clicks yet)
  const kpis = stats ? [
    { icon: '👥', label: 'Total Users',          value: (stats.totalUsers            ?? 0).toLocaleString(), cls: '' },
    { icon: '✅', label: 'Active Users',          value: (stats.activeUsers           ?? 0).toLocaleString(), cls: 'admin-dash__kpi-value--brand' },
    { icon: '🏦', label: 'Loan Checks',           value: (stats.totalLoanChecks       ?? 0).toLocaleString(), cls: '' },
    { icon: '💳', label: 'Card Checks',           value: (stats.totalCardChecks       ?? 0).toLocaleString(), cls: '' },
    { icon: '🔍', label: 'Fraud Checks',          value: (stats.totalFraudChecks      ?? 0).toLocaleString(), cls: '' },
    { icon: '🔗', label: 'Affiliate Clicks',      value: (stats.totalAffiliateClicks  ?? 0).toLocaleString(), cls: '' },
    { icon: '🎯', label: 'Conversions',           value: (stats.totalConversions      ?? 0).toLocaleString(), cls: 'admin-dash__kpi-value--brand' },
    { icon: '₹',  label: 'Est. Revenue',          value: formatCurrency(stats.estimatedRevenue ?? 0, true),   cls: 'admin-dash__kpi-value--brand' },
    { icon: '🔔', label: 'Notifications Sent',    value: (stats.totalNotificationsSent ?? 0).toLocaleString(), cls: '' },
    { icon: '⚠',  label: 'Pending Consent Users', value: (stats.pendingConsentUsers   ?? 0).toLocaleString(), cls: (stats.pendingConsentUsers ?? 0) > 0 ? 'admin-dash__kpi-value--warn' : '' },
  ] : [];

  return (
    <>
      <div className="admin-dash__header">
        <h1 className="admin-dash__title">Platform Overview</h1>
        <p className="admin-dash__sub">Live KPIs from the Fintrix backend.</p>
      </div>

      {loading && <div className="spinner spinner-lg" />}
      {error   && <div className="alert alert-error">{error}</div>}

      {stats && (
        <div className="admin-dash__grid">
          {kpis.map(kpi => (
            <div key={kpi.label} className="admin-dash__kpi">
              <span className="admin-dash__kpi-icon">{kpi.icon}</span>
              <span className="admin-dash__kpi-label">{kpi.label}</span>
              <span className={`admin-dash__kpi-value ${kpi.cls}`}>{kpi.value}</span>
            </div>
          ))}
        </div>
      )}
    </>
  );
};

export default AdminDashboardPage;