import axios from "axios";

// Axios instance
const api = axios.create({
  baseURL: "http://localhost:8080/api",
  timeout: 120000,
});

// Interceptor thêm token tự động
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// Types
export interface Asset {
  id: string;
  symbol: string;
  name: string;
  type: string;
}

export interface LatestPrice {
  price: number;
  volume?: number;
  changePercent24h?: number;
  timestamp?: string;
  source?: string;
}

export interface ChangeResponse {
  percent?: number;
  absolute?: number;
}

export interface ChartPoint {
  timestamp: string | number;
  price: number;
  volume?: number;
}

export interface StatsResponse {
  marketCap?: number;
  pe?: number | null;
  pb?: number | null;
  [k: string]: any;
}

export interface AssetOverview {
  id: string;
  symbol: string;
  name: string;
  description: string;
  isActive: boolean;
  currentPrice: number;
  volume: number | null;
  changePercent: number;
  peRatio: number | null;
  pbRatio: number | null;
  high24h: number | null;
  low24h: number | null;
  marketCap_static?: number;
  marketCap?: number;
  timestamp: string;
  source: string;
  sharesOutstanding?: number;
  evToEbitda?: number;
  eps?: number;
  bookValue?: number;
}

export interface CandleDTO {
  timestamp: string | number | null;
  open: number;
  high: number;
  low: number;
  close: number;
  volume?: number;
}

// ----------------- API functions -----------------
// Dash board APIs
// Lấy giá mới nhất
export const fetchLatestPrice = async (assetId: string) => {
  const res = await fetch(`/api/prices/${assetId}/latest`);
  if (!res.ok) throw new Error("Failed to fetch latest price");
  return await res.json();
};

// Lấy dữ liệu biểu đồ (chart data)
export async function fetchPriceChart(
  assetId: string,
  interval: string = "1m",
  limit: number = 100
) {
  const res = await fetch(
    `/api/prices/${assetId}/chart?interval=${interval}&limit=${limit}`
  );
  if (!res.ok) throw new Error("Không thể tải dữ liệu biểu đồ");
  const json = await res.json();
  return json.data || [];
}

// Lấy top giá (tăng/giảm)
export async function fetchTopPrices(type: "gainers" | "losers", limit = 5) {
  const res = await api.get(`/prices/top`, { params: { limit, type } });
  return res.data.data;
}

// Stock Page APIs
//Lấy tổng quan tài sản (Asset + Company + Metrics + Price)
export async function fetchAssetOverview(symbol: string): Promise<AssetOverview> {
  try {
    const response = await api.get(`/assets/${symbol}/overview`);
    return response.data;
  } catch (error) {
    console.error('Error fetching asset overview:', error);
    throw error;
  }
}

//Lấy lịch sử giá (30 ngày gần nhất)
export async function fetchPriceHistory(assetId: string, limit = 30) {
  try {
    const response = await api.get(`/prices/${assetId}/history/paged?page=0&size=${limit}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching price history:', error);
    throw error;
  }
}

//Lấy thống kê giá (min, max, avg, YTD)
export async function fetchPriceStats(assetId: string, range = "month") {
  try {
    const response = await api.get(`/prices/${assetId}/stats?range=${range}`);
    return response.data.data || response.data;
  } catch (error) {
    console.error('Error fetching price stats:', error);
    throw error;
  }
}

// StockDetailPage APIs
// Thay vì gọi riêng:
export async function fetchAssetDetails(symbol: string) {
  return fetchAssetOverview(symbol);
}

export async function fetchCompanyInfo(symbol: string) {
  const overview = await fetchAssetOverview(symbol);
  return { name: overview.name, description: overview.description };
}

// Lấy stats / thông số
export async function fetchStats(assetId: string): Promise<StatsResponse> {
  const resp = await api.get<StatsResponse>(`/prices/${assetId}/stats`);
  return resp.data;
}

// AssetStore APIs
// Lấy tất cả assets
export const fetchAllAssets = async (): Promise<Asset[]> => {
  const res = await api.get("/assets");
  return res.data;
};

// Lấy danh sách thị trường (stocks, crypto, ...)
export async function fetchNewMarketStocks() {
  const res = await fetch("http://localhost:8080/api/assets/market/stocks/new");
  if (!res.ok) throw new Error("Failed to fetch new market stocks");
  return res.json();
}

// Gọi backend fetch giá mới nhất + lưu DB
export async function fetchAndSaveAssetPrice(assetId: string) {
  const res = await fetch(`/api/prices/${assetId}/fetch`, { method: "POST" });
  if (!res.ok) throw new Error("Failed to fetch and save asset price");
  return res.json();
}

// ========== ADMIN APIs ==========

// Cập nhật giá cho tất cả asset (admin/system)
export const startFetchAllPrices = async () => {
  const res = await api.post(`/prices/fetch-all/start`);
  return res.data; // { jobId, message }
};

export const getFetchAllStatus = async (jobId: string) => {
  const res = await api.get(`/prices/fetch-all/status/${jobId}`);
  return res.data;
};

// Xóa asset theo ID
export const deleteAsset = async (assetId: string) => {
  const res = await fetch(`/api/assets/${assetId}`, {
    method: "DELETE",
  });

  if (!res.ok) {
    const error = await res.json().catch(() => ({}));
    throw new Error(error.error || "Failed to delete asset");
  }

  return res.json();
};

// Lấy thay đổi giá theo giờ (ví dụ 7 ngày = 168 giờ)
export async function fetchChange(assetId: string, hours = 168): Promise<ChangeResponse> {
  const resp = await api.get<ChangeResponse>(`/prices/${assetId}/change`, {
    params: { hours },
  });
  return resp.data;
}

// Thêm biến API_URL dùng chung cho watchlist
const API_URL = "http://localhost:8080/api/users";

// ================= Watchlist APIs =================
export const getWatchlist = async (token: string) => {
  const res = await axios.get(`${API_URL}/watchlist`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  return res.data; // giả sử backend trả về array symbol
};

export const addToWatchlist = async (symbol: string, token: string) => {
  await axios.post(
    `${API_URL}/watchlist`,
    { symbol },
    { headers: { Authorization: `Bearer ${token}` } }
  );
};

export const removeFromWatchlist = async (symbol: string, token: string) => {
  await axios.delete(`${API_URL}/watchlist`, {
    headers: { Authorization: `Bearer ${token}` },
    data: { symbol },
  });
};

export const searchAssets = async (query: string): Promise<Asset[]> => {
  if (!query.trim()) return [];
  const res = await fetch(`http://localhost:8080/api/assets/search?query=${encodeURIComponent(query)}`);
  if (!res.ok) throw new Error("Search failed");
  return res.json();
};

export default api;