// ================================================================
// useEmiTracker.ts
// Thin hook over emiStore. Auto-fetches on mount.
// Exposes derived values: totalMonthlyEmi, dueSoonCount
// ================================================================

import { useEffect } from 'react';
import useEmiStore   from '../store/emiStore';

const useEmiTracker = () => {
  const store = useEmiStore();

  useEffect(() => {
    // Only fetch if store is empty
    if (store.emis.length === 0) {
      store.fetchEmis();
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const totalMonthlyEmi = store.emis.reduce(
    (sum, e) => sum + Number(e.emiAmount), 0
  );

  const dueSoonCount = store.emis.filter(e => e.isDueSoon).length;

  return {
    ...store,
    totalMonthlyEmi,
    dueSoonCount,
  };
};

export default useEmiTracker;