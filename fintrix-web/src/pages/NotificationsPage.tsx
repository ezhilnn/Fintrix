// ================================================================
// NotificationsPage.tsx  —  /notifications  (protected)
// GET  /api/v1/notifications          (paginated, page=0..n)
// GET  /api/v1/notifications/unread-count
// PUT  /api/v1/notifications/read-all
// ================================================================

import { useEffect } from 'react';
import { useNavigate }              from 'react-router-dom';
import useNotificationStore         from '../store/notificationStore';
import { formatDate }               from '../utils/formatters';
import { NOTIFICATION_TYPE_LABELS } from '../utils/constants';
import type { NotificationResponse } from '../types/api.types';
import './NotificationsPage.css';

// ── Notification type → icon ───────────────────────────────────
const typeIcon = (type: string) => ({
  EMI_REMINDER: '💳',
  SCORE_UPDATE: '📊',
  FRAUD_ALERT:  '🚨',
  OFFER:        '🎁',
}[type] ?? '🔔');

// Parse payload JSON: { route: "/loans", entityId: "xxx" }
const parsePayload = (raw?: string): { route?: string; entityId?: string } => {
  if (!raw) return {};
  try { return JSON.parse(raw); } catch { return {}; }
};

// ── Single notification item ───────────────────────────────────
const NotifItem = ({
  item,
  onClick,
}: {
  item: NotificationResponse;
  onClick?: () => void;
}) => (
  <div
    className={`notif-page__item${!item.isRead ? ' notif-page__item--unread' : ''}${onClick ? ' notif-page__item--clickable' : ''}`}
    onClick={onClick}
    role={onClick ? 'button' : undefined}
    tabIndex={onClick ? 0 : undefined}
    onKeyDown={onClick ? e => e.key === 'Enter' && onClick() : undefined}
  >
    <span className="notif-page__item-icon">{typeIcon(item.notificationType)}</span>
    <div className="notif-page__item-body">
      <p className="notif-page__item-title">{item.title}</p>
      <p className="notif-page__item-body-text">{item.body}</p>
      <div className="notif-page__item-meta">
        <span className="notif-page__item-time">
          {formatDate(item.createdAt)}
        </span>
        <span className="badge badge-info" style={{ fontSize: 'var(--text-xs)' }}>
          {NOTIFICATION_TYPE_LABELS[item.notificationType] ?? item.notificationType}
        </span>
        {!item.isRead && (
          <span className="badge badge-brand" style={{ fontSize: 'var(--text-xs)' }}>
            New
          </span>
        )}
        {/* Show deep-link indicator if payload has a route */}
        {parsePayload(item.payload).route && (
          <span style={{ fontSize: 'var(--text-xs)', color: 'var(--color-brand-primary)' }}>
            View →
          </span>
        )}
      </div>
    </div>
  </div>
);

// ── Page ───────────────────────────────────────────────────────
const NotificationsPage = () => {
  const navigate = useNavigate();
  const {
    notifications, unreadCount, isLoading, isLoadingMore,
    currentPage, totalPages,
    fetchNotifications, loadMore, markAllRead,
  } = useNotificationStore();

  useEffect(() => {
    // Only fetch if store is empty
    if (notifications.length === 0) {
      fetchNotifications();
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleNotifClick = (item: NotificationResponse) => {
    const { route } = parsePayload(item.payload);
    if (route) navigate(route);
  };

  return (
    <div className="notif-page">

      <div className="notif-page__header">
        <div className="notif-page__title-wrap">
          <p className="notif-page__eyebrow">Inbox</p>
          <h1 className="notif-page__title">Notifications</h1>
        </div>
        {unreadCount > 0 && (
          <button className="btn btn-secondary btn-sm" onClick={markAllRead}>
            ✓ Mark all as read ({unreadCount})
          </button>
        )}
      </div>

      {isLoading && (
        <div style={{ display: 'flex', justifyContent: 'center', padding: 'var(--space-16)' }}>
          <div className="spinner spinner-lg" />
        </div>
      )}

      {!isLoading && notifications.length === 0 && (
        <div className="notif-page__empty">
          <span className="notif-page__empty-icon">🔔</span>
          <h3 className="notif-page__empty-title">No notifications yet</h3>
        </div>
      )}

      {!isLoading && notifications.length > 0 && (
        <>
          <div className="notif-page__list">
            {notifications.map(n => (
              <NotifItem
                key={n.id}
                item={n}
                onClick={parsePayload(n.payload).route ? () => handleNotifClick(n) : undefined}
              />
            ))}
          </div>

          {currentPage + 1 < totalPages && (
            <div className="notif-page__load-more">
              <button
                className="btn btn-secondary"
                onClick={loadMore}
                disabled={isLoadingMore}
              >
                {isLoadingMore
                  ? <><span className="spinner" /> Loading…</>
                  : 'Load more'}
              </button>
            </div>
          )}
        </>
      )}

    </div>
  );
};

export default NotificationsPage;