// ================================================================
// Topbar.tsx
// Mobile-only top bar (hidden on desktop — sidebar handles nav).
// Shows hamburger button + Fintrix wordmark + user avatar.
// ================================================================

import { useLocation } from 'react-router-dom';
import useAuthStore    from '../../store/authStore';
import { ROUTES }      from '../../utils/constants';
import './Topbar.css';

interface TopbarProps {
  onMenuClick: () => void;
}

// Derive page title from current route
const PAGE_TITLES: Record<string, string> = {
  [ROUTES.DASHBOARD]:         'Dashboard',
  [ROUTES.LOAN]:              'Loan Eligibility',
  [ROUTES.CREDIT_CARD]:       'Credit Cards',
  [ROUTES.FRAUD_CHECK]:       'Fraud Check',
  [ROUTES.USER_PROFILE]:      'Your Profile',
  [ROUTES.FINANCIAL_PROFILE]: 'Financial Profile',
};

const Topbar = ({ onMenuClick }: TopbarProps) => {
  const { user }   = useAuthStore();
  const { pathname } = useLocation();

  const pageTitle = PAGE_TITLES[pathname] ?? 'Fintrix';

  const initials = user?.fullName
    ? user.fullName.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2)
    : '?';

  return (
    <header className="topbar">

      {/* Hamburger */}
      <button
        className="topbar__menu-btn"
        onClick={onMenuClick}
        aria-label="Open navigation"
      >
        <span className="topbar__bar" />
        <span className="topbar__bar" />
        <span className="topbar__bar" />
      </button>

      {/* Page title */}
      <span className="topbar__title">{pageTitle}</span>

      {/* User avatar */}
      {user?.profilePictureUrl ? (
        <img
          className="topbar__avatar"
          src={user.profilePictureUrl}
          alt={user.fullName ?? 'Profile'}
        />
      ) : (
        <div className="topbar__avatar-fallback">{initials}</div>
      )}

    </header>
  );
};

export default Topbar;