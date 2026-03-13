// ================================================================
// OAuthCallbackPage.tsx  —  /oauth2/callback  (public route)
// Handles redirect from OAuth2AuthenticationSuccessHandler.java:
//   http://localhost:5173/oauth2/callback?token=eyJhbGci...
// ================================================================

import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthService   from '../services/auth.service';
import useAuthStore  from '../store/authStore';
import { ROUTES }    from '../utils/constants';
import './OAuthCallbackPage.css';

const OAuthCallbackPage = () => {
  const navigate           = useNavigate();
  const handleOAuthSuccess = useAuthStore(s => s.handleOAuthSuccess);
  const isLoading          = useAuthStore(s => s.isLoading);
  const user               = useAuthStore(s => s.user);
  const error              = useAuthStore(s => s.error);

  // Step 1 — extract ?token= and store it
  useEffect(() => {
    const token = AuthService.extractTokenFromUrl();
    if (!token) {
      navigate(ROUTES.LOGIN, { replace: true });
      return;
    }
    handleOAuthSuccess(token);
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // Step 2 — once user is in store, redirect appropriately
  useEffect(() => {
    if (!isLoading && user) {
      if (user.isProfileComplete) {
        navigate(ROUTES.DASHBOARD, { replace: true });
      } else {
        navigate(ROUTES.USER_PROFILE, { replace: true });
      }
    }
  }, [isLoading, user, navigate]);

  return (
    <div className="oauth-callback">

      {error ? (
        /* ── Error state ─────────────────────────────────── */
        <div className="oauth-callback__error">
          <span className="oauth-callback__error-icon">✕</span>
          <h2 className="oauth-callback__error-title">Login failed</h2>
          <p className="oauth-callback__error-msg">{error}</p>
          <button
            className="btn btn-primary"
            onClick={() => navigate(ROUTES.LOGIN)}
          >
            Back to Login
          </button>
        </div>
      ) : (
        /* ── Loading state ───────────────────────────────── */
        <div className="oauth-callback__loading">
          <div className="oauth-callback__spinner-wrap">
            <div className="oauth-callback__spinner-track" />
            <div className="oauth-callback__spinner-fill" />
          </div>
          <div className="oauth-callback__label">
            <p className="oauth-callback__label-primary">Signing you in…</p>
            <p className="oauth-callback__label-secondary">
              Setting up your Fintrix account
            </p>
          </div>
        </div>
      )}

    </div>
  );
};

export default OAuthCallbackPage;