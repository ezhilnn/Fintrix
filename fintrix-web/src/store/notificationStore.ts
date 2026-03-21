// ================================================================
// notificationStore.ts
//
// State:  notifications[], unreadCount, currentPage, hasMore
// Actions: fetchNotifications(), loadMore(), markAllRead(), pollUnreadCount()
//
// unreadCount is polled every 60s while app is open (via interval).
// The Topbar NotificationBell reads unreadCount directly.
// ================================================================

import { create } from 'zustand';
import NotificationService from '../services/notification.service';
import type { NotificationResponse } from '../types/api.types';
import type { ApiError } from '../types/api.types';

interface NotificationState {
  notifications:  NotificationResponse[];
  unreadCount:    number;
  currentPage:    number;
  totalPages:     number;
  isLoading:      boolean;
  isLoadingMore:  boolean;
  error:          string | null;

  fetchNotifications:  () => Promise<void>;
  loadMore:            () => Promise<void>;
  markAllRead:         () => Promise<void>;
  fetchUnreadCount:    () => Promise<void>;
  startPolling:        () => () => void;  // returns cleanup fn
}

const useNotificationStore = create<NotificationState>((set, get) => ({
  notifications: [],
  unreadCount:   0,
  currentPage:   0,
  totalPages:    1,
  isLoading:     false,
  isLoadingMore: false,
  error:         null,

  fetchNotifications: async () => {
    set({ isLoading: true, error: null });
    try {
      const paged = await NotificationService.getNotifications(0);
      set({
        notifications: paged.content,
        currentPage:   paged.number,
        totalPages:    paged.totalPages,
        isLoading:     false,
      });
    } catch (err) {
      set({ isLoading: false, error: (err as ApiError).message });
    }
  },

  loadMore: async () => {
    const { currentPage, totalPages, isLoadingMore } = get();
    if (isLoadingMore || currentPage + 1 >= totalPages) return;
    set({ isLoadingMore: true });
    try {
      const paged = await NotificationService.getNotifications(currentPage + 1);
      set(s => ({
        notifications: [...s.notifications, ...paged.content],
        currentPage:   paged.number,
        totalPages:    paged.totalPages,
        isLoadingMore: false,
      }));
    } catch {
      set({ isLoadingMore: false });
    }
  },

  markAllRead: async () => {
    try {
      await NotificationService.markAllRead();
      set(s => ({
        unreadCount:   0,
        notifications: s.notifications.map(n => ({ ...n, isRead: true })),
      }));
    } catch (err) {
      set({ error: (err as ApiError).message });
    }
  },

  fetchUnreadCount: async () => {
    try {
      const count = await NotificationService.getUnreadCount();
      set({ unreadCount: count });
    } catch {
      /* silent — badge failure should not break UI */
    }
  },

  // Call once on app mount. Returns cleanup fn for useEffect.
  startPolling: () => {
    get().fetchUnreadCount();
    const id = window.setInterval(() => get().fetchUnreadCount(), 60_000);
    return () => window.clearInterval(id);
  },
}));

export default useNotificationStore;