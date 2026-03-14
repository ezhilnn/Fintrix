// ================================================================
// AccountDeactivatedPage.tsx  —  /account-deactivated  (public)
// Shown after DELETE /api/v1/users/me completes.
// User is already logged out at this point (authStore.deactivateAccount
// calls logout() before navigating here).
// ================================================================

import { useNavigate } from 'react-router-dom';
import { ROUTES }      from '../utils/constants';
import './AccountDeactivatedPage.css';

const AccountDeactivatedPage = () => {
  const navigate = useNavigate();

  return (
    <div className="deactivated-page">
      <div className="deactivated-page__card">
        <span className="deactivated-page__icon">👋</span>

        <h1 className="deactivated-page__title">Account Deactivated</h1>

        <p className="deactivated-page__body">
          Your Fintrix account and all associated financial data have been
          removed. We're sorry to see you go.
        </p>

        <div className="deactivated-page__divider" />

        <p className="deactivated-page__note">
          If this was a mistake or you change your mind, you can create a
          new account at any time by signing in with Google.
        </p>

        <button
          className="btn btn-primary btn-full"
          onClick={() => navigate(ROUTES.LOGIN, { replace: true })}
        >
          Back to Login
        </button>
      </div>
    </div>
  );
};

export default AccountDeactivatedPage;