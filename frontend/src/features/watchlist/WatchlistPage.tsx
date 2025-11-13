import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import AssetTable from "../../components/tables/AssetTable";
import api from "../../utils/api";
import { useAuthStore } from "../../store/authStore";
import { fetchPriceChart } from "../../utils/api";

interface WatchlistRow {
  id: string;
  symbol: string;
  name: string;
  latestPrice?: number | null;
  change24h?: number | null;
  volume?: number | null;
  pe?: number | null;
  pb?: number | null;
  chart30d?: { timestamp: number; price: number }[] | null;
}

const WatchlistPage: React.FC = () => {
  const { user: authUser } = useAuthStore();
  const [rows, setRows] = useState<WatchlistRow[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filter] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const loadData = async () => {
      if (!authUser?.token) {
        setError("Bạn chưa đăng nhập");
        setLoading(false);
        return;
      }

      try {
        // 1. Lấy danh sách symbol trong watchlist
        const res = await api.get("/users/watchlist", {
          headers: { Authorization: `Bearer ${authUser.token}` },
        });

        const symbols: string[] = res.data?.data || [];

        // 2. Lấy overview + 30D chart
        const enriched: WatchlistRow[] = await Promise.all(
          symbols.map(async (symbol) => {
            try {
              const overviewRes = await api.get(`/assets/${symbol}/overview`);
              const o = overviewRes.data;

              // Lấy dữ liệu 30D
              let chart30d: { timestamp: number; price: number }[] = [];
              try {
                const chartDataRaw = await fetchPriceChart(symbol, "1d", 30);
                chart30d = chartDataRaw.map(
                  (c: { timestamp: number; price: number }) => ({
                    timestamp: c.timestamp,
                    price: c.price,
                  })
                );
              } catch (err) {
                console.warn(`Không lấy được chart 30D cho ${symbol}`);
              }

              return {
                id: o.id,
                symbol: o.symbol,
                name: o.name,
                latestPrice: o.currentPrice ?? null,
                change24h: o.changePercent ?? null,
                volume: o.volume ?? null,
                pe: o.peRatio ?? null,
                pb: o.pbRatio ?? null,
                chart30d,
              };
            } catch (err) {
              console.warn(`Không lấy được thông tin cho ${symbol}`, err);
              return {
                id: symbol,
                symbol,
                name: "",
                latestPrice: null,
                change24h: null,
                volume: null,
                pe: null,
                pb: null,
                chart30d: [],
              };
            }
          })
        );

        setRows(enriched);
      } catch (err: any) {
        console.error("Error loading watchlist:", err);
        setError("Không thể tải danh sách theo dõi.");
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [authUser?.token]);

  const visibleRows = rows.filter((r) => {
    const q = filter.trim().toLowerCase();
    if (!q) return true;
    return (
      (r.symbol ?? "").toLowerCase().includes(q) ||
      (r.name ?? "").toLowerCase().includes(q)
    );
  });

  return (
    <div className="page active" id="watchlist">
      <h1 className="page-title font-bold text-2xl mb-4">Danh Mục Theo Dõi</h1>

      <div className="flex items-center gap-3 mb-4">
        {loading ? (
          <div className="flex items-center gap-2 text-gray-500">
            <div className="spinner" /> Đang tải dữ liệu...
          </div>
        ) : error ? (
          <div className="text-red-600">{error}</div>
        ) : null}
      </div>

      <AssetTable
        rows={visibleRows}
        showChart={true} // Hiển thị chart 30D
        showStar={false} // Watchlist không cần star
        onRowClick={(symbol) => navigate(`/stocks/${symbol}`)}
      />

      <style>{`
        .spinner {
          width: 16px;
          height: 16px;
          border-radius: 50%;
          border: 2px solid rgba(0,0,0,0.1);
          border-top-color: rgba(0,0,0,0.5);
          animation: spin 1s linear infinite;
        }
        @keyframes spin { to { transform: rotate(360deg); } }
      `}</style>
    </div>
  );
};

export default WatchlistPage;
