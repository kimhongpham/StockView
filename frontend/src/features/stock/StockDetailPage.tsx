import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import {
  fetchPriceHistory,
  fetchAssetOverview,
  fetchPriceStats,
  AssetOverview,
} from "../../utils/api";
import ChartSection from "../../components/charts/ChartSection";
import "../../styles/pages/StockDetailPage.css";

const StockDetailPage: React.FC = () => {
  const { symbol } = useParams<{ symbol: string }>();

  const [overview, setOverview] = useState<AssetOverview | null>(null);
  const [priceHistory, setPriceHistory] = useState<any[]>([]);
  const [priceStats, setPriceStats] = useState<any>(null);
  const [activeTab, setActiveTab] = useState<
    "overview" | "financials" | "news"
  >("overview");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!symbol) return;

    (async () => {
      try {
        setLoading(true);
        setError(null);

        const overviewData = await fetchAssetOverview(symbol);
        setOverview(overviewData);

        if (overviewData.id) {
          try {
            const historyData = await fetchPriceHistory(overviewData.id, 30);
            const chartData =
              historyData.content || historyData.data || historyData || [];
            setPriceHistory(chartData);
          } catch (historyError) {
            console.warn(
              "Could not fetch price history, using mock data:",
              historyError
            );
            setPriceHistory(generateMockChartData(overviewData.currentPrice));
          }

          try {
            const statsData = await fetchPriceStats(overviewData.id, "month");
            setPriceStats(statsData);
          } catch (statsError) {
            console.warn(
              "Could not fetch price stats, continuing without stats:",
              statsError
            );
          }
        }
      } catch (err) {
        console.error("Error fetching data:", err);
        setError(
          `Không thể tải thông tin cổ phiếu: ${
            err instanceof Error ? err.message : "Lỗi không xác định"
          }`
        );
      } finally {
        setLoading(false);
      }
    })();
  }, [symbol]);

  const formatNumber = (num: number | undefined | null) =>
    num != null ? num.toLocaleString("vi-VN") : "-";

  const formatPrice = (price: number | undefined | null) =>
    price != null ? formatNumber(price) : "-";

  const formatPercentage = (percent: number | undefined | null) =>
    percent != null ? `${percent >= 0 ? "+" : ""}${percent.toFixed(2)}%` : "-";

  const formatCurrency = (
    value: number | undefined | null,
    suffix: string = ""
  ) => (value != null ? `${formatNumber(value)}${suffix}` : "-");

  const generateMockChartData = (basePrice: number) => {
    const data = [];
    for (let i = 30; i >= 0; i--) {
      const date = new Date();
      date.setDate(date.getDate() - i);
      const randomVariation = (Math.random() - 0.5) * 0.1;
      data.push({
        timestamp: date.toISOString(),
        close: basePrice * (1 + randomVariation),
      });
    }
    return data;
  };

  const calculateChange24h = () => {
    if (!overview?.currentPrice || !overview?.changePercent) return 0;
    return (overview.currentPrice * overview.changePercent) / 100;
  };

  const getChartData = () => {
    if (priceHistory.length > 0) {
      return priceHistory.map((item: any) => ({
        timestamp: item.timestamp || item.date,
        close: item.price ?? item.close,
        open: item.open ?? null,
        high: item.high ?? null,
        low: item.low ?? null,
        volume: item.volume ?? null,
      }));
    }

    return overview
      ? generateMockChartData(overview.currentPrice).map((d) => ({
          ...d,
          open: null,
          high: null,
          low: null,
          volume: null,
        }))
      : [];
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="loading-spinner"></div>
        <div className="loading-text">Đang tải dữ liệu...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="error-container">
        <div className="error-icon">⚠️</div>
        <div className="error-message">{error}</div>
        <button
          className="btn btn-primary"
          onClick={() => window.location.reload()}
        >
          Thử lại
        </button>
      </div>
    );
  }

  if (!overview) {
    return (
      <div className="not-found-container">
        <div className="not-found-icon">📈</div>
        <div className="not-found-message">
          Không tìm thấy thông tin cổ phiếu
        </div>
      </div>
    );
  }

  return (
    <div className="stock-detail-page">
      <div className="stock-detail-container">
        {/* Header Section */}
        <div className="stock-header">
          <div className="stock-title-section">
            <div className="stock-symbol-container">
              <h1 className="stock-symbol">{overview.symbol}</h1>
              <span className="stock-exchange">NASDAQ</span>
            </div>
            <p className="company-name">{overview.name}</p>
            <p className="company-description">{overview.description}</p>
          </div>

          <div className="stock-price-section">
            <div className="price-main">
              <div className="current-price">
                {formatPrice(overview.currentPrice)}{" "}
                <span className="currency">USD</span>
              </div>
              <div
                className={`price-change ${
                  overview.changePercent >= 0 ? "positive" : "negative"
                }`}
              >
                {overview.changePercent >= 0 ? "+" : ""}
                {formatPrice(calculateChange24h())} (
                {formatPercentage(overview.changePercent)})
              </div>
            </div>
            <div className="price-range">
              <div className="range-item">
                <span className="range-label">Thấp nhất</span>
                <span className="range-value">
                  {formatPrice(overview.low24h) || "-"}
                </span>
              </div>
              <div className="range-item">
                <span className="range-label">Cao nhất</span>
                <span className="range-value">
                  {formatPrice(overview.high24h) || "-"}
                </span>
              </div>
              <div className="range-item">
                <span className="range-label">24h</span>
                <span className="range-value">
                  {formatPercentage(overview.changePercent)}
                </span>
              </div>
            </div>
          </div>
        </div>

        {/* Stats Grid */}
        <div className="stats-grid">
          <StatItem label="Vốn hóa" value={formatCurrency(overview.marketCap_static ?? overview.marketCap, "T")} />
          <StatItem label="Khối lượng giao dịch" value={formatNumber(overview.volume)} />
          <StatItem label="Số lượng cổ phiếu lưu hành" value={formatNumber(overview.sharesOutstanding)} />
          <StatItem label="P/E" value={formatNumber(overview.peRatio)} />
          <StatItem label="P/B" value={formatNumber(overview.pbRatio)} />
          <StatItem label="EV/EBITDA" value={formatNumber(overview.evToEbitda)} />
          <StatItem label="EPS" value={formatNumber(overview.eps)} />
          <StatItem label="Giá trị sổ sách" value={formatNumber(overview.bookValue)} />
        </div>

        {/* Company Info Section */}
        <div className="company-info-section">
          <div className="info-card">
            <h3 className="info-card-title">Thông tin công ty</h3>
            <div className="info-grid">
              <InfoItem label="Ngành" value="Công nghệ" />
              <InfoItem label="Lĩnh vực" value="Thiết bị điện tử & Phần mềm" />
              <InfoItem
                label="Trạng thái"
                value={overview.isActive ? "Đang hoạt động" : "Ngừng hoạt động"}
                status={overview.isActive ? "active" : "inactive"}
              />
              <InfoItem label="Nguồn dữ liệu" value={overview.source} />
              <InfoItem
                label="Cập nhật"
                value={new Date(overview.timestamp).toLocaleString("vi-VN")}
              />
            </div>
          </div>

          <WatchlistCard />
        </div>

        {/* Tabs Navigation */}
        <div className="tabs-navigation">
          <TabButton
            active={activeTab === "overview"}
            onClick={() => setActiveTab("overview")}
            label="Tổng quan"
          />
          <TabButton
            active={activeTab === "financials"}
            onClick={() => setActiveTab("financials")}
            label="Báo cáo tài chính"
          />
          <TabButton
            active={activeTab === "news"}
            onClick={() => setActiveTab("news")}
            label="Tin tức"
          />
        </div>

        {/* Tab Content */}
        <div className="tab-content">
          {activeTab === "overview" && (
            <div className="overview-tab">
              <ChartSection
                data={getChartData()}
                selectedStock={overview.symbol}
                loading={false}
              />
            </div>
          )}

          {activeTab === "financials" && (
            <FinancialsTab
              priceStats={priceStats}
              formatPrice={formatPrice}
              formatPercentage={formatPercentage}
            />
          )}

          {activeTab === "news" && <NewsTab />}
        </div>
      </div>
    </div>
  );
};

