// ================================================================
// useFinancialProfile.ts
//
// Hook for FinancialProfilePage — wraps financialProfileStore.
// Components use this instead of importing the store directly.
// ================================================================

import { useEffect } from 'react';
import useFinancialProfileStore from '../store/financialProfileStore';
import type { FinancialProfileRequest } from '../types/financialProfile.types';

const useFinancialProfile = () => {
  const {
    profile,
    isFetching,
    isSaving,
    fetchError,
    saveError,
    fieldErrors,
    fetchProfile,
    saveProfile,
    clearErrors,
  } = useFinancialProfileStore();

  // Auto-fetch on mount if not already loaded
  useEffect(() => {
    if (!profile && !isFetching) {
      fetchProfile();
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return {
    profile,
    isFetching,
    isSaving,
    fetchError,
    saveError,
    fieldErrors,
    // Computed helper — has the user created a profile yet?
    hasProfile: !!profile,
    isComplete: profile?.isComplete ?? false,
    saveProfile: (
      req: FinancialProfileRequest,
      onSuccess?: () => void,
    ) => saveProfile(req, onSuccess),
    refetch:     fetchProfile,
    clearErrors,
  };
};

export default useFinancialProfile;