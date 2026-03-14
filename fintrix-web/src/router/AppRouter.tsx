// ================================================================
// AppRouter.tsx
//
// Public routes:  /login, /oauth2/callback
// Protected routes: wrapped in ProtectedRoute + AppLayout
//
// Onboarding routes (/profile, /financial-profile) get AppLayout
// but ProtectedRoute won't block them even if profile incomplete.
// ================================================================

import { BrowserRouter, Routes, Route, Navigate, useNavigate } from 'react-router-dom';
import { useEffect, lazy, Suspense } from 'react';

import ProtectedRoute from '../components/common/ProtectedRoute';
import AppLayout from '../components/common/AppLayout';
import useAuthStore   from '../store/authStore';
import { ROUTES }     from '../utils/constants';

// Public — eager (tiny, always needed immediately)
import LoginPage            from '../pages/LoginPage';
import OAuthCallbackPage    from '../pages/OAuthCallbackPage';
import AccountDeactivatedPage from '../pages/AccountDeactivatedPage';

// Protected — lazy loaded for code splitting
const DashboardPage        = lazy(() => import('../pages/DashboardPage'));
const UserProfilePage      = lazy(() => import('../pages/UserProfilePage'));
const FinancialProfilePage = lazy(() => import('../pages/FinancialProfilePage'));
const LoanEligibilityPage  = lazy(() => import('../pages/LoanEligibilityPage'));
const CreditCardPage       = lazy(() => import('../pages/CreditCardPage'));
const FraudCheckPage       = lazy(() => import('../pages/FraudCheckPage'));

// Full-screen spinner while lazy chunks load
const PageLoader = () => (
  <div className="page-loader">
    <div className="spinner spinner-lg" />
  </div>
);

// Convenience: ProtectedRoute + AppLayout in one wrapper
const ProtectedLayout = ({ children }: { children: React.ReactNode }) => (
  <ProtectedRoute>
    <AppLayout>{children}</AppLayout>
  </ProtectedRoute>
);

// Listens for deactivation event fired by userStore and redirects
// Must be rendered INSIDE BrowserRouter to use useNavigate
const DeactivationListener = () => {
  const navigate = useNavigate();
  useEffect(() => {
    const handler = () => navigate(ROUTES.ACCOUNT_DEACTIVATED, { replace: true });
    window.addEventListener('fintrix:account-deactivated', handler);
    return () => window.removeEventListener('fintrix:account-deactivated', handler);
  }, [navigate]);
  return null;
};

const AppRouter = () => {
  const initAuth = useAuthStore(s => s.initAuth);

  useEffect(() => {
    initAuth();
  }, [initAuth]);

  return (
    <BrowserRouter>
      <DeactivationListener />
      <Suspense fallback={<PageLoader />}>
        <Routes>

          {/* ── Public routes ────────────────────────────────── */}
          <Route path={ROUTES.LOGIN}                 element={<LoginPage />} />
          <Route path={ROUTES.OAUTH_CALLBACK}        element={<OAuthCallbackPage />} />
          <Route path={ROUTES.ACCOUNT_DEACTIVATED}   element={<AccountDeactivatedPage />} />

          {/* ── Root redirect ─────────────────────────────────── */}
          <Route path="/" element={<Navigate to={ROUTES.DASHBOARD} replace />} />

          {/* ── Protected + Layout routes ─────────────────────── */}
          <Route
            path={ROUTES.DASHBOARD}
            element={<ProtectedLayout><DashboardPage /></ProtectedLayout>}
          />
          <Route
            path={ROUTES.USER_PROFILE}
            element={<ProtectedLayout><UserProfilePage /></ProtectedLayout>}
          />
          <Route
            path={ROUTES.FINANCIAL_PROFILE}
            element={<ProtectedLayout><FinancialProfilePage /></ProtectedLayout>}
          />
          <Route
            path={ROUTES.LOAN}
            element={<ProtectedLayout><LoanEligibilityPage /></ProtectedLayout>}
          />
          <Route
            path={ROUTES.CREDIT_CARD}
            element={<ProtectedLayout><CreditCardPage /></ProtectedLayout>}
          />
          <Route
            path={ROUTES.FRAUD_CHECK}
            element={<ProtectedLayout><FraudCheckPage /></ProtectedLayout>}
          />

          {/* ── 404 fallback ──────────────────────────────────── */}
          <Route path="*" element={<Navigate to={ROUTES.DASHBOARD} replace />} />

        </Routes>
      </Suspense>
    </BrowserRouter>
  );
};

export default AppRouter;