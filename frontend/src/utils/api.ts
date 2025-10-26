import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api", // hoặc "/api" nếu có proxy
  timeout: 10000,
});


// Types
export interface Asset {
  id: string;
  symbol: string;
  name?: string;
  // backend có thể trả thêm field khác
}

export interface LatestPrice {
  price: number;
  volume?: number;
  changePercent24h?: number; // % 24h
  timestamp?: string;
  source?: string;
}

export interface ChangeResponse {
  percent?: number; // e.g. change over requested period in percent
  absolute?: number;
}

export interface ChartPoint {
  timestamp: string | number; // ISO string or unix ms
  price: number;
}

export interface StatsResponse {
  marketCap?: number;
  pe?: number | null;
  pb?: number | null;
  [k: string]: any;
}

// API functions

// Hàm fetchPriceHistory
export async function fetchPriceHistory(assetId) {
  const response = await api.get(`/prices/${assetId}/latest`);
  return response.data;
}

export const fetchAllAssets = async () => {
  const res = await api.get("/assets");
  return res.data;
};

export async function fetchMarketAssets(scope = "stocks"): Promise<Asset[]> {
  // GET /api/assets/market/stocks
  const url = `/assets/market/${scope}`;
  const resp = await api.get<Asset[]>(url);
  return resp.data;
}

export async function fetchLatestPrice(assetId: string): Promise<LatestPrice> {
  // GET /api/prices/{assetId}/latest
  const resp = await api.get<LatestPrice>(`/prices/${assetId}/latest`);
  return resp.data;
}

export async function fetchChange7D(assetId: string, hours = 168): Promise<ChangeResponse> {
  // GET /api/prices/{assetId}/change?hours=168
  const resp = await api.get<ChangeResponse>(`/prices/${assetId}/change`, {
    params: { hours },
  });
  return resp.data;
}

export async function fetchChart30D(assetId: string, interval = "1d", limit = 30): Promise<ChartPoint[]> {
  // GET /api/prices/{assetId}/chart?interval=1d&limit=30
  const resp = await api.get<ChartPoint[]>(`/prices/${assetId}/chart`, {
    params: { interval, limit },
  });
  return resp.data;
}

export async function fetchStats(assetId: string): Promise<StatsResponse> {
  // optional: GET /api/prices/{assetId}/stats
  const resp = await api.get<StatsResponse>(`/prices/${assetId}/stats`);
  return resp.data;
}

// Helpful: fetch asset by symbol or id (used in your current code)
export async function fetchAssetById(idOrSymbol: string): Promise<Asset | null> {
  // Try by id first
  try {
    const resp = await api.get<Asset>(`/assets/${idOrSymbol}`);
    return resp.data;
  } catch {
    // fallback: maybe the backend exposes search by symbol; try market list and find
    try {
      const markets = await fetchMarketAssets("stocks");
      const found = markets.find(
        (a) =>
          a.id === idOrSymbol ||
          a.symbol?.toUpperCase() === idOrSymbol.toUpperCase() ||
          a.name?.toUpperCase() === idOrSymbol.toUpperCase()
      );
      return found ?? null;
    } catch {
      return null;
    }
  }
}
