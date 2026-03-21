// ================================================================
// notification.service.ts
//
// POST /api/v1/notifications/token    → register FCM device token
// GET  /api/v1/notifications          → history (paginated, page=0..n)
// GET  /api/v1/notifications/unread-count → badge number
// PUT  /api/v1/notifications/read-all → mark all as read
// ================================================================

import { get, post, put } from './api.client';
import { API } from '../utils/constants';
import type {
  NotificationResponse,
  RegisterTokenRequest,
  PagedNotifications,
} from '../types/api.types';

const NotificationService = {

  registerToken(request: RegisterTokenRequest): Promise<void> {
    return post<void>(API.NOTIFICATIONS_TOKEN, request);
  },

  getNotifications(page = 0): Promise<PagedNotifications> {
    return get<PagedNotifications>(API.NOTIFICATIONS, { page });
  },

  getUnreadCount(): Promise<number> {
    return get<number>(API.NOTIFICATIONS_UNREAD);
  },

  markAllRead(): Promise<void> {
    return put<void>(API.NOTIFICATIONS_READ_ALL);
  },
};

export default NotificationService;