// Sub-components
const StatItem: React.FC<{ label: string; value: string }> = ({
  label,
  value,
}) => (
  <div className="stat-item">
    <div className="stat-label">{label}</div>
    <div className="stat-value">{value}</div>
  </div>
);

const InfoItem: React.FC<{
  label: string;
  value: string;
  status?: "active" | "inactive";
}> = ({ label, value, status }) => (
  <div className="info-item">
    <span className="info-label">{label}</span>
    <span className={`info-value ${status ? `status ${status}` : ""}`}>
      {value}
    </span>
  </div>
);

const WatchlistCard: React.FC = () => (
  <div className="watchlist-card">
    <div className="watchlist-count">
      <span className="count-number">-</span>
      <span className="count-label">watchlists</span>
    </div>
    <button className="watchlist-btn">
      <span className="btn-icon">👁️</span>
      Theo dõi
    </button>
  </div>
);

const TabButton: React.FC<{
  active: boolean;
  onClick: () => void;
  label: string;
}> = ({ active, onClick, label }) => (
  <button className={`tab-btn ${active ? "active" : ""}`} onClick={onClick}>
    {label}
  </button>
);

const FinancialsTab: React.FC<{
  priceStats: any;
  formatPrice: (price: number | null | undefined) => string;
  formatPercentage: (percent: number | null | undefined) => string;
}> = ({ priceStats, formatPrice, formatPercentage }) => (
  <div className="financials-tab">
    <h3 className="tab-title">Báo cáo tài chính</h3>
    <p className="tab-subtitle">
      Nội dung báo cáo tài chính sẽ được hiển thị tại đây.
    </p>

    {priceStats && (
      <div className="stats-details">
        <h4 className="stats-title">Thống kê giá</h4>
        <div className="stats-grid-mini">
          <div className="stat-mini-item">
            <span className="stat-mini-label">Giá trung bình</span>
            <span className="stat-mini-value">
              {formatPrice(priceStats.average)}
            </span>
          </div>
          <div className="stat-mini-item">
            <span className="stat-mini-label">Biến động</span>
            <span className="stat-mini-value">
              {formatPercentage(priceStats.volatility)}
            </span>
          </div>
          <div className="stat-mini-item">
            <span className="stat-mini-label">YTD</span>
            <span className="stat-mini-value">
              {formatPercentage(priceStats.ytdChange)}
            </span>
          </div>
        </div>
      </div>
    )}
  </div>
);

const NewsTab: React.FC = () => (
  <div className="news-tab">
    <h3 className="tab-title">Tin tức</h3>
    <p className="tab-subtitle">
      Các tin tức mới nhất về cổ phiếu sẽ được hiển thị tại đây.
    </p>
  </div>
);

export default StockDetailPage;
