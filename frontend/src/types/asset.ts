export interface Asset {
  id: string;
  name: string;
  symbol: string;
  type?: string;
  description?: string;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface PriceDto {
  id?: string;
  assetId: string;
  price: number;
  timestamp: string;
  changePercent?: number;
  volume?: number;
  high24h?: number;
  low24h?: number;
  marketCap?: number;
  source?: string;
}

export interface ChartPoint {
  timestamp: string;
  open: number;
  high: number;
  low: number;
  close: number;
  volume: number;
}

export interface LatestPrice {
  price: number;
  changePercent: number;
  volume?: number;
  marketCap?: number;
}
