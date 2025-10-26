// data/mockData.ts
interface Stock {
  code: string;
  name: string;
  price: number;
  change: number;
  changePercent: number;
  value: number;
  volume: number;
  change7d?: number;
  marketCap?: string;
  pe?: number;
  pb?: number;
  high?: number;
  low?: number;
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

interface MarketIndex {
  name: string;
  value: number;
  change: number;
  changePercent: number;
}

interface TopGainer {
  code: string;
  name: string;
  changePercent: number;
  volume: number;
}

interface ForeignBuy {
  code: string;
  name: string;
  netBuy: number;
}

interface FundActivity {
  code: string;
  fund: string;
  quantity: number;
}

// Updated Mock Data for Vietnamese stocks matching the image
export const stockData: Stock[] = [
  {
    code: "MBB",
    name: "Ngân hàng Thương mại Cổ phần Quân đội",
    price: 25250,
    change: -1840,
    changePercent: -6.82,
    value: 218.29,
    volume: 66008300,
    change7d: -1.28,
    marketCap: "218,290T",
    pe: 8.9,
    pb: 1.8
  },
  {
    code: "GAS",
    name: "Tổng Công ty Khí Việt Nam",
    price: 57300,
    change: -1295,
    changePercent: -2.21,
    value: 141.40,
    volume: 537100,
    change7d: -3.78,
    marketCap: "141,398T",
    pe: 11.7,
    pb: 2.1
  },
  {
    code: "MWG",
    name: "Công ty Cổ phần Đầu tư Thế Giới Di Động",
    price: 81500,
    change: -3000,
    changePercent: -3.55,
    value: 124.93,
    volume: 12590800,
    change7d: 3.05,
    marketCap: "124,929T",
    pe: 25.8,
    pb: 4.2
  },
  {
    code: "SAB",
    name: "Tổng Công ty Cổ phần Bia - Rượu - Nước giải khát Sài Gòn",
    price: 44300,
    change: -945,
    changePercent: -2.09,
    value: 58.04,
    volume: 812200,
    change7d: -1.31,
    marketCap: "58,035T",
    pe: 14.2,
    pb: 2.7
  },
  {
    code: "VJC",
    name: "Công ty Cổ phần Hàng không VietJet",
    price: 175500,
    change: 490,
    changePercent: 0.28,
    value: 103.53,
    volume: 3231400,
    change7d: 31.28,
    marketCap: "103,531T",
    pe: 54.8,
    pb: 4.4
  },
  {
    code: "PLX",
    name: "Tập đoàn Xăng dầu Việt Nam",
    price: 32700,
    change: -850,
    changePercent: -2.53,
    value: 42.63,
    volume: 1992200,
    change7d: -1.47,
    marketCap: "42,628T",
    pe: 20.2,
    pb: 1.7
  },
  {
    code: "TCB",
    name: "Ngân hàng TMCP Kỹ thương Việt Nam",
    price: 37900,
    change: -2750,
    changePercent: -6.76,
    value: 288.06,
    volume: 24062100,
    change7d: 3.30,
    marketCap: "288,055T",
    pe: 13.5,
    pb: 1.8
  },
  {
    code: "VHM",
    name: "Công ty Cổ phần Vinhomes",
    price: 110400,
    change: -5600,
    changePercent: -4.82,
    value: 476.46,
    volume: 9874300,
    change7d: -5.69,
    marketCap: "476,459T",
    pe: 15.8,
    pb: 2.2
  },
  {
    code: "VIE",
    name: "Công ty Cổ phần Vincapital",
    price: 38150,
    change: -2850,
    changePercent: -6.95,
    value: 93.17,
    volume: 18547700,
    change7d: 1.61,
    marketCap: "93,165T",
    pe: 21.2,
    pb: 2.1
  },
  {
    code: "SSI",
    name: "Công ty Cổ phần Chứng khoán SSI",
    price: 38000,
    change: -2800,
    changePercent: -6.86,
    value: 84.70,
    volume: 65166800,
    change7d: 0.25,
    marketCap: "84,697T",
    pe: 27.9,
    pb: 3.0
  },
  {
    code: "VNM",
    name: "Công ty Cổ phần Sữa Việt Nam",
    price: 56000,
    change: -2800,
    changePercent: -4.76,
    value: 122.87,
    volume: 7077100,
    change7d: -5.77,
    marketCap: "122,869T",
    pe: 14.3,
    pb: 3.9
  },
  {
    code: "FPT",
    name: "Công ty Cổ phần FPT",
    price: 87000,
    change: -1090,
    changePercent: -1.24,
    value: 150.08,
    volume: 13484100,
    change7d: -8.32,
    marketCap: "150,078T",
    pe: 17.4,
    pb: 4.5
  },
  {
    code: "VIC",
    name: "Tập đoàn Vingroup",
    price: 195800,
    change: -8180,
    changePercent: -4.01,
    value: 786.02,
    volume: 5321400,
    change7d: 6.25,
    marketCap: "786,015T",
    pe: 58.1,
    pb: 5.5
  },
  {
    code: "HPG",
    name: "Công ty Cổ phần Tập đoàn Hòa Phát",
    price: 26150,
    change: -1850,
    changePercent: -6.60,
    value: 214.91,
    volume: 87782000,
    change7d: -5.41,
    marketCap: "214,913T",
    pe: 16.0,
    pb: 1.8
  },
  {
    code: "CTG",
    name: "Ngân hàng Thương mại Cổ phần Công thương Việt Nam",
    price: 49150,
    change: -3050,
    changePercent: -5.84,
    value: 280.31,
    volume: 22772100,
    change7d: -4.67,
    marketCap: "280,313T",
    pe: 9.3,
    pb: 1.7
  },
  {
    code: "MSN",
    name: "Công ty Cổ phần Tập đoàn Masan",
    price: 81900,
    change: -6100,
    changePercent: -6.93,
    value: 133.80,
    volume: 31518400,
    change7d: 4.64,
    marketCap: "133,803T",
    pe: 47.5,
    pb: 4.1
  }
];

export const watchlistData: WatchlistItem[] = [
  {
    code: "MBB",
    name: "Ngân hàng Thương mại Cổ phần Quân đội",
    price: 25250,
    change: -1840,
    changePercent: -6.82,
    value: 218.29,
    volume: 66008300
  },
  {
    code: "GAS",
    name: "Tổng Công ty Khí Việt Nam",
    price: 57300,
    change: -1295,
    changePercent: -2.21,
    value: 141.40,
    volume: 537100
  },
  {
    code: "VJC",
    name: "Công ty Cổ phần Hàng không VietJet",
    price: 175500,
    change: 490,
    changePercent: 0.28,
    value: 103.53,
    volume: 3231400
  }
];

export const transactionData: Transaction[] = [
  {
    id: "TX001",
    amount: "1,234,560,000",
    date: "2023-05-15",
    status: "Success",
  },
  {
    id: "TX002",
    amount: "567,890,000",
    date: "2023-05-14",
    status: "Pending",
  },
  {
    id: "TX003",
    amount: "2,345,670,000",
    date: "2023-05-12",
    status: "Success",
  },
  {
    id: "TX004",
    amount: "789,010,000",
    date: "2023-05-10",
    status: "Success",
  },
  {
    id: "TX005",
    amount: "1,567,890,000",
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

// New data for dashboard
export const marketIndices: MarketIndex[] = [
  { name: "VNINDEX", value: 1698.93, change: 32.26, changePercent: 1.86 },
  { name: "Nasdaq Composite", value: 22679.97, change: 117.43, changePercent: 0.52 },
  { name: "Russell 2000", value: 2452.17, change: 14.84, changePercent: 0.60 },
  { name: "FTSE 100", value: 9354.57, change: 81.52, changePercent: 0.86 },
  { name: "Hang Seng Composite", value: 25829.13, change: 582.03, changePercent: 2.31 },
  { name: "Dow Jones", value: 46190.62, change: 238.37, changePercent: 0.52 },
  { name: "S&P 500", value: 6664.00, change: 34.92, changePercent: 0.53 },
  { name: "US Dollar Index (DXY)", value: 98.55, change: 0.01, changePercent: 0.02 },
  { name: "Nikkei 225", value: 49185.50, change: 1603.40, changePercent: 3.37 },
  { name: "Bitcoin", value: 111180.08, change: 2537.31, changePercent: 2.34 }
];

export const topGainers: TopGainer[] = [
  { code: "VVS", name: "Công ty CP VVS", changePercent: 13.81, volume: 218000 },
  { code: "HLC", name: "Công ty CP HLC", changePercent: 9.82, volume: 581300 },
  { code: "KSV", name: "Công ty CP KSV", changePercent: 8.86, volume: 206300 },
  { code: "VGI", name: "Công ty CP VGI", changePercent: 8.58, volume: 772000 },
  { code: "SVN", name: "Công ty CP SVN", changePercent: 7.14, volume: 383600 }
];

export const foreignBuyStocks: ForeignBuy[] = [
  { code: "VRE", name: "Vincom Retail", netBuy: 1250000 },
  { code: "DIG", name: "Tập đoàn DIG", netBuy: 980000 },
  { code: "VIX", name: "Công ty CP VIX", netBuy: 756000 },
  { code: "VJC", name: "Vietjet Air", netBuy: 623000 },
  { code: "HDC", name: "Công ty CP HDC", netBuy: 512000 }
];

export const fundActivities: FundActivity[] = [
  { code: "VHM", fund: "IPAAM", quantity: 85000 },
  { code: "TPB", fund: "BAOVIETFUND", quantity: 34600 },
  { code: "TCB", fund: "BAOVIETFUND", quantity: 97200 },
  { code: "TCB", fund: "DFVN", quantity: -27000 },
  { code: "TCB", fund: "VINACAPITAL", quantity: 30230 }
];

// Helper functions
export const getStockByCode = (code: string): Stock | undefined => {
  return stockData.find(stock => stock.code === code);
};

export const getStockChartData = (code: string) => {
  // Mock chart data for different stocks
  const chartData: { [key: string]: number[] } = {
    'MBB': [28000, 27500, 27000, 26800, 26500, 26200, 26000, 25800, 25500, 25250],
    'GAS': [59000, 58500, 58000, 57800, 57500, 57200, 57000, 56800, 56500, 57300],
    'MWG': [85000, 84000, 83500, 83000, 82500, 82000, 81500, 81000, 80500, 81500],
    'SAB': [46000, 45500, 45000, 44800, 44500, 44300, 44000, 43800, 43500, 44300],
    'VJC': [175000, 174500, 174000, 175200, 175400, 175300, 175500, 175600, 175800, 175500],
    'PLX': [34000, 33500, 33000, 32800, 32500, 32700, 32600, 32500, 32400, 32700],
    'TCB': [41000, 40500, 40000, 39500, 39000, 38500, 38000, 37800, 37600, 37900],
    'VHM': [116000, 115000, 114000, 113000, 112000, 111000, 110400, 110000, 109500, 110400],
    'VIE': [41000, 40500, 40000, 39500, 39000, 38500, 38200, 38000, 37900, 38150],
    'SSI': [41000, 40500, 40000, 39500, 39000, 38500, 38200, 38000, 37900, 38000],
    'VNM': [59000, 58500, 58000, 57500, 57000, 56500, 56200, 56000, 55800, 56000],
    'FPT': [88000, 87800, 87500, 87200, 87000, 86800, 86500, 86300, 86000, 87000],
    'VIC': [204000, 202000, 200000, 198000, 196000, 195800, 195000, 194500, 194000, 195800],
    'HPG': [28000, 27500, 27000, 26800, 26500, 26200, 26000, 25800, 25600, 26150],
    'CTG': [52000, 51500, 51000, 50500, 50000, 49500, 49200, 49000, 48800, 49150],
    'MSN': [88000, 87000, 86000, 85000, 84000, 83000, 82500, 82000, 81500, 81900]
  };
  
  return chartData[code] || [100, 105, 102, 108, 110, 115, 112, 118, 120, 122];
};

export const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(amount);
};

export const formatNumber = (num: number): string => {
  return new Intl.NumberFormat('vi-VN').format(num);
};

export const formatVolume = (volume: number): string => {
  if (volume >= 1000000) {
    return (volume / 1000000).toFixed(1) + 'M';
  }
  if (volume >= 1000) {
    return (volume / 1000).toFixed(1) + 'K';
  }
  return volume.toString();
};