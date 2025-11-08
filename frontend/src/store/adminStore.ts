import { create } from "zustand";
import { fetchAllAssets, deleteAsset } from "../utils/api";
import { Asset } from "../utils/api";

interface AdminState {
  assets: Asset[];
  loading: boolean;
  fetchAssets: () => Promise<void>;
  removeAsset: (id: string) => Promise<void>;
}

export const useAdminStore = create<AdminState>((set) => ({
  assets: [],
  loading: false,

  fetchAssets: async () => {
    set({ loading: true });
    try {
      const data = await fetchAllAssets();
      set({ assets: data });
    } catch (error) {
      console.error("Error fetching assets:", error);
    } finally {
      set({ loading: false });
    }
  },

  removeAsset: async (id) => {
    try {
      await deleteAsset(id);
      set((state) => ({
        assets: state.assets.filter((a) => a.id !== id),
      }));
    } catch (error) {
      console.error("Error deleting asset:", error);
    }
  },
}));
