export interface Stock {
  code: string;
  name: string;
  price: number;
  change: number;
  changePercent: number;
  value: number;
  high?: number;
  low?: number;
  volume?: string;
  time?: string;
}

export interface Transaction {
  id: string;
  amount: string;
  date: string;
  status: string;
}

export interface WatchlistItem extends Stock {}