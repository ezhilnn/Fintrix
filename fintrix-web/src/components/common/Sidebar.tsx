// ================================================================
// Sidebar.tsx  — v2 update
// Added: EMI Tracker, Notifications nav items
//        Consent Settings under Account section
//        Admin section shown only if role = ADMIN
//        NotificationBell in footer
// ================================================================

import { NavLink, useNavigate } from 'react-router-dom';
import useAuthStore          from '../../store/authStore';
import useNotificationStore  from '../../store/notificationStore';
import { ROUTES }            from '../../utils/constants';
import './Sidebar.css';

interface SidebarProps {
  isOpen:  boolean;
  onClose: () => void;
}

const NAV_ITEMS = [
  { icon: '📊', label: 'Dashboard',        route: ROUTES.DASHBOARD },
  { icon: '🏦', label: 'Loan Eligibility', route: ROUTES.LOAN },
  { icon: '💳', label: 'Credit Cards',     route: ROUTES.CREDIT_CARD },
  { icon: '🔍', label: 'Fraud Check',      route: ROUTES.FRAUD_CHECK },
  { icon: '📅', label: 'EMI Tracker',      route: ROUTES.EMI_TRACKER },
];

const ACCOUNT_ITEMS = [
  { icon: '👤', label: 'Your Profile',      route: ROUTES.USER_PROFILE },
  { icon: '💰', label: 'Financial Profile', route: ROUTES.FINANCIAL_PROFILE },
  { icon: '🔒', label: 'Consent Settings',  route: ROUTES.CONSENT },
];

const ADMIN_ITEMS = [
  { icon: '⚙️', label: 'Admin Dashboard', route: ROUTES.ADMIN_DASHBOARD },
  { icon: '🏛',  label: 'Lenders',         route: ROUTES.ADMIN_LENDERS },
  { icon: '🗂',  label: 'Cards',           route: ROUTES.ADMIN_CARDS },
  { icon: '🚨', label: 'Fraud Keywords',  route: ROUTES.ADMIN_FRAUD },
];

const Sidebar = ({ isOpen, onClose }: SidebarProps) => {
  const navigate      = useNavigate();
  const { user, logout } = useAuthStore();
  const unreadCount   = useNotificationStore(s => s.unreadCount);
  const isAdmin       = user?.role === 'ADMIN';

  const handleLogout = () => {
    logout();
    navigate(ROUTES.LOGIN, { replace: true });
  };

  const initials = user?.fullName
    ? user.fullName.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2)
    : '?';

  return (
    <>
      {isOpen && <div className="sidebar__backdrop" onClick={onClose} />}

      <aside className={`sidebar${isOpen ? ' sidebar--open' : ''}`}>

        {/* ── Wordmark ─────────────────────────── */}
        <NavLink to={ROUTES.DASHBOARD} className="sidebar__logo" onClick={onClose}>
          <div>
            <div className="sidebar__wordmark">Fintrix<span>.</span></div>
            <div className="sidebar__tagline">Finance Assistant</div>
          </div>
        </NavLink>

        {/* ── Nav ──────────────────────────────── */}
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

          {/* Notifications — with unread badge */}
          <NavLink
            to={ROUTES.NOTIFICATIONS}
            className={({ isActive }) =>
              `sidebar__item${isActive ? ' sidebar__item--active' : ''}`
            }
            onClick={onClose}
          >
            <span className="sidebar__item-icon">🔔</span>
            <span className="sidebar__item-label">Notifications</span>
            {unreadCount > 0 && (
              <span style={{
                marginLeft: 'auto',
                fontSize: 'var(--text-xs)',
                fontWeight: 'var(--font-semibold)',
                fontFamily: 'var(--font-mono)',
                padding: '1px 7px',
                borderRadius: 'var(--radius-full)',
                background: 'var(--color-danger)',
                color: '#fff',
              }}>
                {unreadCount > 99 ? '99+' : unreadCount}
              </span>
            )}
          </NavLink>

          <div className="sidebar__divider" />
          <span className="sidebar__section-label">Account</span>

          {ACCOUNT_ITEMS.map(({ icon, label, route }) => (
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

          {/* ── Admin section — ADMIN role only ─── */}
          {isAdmin && (
            <>
              <div className="sidebar__divider" />
              <span className="sidebar__section-label" style={{ color: 'var(--color-danger)' }}>
                Admin
              </span>
              {ADMIN_ITEMS.map(({ icon, label, route }) => (
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
            </>
          )}

        </nav>

        {/* ── User footer ──────────────────────── */}
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