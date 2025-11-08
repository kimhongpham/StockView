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

        // Fetch overview data
        const overviewData = await fetchAssetOverview(symbol);
        setOverview(overviewData);

        // Fetch price history for chart (30 days)
        if (overviewData.id) {
          try {
            const historyData = await fetchPriceHistory(overviewData.id, 30);
            // Xử lý các định dạng response khác nhau
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

          // Fetch price stats - bỏ qua nếu có lỗi
          try {
            const statsData = await fetchPriceStats(overviewData.id, "month");
            setPriceStats(statsData);
          } catch (statsError) {
            console.warn(
              "Could not fetch price stats, continuing without stats:",
              statsError
            );
            // Không set error vì stats không quan trọng bằng overview
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

  // Mock function để tạo dữ liệu biểu đồ nếu API không có
  const generateMockChartData = (basePrice: number) => {
    const data = [];
    for (let i = 30; i >= 0; i--) {
      const date = new Date();
      date.setDate(date.getDate() - i);
      const randomVariation = (Math.random() - 0.5) * 0.1; // ±5%
      data.push({
        timestamp: date.toISOString(),
        close: basePrice * (1 + randomVariation),
      });
    }
    return data;
  };

  // Tính toán change24h từ changePercent
  const calculateChange24h = () => {
    if (!overview?.currentPrice || !overview?.changePercent) return 0;
    return (overview.currentPrice * overview.changePercent) / 100;
  };

  // Chuẩn bị dữ liệu cho ChartSection
  const getChartData = () => {
    if (priceHistory.length > 0) {
      return priceHistory.map((item: any) => ({
        timestamp: item.timestamp || item.date,
        close: item.price || item.close,
        open: item.open ?? null,
        high: item.high ?? null,
        low: item.low ?? null,
        volume: item.volume ?? null,
      }));
    }

    // Fallback to mock data if no history
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

  if (loading) return <div className="loading">Loading...</div>;
  if (error) return <div className="error-message">{error}</div>;
  if (!overview) return <div>Không tìm thấy thông tin cổ phiếu</div>;

  return (
    <div className="stock-detail-page">
      <div className="stock-detail-container">
        {/* Header Section */}
        <div className="stock-header">
          <div className="stock-title-section">
            <h1 className="stock-symbol">
              {overview.symbol} <span className="stock-exchange">(NASDAQ)</span>
            </h1>
            <p className="company-name">{overview.name}</p>
            <p className="company-description">{overview.description}</p>
          </div>

          <div className="stock-price-section">
            <div className="current-price">
              {formatPrice(overview.currentPrice)} USD
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
            <div className="price-range">
              <div className="range-item">
                <span className="range-label">Giá thấp nhất</span>
                <span className="range-value">
                  {formatPrice(overview.low24h) || "-"}
                </span>
              </div>
              <div className="range-item">
                <span className="range-label">Giá cao nhất</span>
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
          <div className="stat-item">
            <div className="stat-label">Vốn hóa</div>
            <div className="stat-value">
              {formatCurrency(overview.marketCap, "T")}
            </div>
          </div>
          <div className="stat-item">
            <div className="stat-label">Khối lượng giao dịch</div>
            <div className="stat-value">{formatNumber(overview.volume)}</div>
          </div>
          <div className="stat-item">
            <div className="stat-label">Số lượng cổ phiếu lưu hành</div>
            <div className="stat-value">-</div>
          </div>
          <div className="stat-item">
            <div className="stat-label">P/E</div>
            <div className="stat-value">{formatNumber(overview.peRatio)}</div>
          </div>
          <div className="stat-item">
            <div className="stat-label">P/B</div>
            <div className="stat-value">{formatNumber(overview.pbRatio)}</div>
          </div>
          <div className="stat-item">
            <div className="stat-label">EV/EBITDA</div>
            <div className="stat-value">-</div>
          </div>
          <div className="stat-item">
            <div className="stat-label">EPS</div>
            <div className="stat-value">-</div>
          </div>
          <div className="stat-item">
            <div className="stat-label">Giá trị sổ sách</div>
            <div className="stat-value">-</div>
          </div>
        </div>

        {/* Company Info Section */}
        <div className="company-info-section">
          <div className="info-card">
            <h3 className="info-card-title">Thông tin công ty</h3>
            <div className="info-grid">
              <div className="info-item">
                <span className="info-label">Ngành</span>
                <span className="info-value">Công nghệ</span>
              </div>
              <div className="info-item">
                <span className="info-label">Lĩnh vực</span>
                <span className="info-value">Thiết bị điện tử & Phần mềm</span>
              </div>
              <div className="info-item">
                <span className="info-label">Trạng thái</span>
                <span
                  className={`info-value status ${
                    overview.isActive ? "active" : "inactive"
                  }`}
                >
                  {overview.isActive ? "Đang hoạt động" : "Ngừng hoạt động"}
                </span>
              </div>
              <div className="info-item">
                <span className="info-label">Nguồn dữ liệu</span>
                <span className="info-value">{overview.source}</span>
              </div>
              <div className="info-item">
                <span className="info-label">Cập nhật</span>
                <span className="info-value">
                  {new Date(overview.timestamp).toLocaleDateString("vi-VN")}
                </span>
              </div>
            </div>
          </div>

          <div className="watchlist-card">
            <div className="watchlist-count">
              <span className="count-number">-</span>
              <span className="count-label">watchlists</span>
            </div>
            <button className="watchlist-btn">Theo dõi</button>
          </div>
        </div>

        {/* Tabs Navigation */}
        <div className="tabs-navigation">
          <button
            className={`tab-btn ${activeTab === "overview" ? "active" : ""}`}
            onClick={() => setActiveTab("overview")}
          >
            Tổng quan
          </button>
          <button
            className={`tab-btn ${activeTab === "financials" ? "active" : ""}`}
            onClick={() => setActiveTab("financials")}
          >
            Báo cáo tài chính
          </button>
          <button
            className={`tab-btn ${activeTab === "news" ? "active" : ""}`}
            onClick={() => setActiveTab("news")}
          >
            Tin tức
          </button>
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
            <div className="financials-tab">
              <h3>Báo cáo tài chính</h3>
              <p>Nội dung báo cáo tài chính sẽ được hiển thị tại đây.</p>
              {priceStats && (
                <div className="stats-details">
                  <h4>Thống kê giá</h4>
                  <div className="stats-grid-mini">
                    <div>Giá trung bình: {formatPrice(priceStats.average)}</div>
                    <div>
                      Biến động: {formatPercentage(priceStats.volatility)}
                    </div>
                    <div>YTD: {formatPercentage(priceStats.ytdChange)}</div>
                  </div>
                </div>
              )}
            </div>
          )}

          {activeTab === "news" && (
            <div className="news-tab">
              <h3>Tin tức</h3>
              <p>Các tin tức mới nhất về cổ phiếu sẽ được hiển thị tại đây.</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default StockDetailPage;
