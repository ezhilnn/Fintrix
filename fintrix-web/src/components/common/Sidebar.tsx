// ================================================================
// Sidebar.tsx
// Fixed left navigation sidebar for all protected pages.
// Uses NavLink for automatic active state detection.
// ================================================================

import { NavLink, useNavigate } from 'react-router-dom';
import useAuthStore from '../../store/authStore';
import { ROUTES }   from '../../utils/constants';
import './Sidebar.css';

interface SidebarProps {
  isOpen:    boolean;
  onClose:   () => void;
}

const NAV_ITEMS = [
  { icon: '📊', label: 'Dashboard',          route: ROUTES.DASHBOARD },
  { icon: '🏦', label: 'Loan Eligibility',   route: ROUTES.LOAN },
  { icon: '💳', label: 'Credit Cards',       route: ROUTES.CREDIT_CARD },
  { icon: '🔍', label: 'Fraud Check',        route: ROUTES.FRAUD_CHECK },
];

const SETTINGS_ITEMS = [
  { icon: '👤', label: 'Your Profile',       route: ROUTES.USER_PROFILE },
  { icon: '💰', label: 'Financial Profile',  route: ROUTES.FINANCIAL_PROFILE },
];

const Sidebar = ({ isOpen, onClose }: SidebarProps) => {
  const navigate = useNavigate();
  const { user, logout } = useAuthStore();

  const handleLogout = () => {
    logout();
    navigate(ROUTES.LOGIN, { replace: true });
  };

  // Initials fallback for avatar
  const initials = user?.fullName
    ? user.fullName.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2)
    : '?';

  return (
    <>
      {/* Backdrop — mobile only, closes sidebar on tap */}
      {isOpen && (
        <div className="sidebar__backdrop" onClick={onClose} />
      )}

      <aside className={`sidebar${isOpen ? ' sidebar--open' : ''}`}>

        {/* ── Wordmark ───────────────────────────── */}
        <NavLink to={ROUTES.DASHBOARD} className="sidebar__logo" onClick={onClose}>
          <div>
            <div className="sidebar__wordmark">Fintrix<span>.</span></div>
            <div className="sidebar__tagline">Finance Assistant</div>
          </div>
        </NavLink>

        {/* ── Main nav ───────────────────────────── */}
        <nav className="sidebar__nav">

          <span className="sidebar__section-label">Tools</span>

          {NAV_ITEMS.map(({ icon, label, route }) => (
            <NavLink
              key={route}
              to={route}
              className={({ isActive }) =>
                `sidebar__item${isActive ? ' sidebar__item--active' : ''}`
              }
              onClick={onClose}
            >
              <span className="sidebar__item-icon">{icon}</span>
              <span className="sidebar__item-label">{label}</span>
            </NavLink>
          ))}

          <div className="sidebar__divider" />
          <span className="sidebar__section-label">Account</span>

          {SETTINGS_ITEMS.map(({ icon, label, route }) => (
            <NavLink
              key={route}
              to={route}
              className={({ isActive }) =>
                `sidebar__item${isActive ? ' sidebar__item--active' : ''}`
              }
              onClick={onClose}
            >
              <span className="sidebar__item-icon">{icon}</span>
              <span className="sidebar__item-label">{label}</span>
            </NavLink>
          ))}

        </nav>

        {/* ── User footer ────────────────────────── */}
        <div className="sidebar__footer">

          <NavLink
            to={ROUTES.USER_PROFILE}
            className="sidebar__user"
            onClick={onClose}
          >
            {user?.profilePictureUrl ? (
              <img
                className="sidebar__user-avatar"
                src={user.profilePictureUrl}
                alt={user.fullName ?? 'Profile'}
              />
            ) : (
              <div className="sidebar__user-avatar-fallback">{initials}</div>
            )}
            <div className="sidebar__user-info">
              <p className="sidebar__user-name">{user?.fullName ?? 'Your Profile'}</p>
              <p className="sidebar__user-email">{user?.email ?? ''}</p>
            </div>
          </NavLink>

          <button className="sidebar__logout" onClick={handleLogout}>
            <span className="sidebar__logout-icon">↩</span>
            Sign out
          </button>

        </div>

      </aside>
    </>
  );
};

export default Sidebar;