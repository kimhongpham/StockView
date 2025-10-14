interface Stock {
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

interface Transaction {
  id: string;
  amount: string;
  date: string;
  status: 'Success' | 'Pending' | 'Failed';
}

interface WatchlistItem extends Stock {}

interface TrendingStock {
  name: string;
  last: number;
  high: number;
  low: number;
  change: number;
  changePercent: number;
  volume: string;
  time: string;
}

// Mock Data
export const stockData: Stock[] = [
  {
    code: "AAPL",
    name: "Apple Inc.",
    price: 175.34,
    change: 1.23,
    changePercent: 0.71,
    value: 2.75,
  },
  {
    code: "GOOGL",
    name: "Alphabet Inc.",
    price: 142.56,
    change: -0.45,
    changePercent: -0.31,
    value: 1.82,
  },
  {
    code: "MSFT",
    name: "Microsoft Corp.",
    price: 330.12,
    change: 2.34,
    changePercent: 0.71,
    value: 2.46,
  },
  {
    code: "AMZN",
    name: "Amazon.com Inc.",
    price: 145.67,
    change: 1.12,
    changePercent: 0.77,
    value: 1.49,
  },
  {
    code: "TSLA",
    name: "Tesla Inc.",
    price: 245.78,
    change: -3.45,
    changePercent: -1.38,
    value: 0.78,
  },
  {
    code: "META",
    name: "Meta Platforms Inc.",
    price: 310.45,
    change: 4.56,
    changePercent: 1.49,
    value: 0.79,
  },
  {
    code: "NVDA",
    name: "NVIDIA Corp.",
    price: 435.67,
    change: 12.34,
    changePercent: 2.92,
    value: 1.08,
  },
  {
    code: "JPM",
    name: "JPMorgan Chase",
    price: 156.78,
    change: 0.67,
    changePercent: 0.43,
    value: 0.45,
  },
];

export const watchlistData: WatchlistItem[] = [
  {
    code: "AAPL",
    name: "Apple Inc.",
    price: 175.34,
    change: 1.23,
    changePercent: 0.71,
    value: 2.75,
  },
  {
    code: "MSFT",
    name: "Microsoft Corp.",
    price: 330.12,
    change: 2.34,
    changePercent: 0.71,
    value: 2.46,
  },
  {
    code: "NVDA",
    name: "NVIDIA Corp.",
    price: 435.67,
    change: 12.34,
    changePercent: 2.92,
    value: 1.08,
  },
];

export const transactionData: Transaction[] = [
  {
    id: "TX001",
    amount: "$1,234.56",
    date: "2023-05-15",
    status: "Success",
  },
  {
    id: "TX002",
    amount: "$567.89",
    date: "2023-05-14",
    status: "Pending",
  },
  {
    id: "TX003",
    amount: "$2,345.67",
    date: "2023-05-12",
    status: "Success",
  },
  {
    id: "TX004",
    amount: "$789.01",
    date: "2023-05-10",
    status: "Success",
  },
  {
    id: "TX005",
    amount: "$1,567.89",
    date: "2023-05-08",
    status: "Success",
  },
];

export const trendingStocksData: TrendingStock[] = [
  {
    name: "Vingroup",
    last: 205400,
    high: 205400,
    low: 193900,
    change: 13400,
    changePercent: 6.98,
    volume: "23.99M",
    time: "14:45:00",
  },
  {
    name: "Hoà Phát",
    last: 29000.0,
    high: 29250.0,
    low: 28850.0,
    change: -600.0,
    changePercent: -2.03,
    volume: "88.24M",
    time: "14:45:00",
  },
  {
    name: "MBBank",
    last: 27350.0,
    high: 27400.0,
    low: 27000.0,
    change: -100.0,
    changePercent: -0.36,
    volume: "45.31M",
    time: "14:45:00",
  },
  {
    name: "FPT",
    last: 94000.0,
    high: 94900.0,
    low: 93700.0,
    change: -2100.0,
    changePercent: -2.19,
    volume: "11.15M",
    time: "14:45:00",
  },
  {
    name: "Chứng khoán SSI",
    last: 41350.0,
    high: 41700.0,
    low: 40000.0,
    change: 650.0,
    changePercent: 1.6,
    volume: "40.91M",
    time: "14:45:00",
  },
  {
    name: "Techcombank",
    last: 41300.0,
    high: 41500.0,
    low: 39250.0,
    change: 1950.0,
    changePercent: 4.96,
    volume: "41.45M",
    time: "14:45:00",
  },
  {
    name: "Vinhomes",
    last: 124200,
    high: 126000,
    low: 122100,
    change: 1200,
    changePercent: 0.98,
    volume: "13.78M",
    time: "14:45:00",
  },
  {
    name: "Vincom Retail",
    last: 43000.0,
    high: 43000.0,
    low: 40350.0,
    change: 2650.0,
    changePercent: 6.57,
    volume: "28.26M",
    time: "14:45:00",
  },
  {
    name: "Vietinbank",
    last: 56000.0,
    high: 56300.0,
    low: 54600.0,
    change: 800.0,
    changePercent: 1.45,
    volume: "10.47M",
    time: "14:45:00",
  },
];

// Helper functions
export const getStockByCode = (code: string): Stock | undefined => {
  return stockData.find(stock => stock.code === code);
};

export const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(amount);
};

export const formatNumber = (num: number): string => {
  const absNum = Math.abs(num);
  
  if (absNum >= 1000) {
    const formatted = absNum.toLocaleString();
    return num < 0 ? `-${formatted}` : formatted;
  }
  
  const formatted = absNum.toFixed(1).replace('.0', '');
  return num < 0 ? `-${formatted}` : formatted;
};