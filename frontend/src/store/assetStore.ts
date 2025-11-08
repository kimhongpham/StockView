import { create } from "zustand";
import {
  fetchAllAssets,
  fetchNewMarketStocks,
  fetchLatestPrice,
  fetchPriceHistory,
  fetchAndSaveAssetPrice,
  deleteAsset,
} from "../utils/api";

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

  fetchAllAssets: () => Promise<void>;
  fetchMarketAssets: (type: string) => Promise<void>;
  fetchLatestPrices: (assetIds: string[]) => Promise<void>;
  refreshAssetPrice: (assetId: string) => Promise<void>;
  fetchPriceHistory: (assetId: string) => Promise<Price[]>;
  deleteAsset: (assetId: string) => Promise<void>;
}

export const useAssetStore = create<AssetStore>((set, get) => ({
  assets: [],
  latestPrices: {},
  loading: false,
  error: null,

  // Lấy tất cả asset trong DB
  fetchAllAssets: async () => {
    set({ loading: true, error: null });
    try {
      const assets = await fetchAllAssets();
      set({ assets });
    } catch (err: any) {
      console.error("❌ Failed to fetch all assets:", err);
      set({ error: err.message || "Failed to fetch all assets" });
    } finally {
      set({ loading: false });
    }
  },

  // Lấy danh sách cổ phiếu thị trường (fetch từ Finnhub và lưu DB)
  fetchMarketAssets: async () => {
    set({ loading: true, error: null });
    try {
      const assets = await fetchNewMarketStocks();
      set({ assets });
    } catch (err: any) {
      console.error("❌ Failed to fetch new market stocks:", err);
      set({
        error: err.message || "Failed to fetch new market stocks",
      });
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
