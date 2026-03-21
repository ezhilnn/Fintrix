// ================================================================
// emiStore.ts
//
// State:  emis[], isLoading, isAdding, error, fieldErrors
// Actions: fetchEmis(), addEmi(), deleteEmi()
// ================================================================

import { create } from 'zustand';
import EmiService  from '../services/emi.service';
import type { EmiTrackerRequest, EmiTrackerResponse } from '../types/api.types';
import type { ApiError } from '../types/api.types';

interface EmiState {
  emis:        EmiTrackerResponse[];
  isLoading:   boolean;
  isAdding:    boolean;
  error:       string | null;
  fieldErrors: Record<string, string> | null;

  fetchEmis: () => Promise<void>;
  addEmi:    (req: EmiTrackerRequest, onSuccess?: () => void) => Promise<void>;
  deleteEmi: (id: string) => Promise<void>;
  clearErrors: () => void;
}

const useEmiStore = create<EmiState>((set, get) => ({
  emis:        [],
  isLoading:   false,
  isAdding:    false,
  error:       null,
  fieldErrors: null,

  fetchEmis: async () => {
    set({ isLoading: true, error: null });
    try {
      const emis = await EmiService.getMyEmis();
      set({ emis, isLoading: false });
    } catch (err) {
      set({ isLoading: false, error: (err as ApiError).message });
    }
  },

  addEmi: async (req, onSuccess) => {
    set({ isAdding: true, error: null, fieldErrors: null });
    try {
      const created = await EmiService.addEmi(req);
      // Prepend new EMI to list
      set({ emis: [created, ...get().emis], isAdding: false });
      onSuccess?.();
    } catch (err) {
      const apiErr = err as ApiError;
      set({
        isAdding:    false,
        error:       apiErr.message,
        fieldErrors: apiErr.errors ?? null,
      });
    }
  },

  deleteEmi: async (id) => {
    try {
      await EmiService.deleteEmi(id);
      set({ emis: get().emis.filter(e => e.id !== id) });
    } catch (err) {
      set({ error: (err as ApiError).message });
    }
  },

  clearErrors: () => set({ error: null, fieldErrors: null }),
}));

export default useEmiStore;