// ================================================================
// Topbar.tsx  — v2 update
// Added: NotificationBell with unread badge (right of title)
//        New page titles: EMI Tracker, Notifications, Consent, Admin pages
// ================================================================

import { useLocation }     from 'react-router-dom';
import useAuthStore        from '../../store/authStore';
import NotificationBell    from '../common/NotificationBell';
import { ROUTES }          from '../../utils/constants';
import './Topbar.css';

interface TopbarProps {
  onMenuClick: () => void;
}

const PAGE_TITLES: Record<string, string> = {
  [ROUTES.DASHBOARD]:         'Dashboard',
  [ROUTES.LOAN]:              'Loan Eligibility',
  [ROUTES.CREDIT_CARD]:       'Credit Cards',
  [ROUTES.FRAUD_CHECK]:       'Fraud Check',
  [ROUTES.EMI_TRACKER]:       'EMI Tracker',
  [ROUTES.NOTIFICATIONS]:     'Notifications',
  [ROUTES.CONSENT]:           'Consent Settings',
  [ROUTES.USER_PROFILE]:      'Your Profile',
  [ROUTES.FINANCIAL_PROFILE]: 'Financial Profile',
  [ROUTES.ADMIN_DASHBOARD]:   'Admin',
  [ROUTES.ADMIN_LENDERS]:     'Admin — Lenders',
  [ROUTES.ADMIN_CARDS]:       'Admin — Cards',
  [ROUTES.ADMIN_FRAUD]:       'Admin — Fraud',
};

const Topbar = ({ onMenuClick }: TopbarProps) => {
  const { user }     = useAuthStore();
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

      {/* Notification bell with unread badge */}
      <NotificationBell />

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