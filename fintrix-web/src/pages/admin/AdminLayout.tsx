// ================================================================
// AdminLayout.tsx
// Wraps all /admin/* pages.
// Guards: if user.role !== 'ADMIN' → redirect to /dashboard.
// Adds a secondary tab-bar for admin sub-sections.
// ================================================================

import { useEffect }    from 'react';
import { NavLink, useNavigate, Outlet } from 'react-router-dom';
import useAuthStore     from '../../store/authStore';
import { ROUTES }       from '../../utils/constants';
import './AdminLayout.css';

const ADMIN_TABS = [
  { label: 'Dashboard', route: ROUTES.ADMIN_DASHBOARD },
  { label: 'Lenders',   route: ROUTES.ADMIN_LENDERS   },
  { label: 'Cards',     route: ROUTES.ADMIN_CARDS      },
  { label: 'Fraud',     route: ROUTES.ADMIN_FRAUD      },
];

const AdminLayout = () => {
  const navigate = useNavigate();
  const user     = useAuthStore(s => s.user);

  // Redirect non-admins immediately
  useEffect(() => {
    if (user && user.role !== 'ADMIN') {
      navigate(ROUTES.DASHBOARD, { replace: true });
    }
  }, [user, navigate]);

  if (!user || user.role !== 'ADMIN') return null;

  return (
    <>
      <nav className="admin-layout__topbar">
        <span className="admin-layout__badge">ADMIN</span>
        {ADMIN_TABS.map(tab => (
          <NavLink
            key={tab.route}
            to={tab.route}
            className={({ isActive }) =>
              `admin-layout__tab${isActive ? ' admin-layout__tab--active' : ''}`
            }
          >
            {tab.label}
          </NavLink>
        ))}
      </nav>
      <div className="admin-layout__content">
        <Outlet />
      </div>
    </>
  );
};

export default AdminLayout;