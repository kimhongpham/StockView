import axios from "axios";
import React, { useEffect, useState } from "react";
import MiniChart from "../components/MiniChart";

interface StockRow {
  id: string;
  symbol: string;
  name?: string;
  latestPrice?: number | null;
  change24h?: number | null;
  volume?: number | null;
  change7d?: number | null;
  marketCap?: number | null;
  pe?: number | null;
  pb?: number | null;
  chart30d?: any[] | null;
}

export const StockPage: React.FC = () => {
  const [rows, setRows] = useState<StockRow[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filter, setFilter] = useState("");

  useEffect(() => {
    const loadData = async () => {
      try {
        const assetRes = await axios.get("/api/assets");
        console.log("assets response:", assetRes.data);

        const assets = Array.isArray(assetRes.data)
          ? assetRes.data
          : assetRes.data.content || [];

        const enrichedAssets = await Promise.all(
          assets.map(async (a) => {
            try {
              const [latest, change, stats, chart] = await Promise.all([
                axios.get(`/api/prices/${a.id}/latest`),
                axios.get(`/api/prices/${a.id}/change`),
                axios.get(`/api/prices/${a.id}/stats`),
                axios.get(`/api/prices/${a.id}/chart`),
              ]);

              return {
                ...a,
                latestPrice: latest.data.price,
                change24h: change.data.percentChange, // 🔹 đổi cho đúng field
                change7d: stats.data.change7d,
                chart30d: Array.isArray(chart.data) ? chart.data : [], // 🔹 tránh lỗi .map
              };
            } catch (err) {
              console.error("Error loading asset data:", err);
              return { ...a, latestPrice: null, change24h: null, chart30d: [] };
            }
          })
        );

        setRows(enrichedAssets);
      } catch (err) {
        console.error("Error loading stocks:", err);
        setError("Không thể tải dữ liệu cổ phiếu.");
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, []);

  const formatNumber = (n?: number | null) => {
    if (n == null || Number.isNaN(n)) return "—";
    return n.toLocaleString();
  };

  const formatPrice = (p?: number | null) => {
    if (p == null || Number.isNaN(p)) return "—";
    return p < 1000 ? p.toFixed(2) : Math.round(p).toLocaleString();
  };

  const getChangeClass = (v?: number | null) => {
    if (v == null) return "";
    return v >= 0 ? "positive" : "negative";
  };

  const visibleRows = rows.filter((r) => {
    const q = filter.trim().toLowerCase();
    if (!q) return true;
    return (
      (r.symbol ?? "").toLowerCase().includes(q) ||
      (r.name ?? "").toLowerCase().includes(q)
    );
  });

  return (
    <div className="page active" id="stock">
      <h1 className="page-title">Danh Mục Quan Sát</h1>

      <div
        style={{
          display: "flex",
          gap: 12,
          marginBottom: 12,
          alignItems: "center",
        }}
      >
        <input
          placeholder="Tìm kiếm mã hoặc tên..."
          value={filter}
          onChange={(e) => setFilter(e.target.value)}
          style={{ padding: "8px 10px", minWidth: 240 }}
        />
        <div style={{ marginLeft: "auto" }}>
          {loading ? (
            <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
              <div className="spinner" aria-hidden />
              <span>Đang tải dữ liệu...</span>
            </div>
          ) : error ? (
            <div style={{ color: "red" }}>{error}</div>
          ) : null}
        </div>
      </div>

      <div className="table-container" style={{ overflowX: "auto" }}>
        <table
          className="data-table stock-table"
          style={{ width: "100%", borderCollapse: "collapse" }}
        >
          <thead>
            <tr>
              <th style={{ textAlign: "left", padding: "8px 12px" }}>
                Cổ phiếu
              </th>
              <th style={{ padding: "8px 12px" }}>Giá hiện tại</th>
              <th style={{ padding: "8px 12px" }}>Biến động % (24h)</th>
              <th style={{ padding: "8px 12px" }}>KLGD</th>
              <th style={{ padding: "8px 12px" }}>Thay đổi 7D</th>
              <th style={{ padding: "8px 12px" }}>Vốn hóa</th>
              <th style={{ padding: "8px 12px" }}>P/E</th>
              <th style={{ padding: "8px 12px" }}>P/B</th>
              <th style={{ padding: "8px 12px" }}>Biểu đồ 30D</th>
            </tr>
          </thead>
          <tbody>
            {visibleRows.length === 0 && !loading && (
              <tr>
                <td colSpan={9} style={{ textAlign: "center", padding: 20 }}>
                  Không có cổ phiếu nào.
                </td>
              </tr>
            )}
            {visibleRows.map((r) => (
              <tr key={r.id} style={{ borderTop: "1px solid #eee" }}>
                <td style={{ padding: "10px 12px" }}>
                  <div style={{ fontWeight: 600 }}>{r.symbol}</div>
                  <div style={{ fontSize: 12, color: "#666" }}>{r.name}</div>
                </td>
                <td style={{ textAlign: "right" }}>
                  {formatPrice(r.latestPrice)}
                </td>
                <td style={{ textAlign: "right" }}>
                  <span
                    className={getChangeClass(r.change24h)}
                    style={{ fontWeight: 600 }}
                  >
                    {r.change24h == null
                      ? "—"
                      : `${r.change24h >= 0 ? "+" : ""}${r.change24h}%`}
                  </span>
                </td>
                <td style={{ textAlign: "right" }}>{formatNumber(r.volume)}</td>
                <td style={{ textAlign: "right" }}>
                  <span
                    className={getChangeClass(r.change7d)}
                    style={{ fontWeight: 600 }}
                  >
                    {r.change7d == null
                      ? "—"
                      : `${r.change7d >= 0 ? "+" : ""}${r.change7d}%`}
                  </span>
                </td>
                <td style={{ textAlign: "right" }}>
                  {formatNumber(r.marketCap)}
                </td>
                <td style={{ textAlign: "right" }}>
                  {r.pe?.toFixed(2) ?? "—"}
                </td>
                <td style={{ textAlign: "right" }}>
                  {r.pb?.toFixed(2) ?? "—"}
                </td>
                <td style={{ padding: "10px 12px", width: 160 }}>
                  <div style={{ width: 140, height: 60 }}>
                    {r.chart30d && (
                      <MiniChart
                        data={r.chart30d.map((p: any) => ({
                          timestamp: p.timestamp,
                          price: p.price,
                        }))}
                      />
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <style>{`
        .spinner {
          width: 16px;
          height: 16px;
          border-radius: 50%;
          border: 2px solid rgba(0,0,0,0.12);
          border-top-color: rgba(0,0,0,0.6);
          animation: spin 1s linear infinite;
        }
        @keyframes spin { to { transform: rotate(360deg) } }
        .positive { color: #16a34a; }
        .negative { color: #dc2626; }
      `}</style>
    </div>
  );
};

export default StockPage;
