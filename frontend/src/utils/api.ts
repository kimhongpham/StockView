import axios from "axios";

// Axios instance
const api = axios.create({
  baseURL: "http://localhost:8080/api",
  timeout: 30000, // 30s
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
  const res = await fetch(`http://localhost:8080/api/prices/top?limit=${limit}&type=${type}`);
  if (!res.ok) throw new Error("Failed to fetch top prices");
  const data = await res.json();
  return data.data; // backend trả về { type, success, data, count }
}

// Stock Page APIs
//Lấy tổng quan tài sản (Asset + Company + Metrics + Price)
export async function fetchAssetOverview(symbol: string) {
  const res = await fetch(`/assets/${symbol}/overview`);
  if (!res.ok) throw new Error("Failed to fetch asset overview");
  const data = await res.json();
  return data; 
}

//Lấy lịch sử giá (30 ngày gần nhất)
export async function fetchPriceHistory(assetId: string, limit = 30) {
  const res = await fetch(`/api/prices/${assetId}/history/paged?page=0&size=${limit}`);
  if (!res.ok) throw new Error("Failed to fetch price history");
  return res.json();
}

//Lấy thống kê giá (min, max, avg, YTD)
export async function fetchPriceStats(assetId: string, range = "month") {
  const res = await fetch(`/prices/${assetId}/stats?range=${range}`);
  if (!res.ok) throw new Error("Failed to fetch price stats");
  const data = await res.json();
  return data.data;
}

// StockDetailPage APIs
// Lấy chi tiết một asset
export async function fetchAssetDetails(code: string): Promise<Asset> {
  const resp = await api.get<Asset>(`/assets/${code}/details`);
  return resp.data;
}

// Lấy thông tin công ty theo symbol
export async function fetchCompanyInfo(symbol: string): Promise<any> {
  const resp = await api.get(`/assets/${symbol}/company`);
  return resp.data;
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
export const fetchAndSaveAllPrices = async () => {
  const res = await api.post(`/prices/fetch-all`);
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