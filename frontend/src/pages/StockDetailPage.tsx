import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import {
  fetchAssetDetails,
  fetchCompanyInfo,
  fetchLatestPrice,
  fetchStats,
  Asset,
  LatestPrice,
  ChartPoint,
  StatsResponse,
} from "../utils/api";
import ChartSection from "../components/ChartSection";
import "../styles/StockDetailPage.css";

const StockDetailPage: React.FC = () => {
  const { symbol } = useParams<{ symbol: string }>();

  const [assetInfo, setAssetInfo] = useState<Asset | null>(null);
  const [companyInfo, setCompanyInfo] = useState<any>(null);
  const [latestPrice, setLatestPrice] = useState<LatestPrice | null>(null);
  const [stats, setStats] = useState<StatsResponse | null>(null);
  const [chartData, setChartData] = useState<ChartPoint[]>([]);
  const [activeTab, setActiveTab] = useState<"overview" | "financials" | "news">("overview");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!symbol) return;
    (async () => {
      try {
        const asset = await fetchAssetDetails(symbol);
        setAssetInfo(asset);

        const company = await fetchCompanyInfo(symbol);
        setCompanyInfo(company);

        const latest = await fetchLatestPrice(asset.id);
        setLatestPrice(latest);

        const stat = await fetchStats(asset.id);
        setStats(stat);

      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    })();
  }, [symbol]);

  const formatNumber = (num: number | undefined | null) =>
    num != null ? num.toLocaleString("vi-VN") : "-";
  const formatPrice = (price: number | undefined | null) =>
    price != null ? formatNumber(price * 1000) : "-";

  if (loading || !assetInfo || !latestPrice) return <div>Loading...</div>;

  return (
    <div className="stock-detail-page">
      <div className="stock-detail-container">
        {/* Header */}
        <div className="stock-header">
          <div className="stock-title-section">
            <h1 className="stock-symbol">
              {assetInfo.symbol} <span className="stock-exchange">({companyInfo?.exchange || "-"})</span>
            </h1>
            <p className="company-name">{companyInfo?.companyName || assetInfo.name}</p>
          </div>
          <div className="stock-price-section">
            <div className="current-price">{formatPrice(latestPrice.price)} USD</div>
            <div className={`price-change ${latestPrice.changePercent24h! >= 0 ? "positive" : "negative"}`}>
              {latestPrice.changePercent24h! >= 0 ? "+" : ""}
              {formatPrice(latestPrice.price * (latestPrice.changePercent24h! / 100))}
              ({latestPrice.changePercent24h!.toFixed(2)}%)
            </div>
          </div>
        </div>

        {/* Info Cards */}
        <div className="stock-info-grid">
          <div className="info-card"><div className="info-label">Ngành</div><div className="info-value">{companyInfo?.sector || "-"}</div></div>
          <div className="info-card"><div className="info-label">Lĩnh vực</div><div className="info-value">{companyInfo?.industry || "-"}</div></div>
          <div className="info-card"><div className="info-label">Khối lượng</div><div className="info-value">{formatNumber(latestPrice.volume)}</div></div>
          <div className="info-card"><div className="info-label">Vốn hóa</div><div className="info-value">{formatNumber(stats?.marketCap)}</div></div>
          <div className="info-card"><div className="info-label">P/E</div><div className="info-value">{formatNumber(stats?.pe)}</div></div>
          <div className="info-card"><div className="info-label">P/B</div><div className="info-value">{formatNumber(stats?.pb)}</div></div>
          <div className="info-card"><div className="info-label">Cổ tức</div><div className="info-value">{formatNumber(stats?.dividendYield)}%</div></div>
        </div>

        {/* Tabs */}
        <div className="tabs-navigation">
          <button className={`tab-btn ${activeTab === "overview" ? "active" : ""}`} onClick={() => setActiveTab("overview")}>Tổng quan</button>
          <button className={`tab-btn ${activeTab === "financials" ? "active" : ""}`} onClick={() => setActiveTab("financials")}>Báo cáo tài chính</button>
          <button className={`tab-btn ${activeTab === "news" ? "active" : ""}`} onClick={() => setActiveTab("news")}>Tin tức</button>
        </div>
      </div>
    </div>
  );
};

export default StockDetailPage;
