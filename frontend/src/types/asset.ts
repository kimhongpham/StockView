// src/types/asset.ts

export interface Asset {
  id: string;
  name: string;
  symbol: string;
  type?: string; // STOCK, CRYPTO, METAL, ...
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
