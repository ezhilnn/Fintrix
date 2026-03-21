// ================================================================
// NotificationBell.tsx
// Reads unreadCount from notificationStore (polled every 60s).
// Navigates to /notifications on click.
// ================================================================

import { useNavigate }        from 'react-router-dom';
import useNotificationStore   from '../../store/notificationStore';
import { ROUTES }             from '../../utils/constants';
import './NotificationBell.css';

const NotificationBell = () => {
  const navigate     = useNavigate();
  const unreadCount  = useNotificationStore(s => s.unreadCount);

  return (
    <button
      className="notif-bell"
      onClick={() => navigate(ROUTES.NOTIFICATIONS)}
      aria-label={`Notifications${unreadCount > 0 ? ` (${unreadCount} unread)` : ''}`}
    >
      <span className="notif-bell__icon">🔔</span>
      {unreadCount > 0 && (
        <span className="notif-bell__badge">
          {unreadCount > 99 ? '99+' : unreadCount}
        </span>
      )}
    </button>
  );
};

export default NotificationBell;