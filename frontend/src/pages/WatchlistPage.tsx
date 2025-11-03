import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import AssetTable from "../components/AssetTable";

interface WatchlistRow {
  id: string;
  symbol: string;
  name?: string;
  latestPrice?: number | null;
  change24h?: number | null;
  volume?: number | null;
  pe?: number | null;
  pb?: number | null;
  chart30d?: any[] | null;
}

const WatchlistPage: React.FC = () => {
  const [rows, setRows] = useState<WatchlistRow[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filter, setFilter] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const loadData = async () => {
      try {
        const res = await axios.get("/api/watchlist");
        const assets = Array.isArray(res.data)
          ? res.data
          : res.data.content || [];

        const enriched = await Promise.all(
          assets.map(async (a) => {
            try {
              const [latest, change] = await Promise.all([
                axios.get(`/api/prices/${a.id}/latest`),
                axios.get(`/api/prices/${a.id}/change`),
              ]);
              return {
                ...a,
                latestPrice: latest.data.price,
                change24h: change.data.percentChange,
                chart30d: [], // tạm thời để trống
              };
            } catch {
              return { ...a, latestPrice: null, change24h: null, chart30d: [] };
            }
          })
        );

        setRows(enriched);
      } catch (err) {
        console.error("Error loading watchlist:", err);
        setError("Không thể tải danh sách theo dõi.");
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, []);

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
      <h1 className="page-title font-bold text-2xl mb-4">
        Danh Mục Theo Dõi
      </h1>

      <div className="flex items-center gap-3 mb-4">
        <div className="ml-auto">
          {loading ? (
            <div className="flex items-center gap-2 text-gray-500">
              <div className="spinner" /> Đang tải dữ liệu...
            </div>
          ) : error ? (
            <div className="text-red-600">{error}</div>
          ) : null}
        </div>
      </div>

      <AssetTable
        rows={visibleRows}
        showChart={true} // có cột biểu đồ 30D
        showStar={false}
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
