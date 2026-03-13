// ================================================================
// LoginPage.tsx  —  /login  (public route)
// Initiates Google OAuth2 flow via Spring Security
// ================================================================

import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import useAuthStore from '../store/authStore';
import AuthService  from '../services/auth.service';
import { ROUTES }   from '../utils/constants';
import './LoginPage.css';

const PILLS = [
  'Loan Eligibility Check',
  'Credit Card Match',
  'CIBIL Score Guide',
  'Fraud Detection',
  'Financial Health Score',
];

const TRUST = [
  {
    icon: '🔒',
    text: 'Your financial data stays private. We never share or sell your information.',
  },
  {
    icon: '📋',
    text: 'No credit report access. We use only what you tell us — no hard enquiries.',
  },
  {
    icon: '🇮🇳',
    text: 'Built for India. Guidance based on RBI & SEBI regulations.',
  },
];

const LoginPage = () => {
  const isAuthenticated = useAuthStore(s => s.isAuthenticated);
  const isLoading       = useAuthStore(s => s.isLoading);
  const navigate        = useNavigate();

  // Already logged in → skip to dashboard
  useEffect(() => {
    if (!isLoading && isAuthenticated) {
      navigate(ROUTES.DASHBOARD, { replace: true });
    }
  }, [isAuthenticated, isLoading, navigate]);

  return (
    <div className="login-page">
      <div className="login-page__glow-tr" />
      <div className="login-page__glow-bl" />

      {/* ── Left branding panel ──────────────────────────── */}
      <div className="login-page__left">
        <div className="login-page__wordmark">
          Fintrix<span>.</span>
        </div>

        <h1 className="login-page__headline">
          Make smarter<br />
          <em>financial decisions.</em>
        </h1>

        <p className="login-page__subtext">
          India's intelligent finance assistant — know your loan eligibility,
          improve your CIBIL score, and protect yourself from fraud before you apply.
        </p>

        <div className="login-page__pills">
          {PILLS.map(label => (
            <span className="login-page__pill" key={label}>
              <span className="login-page__pill-dot" />
              {label}
            </span>
          ))}
        </div>
      </div>

      <div className="login-page__divider" />

      {/* ── Right login card ─────────────────────────────── */}
      <div className="login-page__right">
        <div className="login-page__card">

          <p className="login-page__eyebrow">Secure Login</p>

          <h2 className="login-page__card-title">Welcome back</h2>

          <p className="login-page__card-sub">
            Sign in to access your personalised financial dashboard.
          </p>

          <button
            className="login-page__google-btn"
            onClick={() => AuthService.initiateGoogleLogin()}
          >
            <svg
              className="login-page__google-icon"
              viewBox="0 0 24 24"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4"/>
              <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853"/>
              <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l3.66-2.84z" fill="#FBBC05"/>
              <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335"/>
            </svg>
            Continue with Google
          </button>

          <div className="login-page__separator">No password required</div>

          <div className="login-page__trust">
            {TRUST.map(({ icon, text }) => (
              <div className="login-page__trust-item" key={text}>
                <span className="login-page__trust-icon">{icon}</span>
                <span>{text}</span>
              </div>
            ))}
          </div>

          <p className="login-page__footer-note">
            By signing in you agree to our Terms of Service.<br />
            Fintrix provides financial education — not regulated advice.
          </p>

        </div>
      </div>
    </div>
  );
};

export default LoginPage;