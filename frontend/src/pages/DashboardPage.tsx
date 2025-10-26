import React, { useEffect, useRef, useState } from "react";
import Chart from "chart.js/auto";
import { Asset, PriceDto } from "../types/asset";
import {
  fetchMarketAssets,
  fetchLatestPrice,
  fetchPriceHistory,
} from "../utils/api";
import { formatVolume } from "../utils/formatters";
import { foreignBuyStocks, fundActivities } from "../data/mockData";

const DashboardPage: React.FC = () => {
  const chartRef = useRef<HTMLCanvasElement>(null);
  const chartInstance = useRef<Chart | null>(null);

  const [marketStocks, setMarketStocks] = useState<Asset[]>([]);
  const [latestPrices, setLatestPrices] = useState<Record<string, PriceDto>>({});
  const [selectedStock, setSelectedStock] = useState<string>("VVS");
  const [chartData, setChartData] = useState<number[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  // ---------------- Fetch Market Stocks ----------------
  useEffect(() => {
    const loadMarketStocks = async () => {
      try {
        const data = await fetchMarketAssets("stocks");
        setMarketStocks(data);
      } catch (err) {
        console.error("Failed to fetch market stocks:", err);
      }
    };
    loadMarketStocks();
  }, []);

  // ---------------- Fetch Latest Prices ----------------
  useEffect(() => {
    if (!marketStocks.length) return;

    const loadLatestPrices = async () => {
      try {
        const results = await Promise.all(
          marketStocks.map((s) =>
            fetchLatestPrice(s.id).then((price) => [s.symbol, price])
          )
        );
        setLatestPrices(Object.fromEntries(results));
      } catch (err) {
        console.error("Failed to fetch latest prices:", err);
      }
    };
    loadLatestPrices();
  }, [marketStocks]);

  // ---------------- Fetch Price History ----------------
  useEffect(() => {
    const asset = marketStocks.find((s) => s.symbol === selectedStock);
    if (!asset) return;

    const loadChartData = async () => {
      try {
        const history = await fetchPriceHistory(asset.id, 30);
        setChartData(history.map((h) => h.price));
      } catch (err) {
        console.error("Failed to fetch price history:", err);
      } finally {
        setLoading(false);
      }
    };

    loadChartData();
  }, [selectedStock, marketStocks]);

  // ---------------- Render Chart ----------------
  useEffect(() => {
    if (!chartRef.current || chartData.length === 0) return;

    if (chartInstance.current) chartInstance.current.destroy();

    chartInstance.current = new Chart(chartRef.current, {
      type: "line",
      data: {
        labels: chartData.map((_, i) => `Day ${i + 1}`),
        datasets: [
          {
            label: selectedStock,
            data: chartData,
            borderColor: "#10b981",
            backgroundColor: "rgba(16, 185, 129, 0.1)",
            fill: true,
            tension: 0.4,
            pointBackgroundColor: "#10b981",
            pointBorderColor: "#fff",
            pointBorderWidth: 2,
            pointRadius: 3,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: { mode: "index", intersect: false },
        },
        interaction: { mode: "nearest", axis: "x", intersect: false },
        scales: {
          x: { grid: { color: "rgba(0, 0, 0, 0.05)" } },
          y: { grid: { color: "rgba(0, 0, 0, 0.05)" }, beginAtZero: false },
        },
      },
    });

    return () => chartInstance.current?.destroy();
  }, [chartData, selectedStock]);

  // ---------------- Compute Top Gainers / Losers ----------------
  const stocksWithPrices = marketStocks.filter((s) => latestPrices[s.symbol]);

  const topGainers = [...stocksWithPrices]
    .sort(
      (a, b) =>
        (latestPrices[b.symbol].changePercent || 0) -
        (latestPrices[a.symbol].changePercent || 0)
    )
    .slice(0, 5);

  const topLosers = [...stocksWithPrices]
    .sort(
      (a, b) =>
        (latestPrices[a.symbol].changePercent || 0) -
        (latestPrices[b.symbol].changePercent || 0)
    )
    .slice(0, 5);

  const handleSelectStock = (symbol: string) => setSelectedStock(symbol);

  return (
    <div className="page active" id="dashboard">
      <h1 className="page-title">Tổng quan thị trường</h1>

      {/* Chart Section */}
      <div className="chart-container" style={{ height: "300px", marginBottom: "20px" }}>
        {loading ? <p>Đang tải biểu đồ...</p> : <canvas ref={chartRef}></canvas>}
      </div>

      {/* Dashboard Layout */}
      <div className="dashboard-layout market-indices-grid expanded">
        {/* Top Gainers */}
        <div className="dashboard-column">
          <h2>Top tăng giá</h2>
          <table className="data-table">
            <thead>
              <tr>
                <th>Mã</th>
                <th>% thay đổi</th>
                <th>Giá hiện tại</th>
              </tr>
            </thead>
            <tbody>
              {topGainers.map((stock) => {
                const price = latestPrices[stock.symbol];
                return (
                  <tr
                    key={stock.id}
                    className="clickable-row"
                    onClick={() => handleSelectStock(stock.symbol)}
                  >
                    <td>{stock.symbol}</td>
                    <td className="positive">{price?.changePercent ?? 0}%</td>
                    <td>{price?.price ?? "-"}</td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>

        {/* Top Losers */}
        <div className="dashboard-column">
          <h2>Top giảm giá</h2>
          <table className="data-table">
            <thead>
              <tr>
                <th>Mã</th>
                <th>% thay đổi</th>
                <th>Giá hiện tại</th>
              </tr>
            </thead>
            <tbody>
              {topLosers.map((stock) => {
                const price = latestPrices[stock.symbol];
                return (
                  <tr
                    key={stock.id}
                    className="clickable-row"
                    onClick={() => handleSelectStock(stock.symbol)}
                  >
                    <td>{stock.symbol}</td>
                    <td className="negative">{price?.changePercent ?? 0}%</td>
                    <td>{price?.price ?? "-"}</td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>

        {/* Foreign Buy */}
        <div className="dashboard-column">
          <h2>Top khối ngoại mua ròng</h2>
          <table className="data-table">
            <thead>
              <tr>
                <th>Mã</th>
                <th>Giá trị</th>
              </tr>
            </thead>
            <tbody>
              {foreignBuyStocks.map((item) => (
                <tr key={item.code}>
                  <td>{item.code}</td>
                  <td>{formatVolume(item.netBuy)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Fund Activities */}
        <div className="dashboard-column">
          <h2>Hoạt động quỹ</h2>
          <table className="data-table">
            <thead>
              <tr>
                <th>Mã</th>
                <th>Quỹ</th>
                <th>Số lượng</th>
              </tr>
            </thead>
            <tbody>
              {fundActivities.map((item) => (
                <tr key={item.code}>
                  <td>{item.code}</td>
                  <td>{item.fund}</td>
                  <td>{formatVolume(item.quantity)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default DashboardPage;
