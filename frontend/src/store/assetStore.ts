import { create } from "zustand";
import {
  fetchAllAssets,
  fetchMarketAssets,
  fetchLatestPrice,
  fetchPriceHistory,
  fetchAndSaveAssetPrice,
  createAsset,
  updateAsset,
  deleteAsset,
} from "../utils/api";

// Kiểu dữ liệu cơ bản (có thể chỉnh lại nếu bạn đã định nghĩa trong /types)
interface Asset {
  id: string;
  symbol: string;
  name: string;
  type: string;
}

interface Price {
  id: string;
  assetId: string;
  timestamp: string;
  price: number;
  changePercent?: number;
}

interface AssetStore {
  assets: Asset[];
  latestPrices: Record<string, Price>;
  loading: boolean;
  error: string | null;

  // actions
  fetchMarketAssets: (type: string) => Promise<void>;
  fetchLatestPrices: (assetIds: string[]) => Promise<void>;
  refreshAssetPrice: (assetId: string) => Promise<void>;
  fetchPriceHistory: (assetId: string) => Promise<Price[]>;
  createAsset: (data: Partial<Asset>) => Promise<void>;
  updateAsset: (assetId: string, data: Partial<Asset>) => Promise<void>;
  deleteAsset: (assetId: string) => Promise<void>;
}

export const useAssetStore = create<AssetStore>((set, get) => ({
  assets: [],
  latestPrices: {},
  loading: false,
  error: null,

  // Lấy danh sách cổ phiếu thị trường
  fetchMarketAssets: async (type: string) => {
    set({ loading: true, error: null });
    try {
      const assets = await fetchMarketAssets(type);
      set({ assets });
    } catch (err: any) {
      set({ error: err.message || "Failed to fetch market assets" });
    } finally {
      set({ loading: false });
    }
  },

  // Lấy giá mới nhất cho nhiều asset (Promise.all)
  fetchLatestPrices: async (assetIds: string[]) => {
    const { latestPrices } = get();
    try {
      const results = await Promise.all(
        assetIds.map(async (id) => {
          const price = await fetchLatestPrice(id);
          return { id, price };
        })
      );
      const updated = { ...latestPrices };
      results.forEach(({ id, price }) => {
        updated[id] = price;
      });
      set({ latestPrices: updated });
    } catch (err) {
      console.error("❌ Failed to fetch latest prices:", err);
    }
  },

  // Gọi API cập nhật giá mới từ nguồn ngoài (backend fetch + save)
  refreshAssetPrice: async (assetId: string) => {
    try {
      await fetchAndSaveAssetPrice(assetId);
      const newPrice = await fetchLatestPrice(assetId);
      set((state) => ({
        latestPrices: { ...state.latestPrices, [assetId]: newPrice },
      }));
    } catch (err) {
      console.error("❌ Failed to refresh price:", err);
    }
  },

  // Lấy lịch sử giá theo asset
  fetchPriceHistory: async (assetId: string) => {
    try {
      return await fetchPriceHistory(assetId);
    } catch (err) {
      console.error("❌ Failed to fetch price history:", err);
      return [];
    }
  },

  // CRUD cho Asset
  createAsset: async (data) => {
    try {
      await createAsset(data);
      const updated = await fetchAllAssets();
      set({ assets: updated });
    } catch (err) {
      console.error("❌ Failed to create asset:", err);
    }
  },

  updateAsset: async (assetId, data) => {
    try {
      await updateAsset(assetId, data);
      const updated = await fetchAllAssets();
      set({ assets: updated });
    } catch (err) {
      console.error("❌ Failed to update asset:", err);
    }
  },

  deleteAsset: async (assetId) => {
    try {
      await deleteAsset(assetId);
      set((state) => ({
        assets: state.assets.filter((a) => a.id !== assetId),
      }));
    } catch (err) {
      console.error("❌ Failed to delete asset:", err);
    }
  },
}));
