import React, { useEffect, useState } from "react";
import ChartSection from "../../components/charts/ChartSection";
import StockTable from "../../components/tables/StockTable";
import { Asset, LatestPrice, ChartPoint } from "../../types/asset";
import {
  fetchLatestPrice,
  fetchPriceChart,
  fetchTopPrices,
} from "../../utils/api";

const DashboardPage: React.FC = () => {
  const [marketStocks] = useState<Asset[]>([]);
  const [, setLatestPrices] = useState<Record<string, LatestPrice>>(
    {}
  );
  const [selectedStock, setSelectedStock] = useState<string>("VVS");
  const [chartData, setChartData] = useState<ChartPoint[]>([]);
  const [loading, setLoading] = useState(true);

  // 🔹 Lấy giá mới nhất cho tất cả cổ phiếu
  useEffect(() => {
    if (!marketStocks.length) return;
    (async () => {
      const results = await Promise.all(
        marketStocks.map((s) =>
          fetchLatestPrice(s.id).then((p) => [s.symbol, p])
        )
      );
      setLatestPrices(Object.fromEntries(results));
    })();
  }, [marketStocks]);

  const [topGainers, setTopGainers] = useState<any[]>([]);
  const [topLosers, setTopLosers] = useState<any[]>([]);

  // Biểu đồ cho cổ phiếu đang chọn
  useEffect(() => {
    // Tìm asset trong tất cả danh sách (market + top gainers + top losers)
    const allAssets = [
      ...marketStocks,
      ...topGainers.map((g) => ({
        id: g.assetId,
        symbol: g.assetSymbol,
        name: g.assetName,
      })),
      ...topLosers.map((l) => ({
        id: l.assetId,
        symbol: l.assetSymbol,
        name: l.assetName,
      })),
    ];

    const asset = allAssets.find((s) => s.symbol === selectedStock);
    if (!asset) return;

    (async () => {
      try {
        setLoading(true);
        const chart = await fetchPriceChart(asset.id);
        setChartData(chart);
      } catch (err) {
        console.error("Lỗi lấy biểu đồ:", err);
      } finally {
        setLoading(false);
      }
    })();
  }, [selectedStock, marketStocks, topGainers, topLosers]);

  useEffect(() => {
    (async () => {
      try {
        const [gainers, losers] = await Promise.all([
          fetchTopPrices("gainers"),
          fetchTopPrices("losers"),
        ]);
        setTopGainers(gainers);
        setTopLosers(losers);

        // 🔹 Chọn cổ phiếu đầu tiên của top gainers làm mặc định
        if (gainers.length > 0) {
          setSelectedStock(gainers[0].assetSymbol);
        }
      } catch (err) {
        console.error("Lỗi lấy top giá:", err);
      }
    })();
  }, []);

  return (
    <div className="page active" id="dashboard">
      <h1 className="text-2xl font-bold mb-4">Tổng Quan Thị Trường</h1>

      {/* Giữ nguyên biểu đồ */}
      <ChartSection
        data={chartData}
        selectedStock={selectedStock}
        loading={loading}
      />

      {/* Gọi dữ liệu top tăng / giảm */}
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "1fr 1fr",
          gap: 20,
          marginTop: 20,
        }}
      >
        <StockTable
          title="Top tăng giá"
          stocks={topGainers.map((p) => ({
            id: p.assetId,
            symbol: p.assetSymbol,
            name: p.assetName,
          }))}
          prices={Object.fromEntries(
            topGainers.map((p) => [
              p.assetId,
              {
                price: p.price,
                changePercent: p.changePercent,
                volume: p.volume,
                marketCap: p.marketCap,
              },
            ])
          )}
          onSelect={setSelectedStock}
        />

        <StockTable
          title="Top giảm giá"
          stocks={topLosers.map((p) => ({
            id: p.assetId,
            symbol: p.assetSymbol,
            name: p.assetName,
          }))}
          prices={Object.fromEntries(
            topLosers.map((p) => [
              p.assetId,
              {
                price: p.price,
                changePercent: p.changePercent,
                volume: p.volume,
                marketCap: p.marketCap,
              },
            ])
          )}
          onSelect={setSelectedStock}
        />
      </div>
    </div>
  );
};

export default DashboardPage;
