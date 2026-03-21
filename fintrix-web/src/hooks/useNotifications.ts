// ================================================================
// useNotifications.ts
// Starts unread-count polling on mount, cleans up on unmount.
// ================================================================

import { useEffect } from 'react';
import useNotificationStore from '../store/notificationStore';

const useNotifications = () => {
  const store = useNotificationStore();

  useEffect(() => {
    const cleanup = store.startPolling();
    return cleanup;
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return store;
};

export default useNotifications;