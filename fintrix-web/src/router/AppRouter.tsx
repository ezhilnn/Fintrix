// ================================================================
// AppRouter.tsx  — v2 update
//
// New additions:
//   - ConsentGate wraps all protected content (DATA_PROCESSING gate)
//   - Notification polling started on app boot
//   - Consent init called after auth resolves
//   - 5 new user routes: EMI, Notifications, Consent
//   - Admin routes under /admin/* (nested, guarded by AdminLayout)
// ================================================================

import { BrowserRouter, Routes, Route, Navigate, useNavigate } from 'react-router-dom';
import { useEffect, lazy, Suspense } from 'react';

import ProtectedRoute  from '../components/common/ProtectedRoute';
import AppLayout from '../components/common/AppLayout';
import ConsentGate     from '../components/common/ConsentGate';
import useAuthStore    from '../store/authStore';
import useConsentStore from '../store/consentStore';
import useNotificationStore from '../store/notificationStore';
import { ROUTES }      from '../utils/constants';

// ── Public pages — eager ─────────────────────────────────────────
import LoginPage              from '../pages/LoginPage';
import OAuthCallbackPage      from '../pages/OAuthCallbackPage';
import AccountDeactivatedPage from '../pages/AccountDeactivatedPage';

// ── Protected pages — lazy ───────────────────────────────────────
const DashboardPage        = lazy(() => import('../pages/DashboardPage'));
const UserProfilePage      = lazy(() => import('../pages/UserProfilePage'));
const FinancialProfilePage = lazy(() => import('../pages/FinancialProfilePage'));
const LoanEligibilityPage  = lazy(() => import('../pages/LoanEligibilityPage'));
const CreditCardPage       = lazy(() => import('../pages/CreditCardPage'));
const FraudCheckPage       = lazy(() => import('../pages/FraudCheckPage'));
const EmiTrackerPage       = lazy(() => import('../pages/EmiTrackerPage'));
const NotificationsPage    = lazy(() => import('../pages/NotificationsPage'));
const ConsentPage          = lazy(() => import('../pages/ConsentPage'));

// ── Admin pages — lazy ───────────────────────────────────────────
const AdminLayout         = lazy(() => import('../pages/admin/AdminLayout'));
const AdminDashboardPage  = lazy(() => import('../pages/admin/AdminDashboardPage'));
const AdminLendersPage    = lazy(() => import('../pages/admin/AdminLendersPage'));
const AdminCardsPage      = lazy(() => import('../pages/admin/AdminCardsPage'));
const AdminFraudPage      = lazy(() => import('../pages/admin/AdminFraudPage'));

// ── Full-screen spinner ──────────────────────────────────────────
const PageLoader = () => (
  <div className="page-loader">
    <div className="spinner spinner-lg" />
  </div>
);

// ── Deactivation listener ────────────────────────────────────────
const DeactivationListener = () => {
  const navigate = useNavigate();
  useEffect(() => {
    const handler = () => navigate(ROUTES.ACCOUNT_DEACTIVATED, { replace: true });
    window.addEventListener('fintrix:account-deactivated', handler);
    return () => window.removeEventListener('fintrix:account-deactivated', handler);
  }, [navigate]);
  return null;
};

// ── ProtectedLayout: auth + app shell + consent gate ────────────
const ProtectedLayout = ({ children }: { children: React.ReactNode }) => (
  <ProtectedRoute>
    <AppLayout>
      <ConsentGate>
        {children}
      </ConsentGate>
    </AppLayout>
  </ProtectedRoute>
);

// ── App boot: init auth, consent, notification polling ───────────
const AppBoot = () => {
  const initAuth          = useAuthStore(s => s.initAuth);
  const isAuthenticated   = useAuthStore(s => s.isAuthenticated);
  const initConsent       = useConsentStore(s => s.initConsent);
  const startPolling      = useNotificationStore(s => s.startPolling);

  useEffect(() => {
    initAuth();
  }, [initAuth]);

  // Once authenticated, init consent check + start notification polling
  useEffect(() => {
    if (isAuthenticated) {
      initConsent();
      const stopPolling = startPolling();
      return stopPolling;
    }
  }, [isAuthenticated, initConsent, startPolling]);

  return null;
};

const AppRouter = () => (
  <BrowserRouter>
    <AppBoot />
    <DeactivationListener />
    <Suspense fallback={<PageLoader />}>
      <Routes>

        {/* ── Public routes ──────────────────────────────────── */}
        <Route path={ROUTES.LOGIN}               element={<LoginPage />} />
        <Route path={ROUTES.OAUTH_CALLBACK}      element={<OAuthCallbackPage />} />
        <Route path={ROUTES.ACCOUNT_DEACTIVATED} element={<AccountDeactivatedPage />} />

        {/* ── Root redirect ───────────────────────────────────── */}
        <Route path="/" element={<Navigate to={ROUTES.DASHBOARD} replace />} />

        {/* ── Protected user routes ───────────────────────────── */}
        <Route path={ROUTES.DASHBOARD}
          element={<ProtectedLayout><DashboardPage /></ProtectedLayout>} />
        <Route path={ROUTES.USER_PROFILE}
          element={<ProtectedLayout><UserProfilePage /></ProtectedLayout>} />
        <Route path={ROUTES.FINANCIAL_PROFILE}
          element={<ProtectedLayout><FinancialProfilePage /></ProtectedLayout>} />
        <Route path={ROUTES.LOAN}
          element={<ProtectedLayout><LoanEligibilityPage /></ProtectedLayout>} />
        <Route path={ROUTES.CREDIT_CARD}
          element={<ProtectedLayout><CreditCardPage /></ProtectedLayout>} />
        <Route path={ROUTES.FRAUD_CHECK}
          element={<ProtectedLayout><FraudCheckPage /></ProtectedLayout>} />
        <Route path={ROUTES.EMI_TRACKER}
          element={<ProtectedLayout><EmiTrackerPage /></ProtectedLayout>} />
        <Route path={ROUTES.NOTIFICATIONS}
          element={<ProtectedLayout><NotificationsPage /></ProtectedLayout>} />
        <Route path={ROUTES.CONSENT}
          element={<ProtectedLayout><ConsentPage /></ProtectedLayout>} />

        {/* ── Admin routes (nested, AdminLayout adds role guard + sub-nav) */}
        <Route
          path={ROUTES.ADMIN}
          element={
            <ProtectedRoute>
              <AppLayout>
                <AdminLayout />
              </AppLayout>
            </ProtectedRoute>
          }
        >
          <Route index element={<Navigate to={ROUTES.ADMIN_DASHBOARD} replace />} />
          <Route path="dashboard" element={<AdminDashboardPage />} />
          <Route path="lenders"   element={<AdminLendersPage />} />
          <Route path="cards"     element={<AdminCardsPage />} />
          <Route path="fraud"     element={<AdminFraudPage />} />
        </Route>

        {/* ── 404 fallback ────────────────────────────────────── */}
        <Route path="*" element={<Navigate to={ROUTES.DASHBOARD} replace />} />

      </Routes>
    </Suspense>
  </BrowserRouter>
);

export default AppRouter;