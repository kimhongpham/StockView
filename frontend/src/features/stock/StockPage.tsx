import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import AssetTable from "../../components/tables/AssetTable";

interface AssetOverview {
  id: string;
  symbol: string;
  name: string;
  currentPrice?: number;
  latestPrice?: number;
  changePercent?: number;
  change24h?: number;
  peRatio?: number;
  pbRatio?: number;
  pe?: number;
  pb?: number;
  volume?: number | null;
  chart30d?: number[];
}

const StockPage: React.FC = () => {
  const [sortBy, setSortBy] = useState<string>("");
  const [sortOrder, setSortOrder] = useState<"asc" | "desc">("asc");
  const [assets, setAssets] = useState<AssetOverview[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(1);
  const [showFilters, setShowFilters] = useState(false);
  const pageSize = 20;
  const navigate = useNavigate();

  // Filter states
  const [symbolFilter, setSymbolFilter] = useState("");
  const [nameFilter, setNameFilter] = useState("");
  const [priceMin, setPriceMin] = useState("");
  const [priceMax, setPriceMax] = useState("");
  const [changeMin, setChangeMin] = useState("");
  const [changeMax, setChangeMax] = useState("");
  const [volumeMin, setVolumeMin] = useState("");
  const [volumeMax, setVolumeMax] = useState("");
  const [peMin, setPeMin] = useState("");
  const [peMax, setPeMax] = useState("");
  const [pbMin, setPbMin] = useState("");
  const [pbMax, setPbMax] = useState("");

  useEffect(() => {
    const loadData = async () => {
      try {
        const res = await axios.get("/api/assets");
        const assetList = Array.isArray(res.data)
          ? res.data
          : res.data.content || [];

        const detailedAssets = await Promise.all(
          assetList.map(async (a: any) => {
            try {
              const overviewRes = await axios.get(
                `/api/assets/${a.symbol}/overview`
              );
              const o = overviewRes.data;

              return {
                id: o.id,
                symbol: o.symbol,
                name: o.name,
                latestPrice: o.currentPrice ?? 0,
                change24h: o.changePercent ?? 0,
                volume: o.volume ?? 0,
                pe: o.peRatio ?? null,
                pb: o.pbRatio ?? null,
                chart30d: o.chart30d ?? [],
              };
            } catch (err) {
              console.warn(`⚠️ Không lấy được overview cho ${a.symbol}`);
              return {
                ...a,
                latestPrice: 0,
                change24h: 0,
                pe: null,
                pb: null,
                chart30d: [],
              };
            }
          })
        );

        setAssets(detailedAssets);
      } catch (err) {
        console.error("❌ Error loading assets:", err);
        setError("Không thể tải danh sách cổ phiếu.");
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, []);

  // Filter logic
  let visibleAssets = assets.filter((a) => {
    if (symbolFilter && !a.symbol.toLowerCase().includes(symbolFilter.trim().toLowerCase())) return false;
    if (nameFilter && !(a.name ?? "").toLowerCase().includes(nameFilter.trim().toLowerCase())) return false;
    if (priceMin && (a.latestPrice ?? 0) < Number(priceMin)) return false;
    if (priceMax && (a.latestPrice ?? 0) > Number(priceMax)) return false;
    if (changeMin && (a.change24h ?? 0) < Number(changeMin)) return false;
    if (changeMax && (a.change24h ?? 0) > Number(changeMax)) return false;
    if (volumeMin && (a.volume ?? 0) < Number(volumeMin)) return false;
    if (volumeMax && (a.volume ?? 0) > Number(volumeMax)) return false;
    if (peMin && (a.pe ?? 0) < Number(peMin)) return false;
    if (peMax && (a.pe ?? 0) > Number(peMax)) return false;
    if (pbMin && (a.pb ?? 0) < Number(pbMin)) return false;
    if (pbMax && (a.pb ?? 0) > Number(pbMax)) return false;
    return true;
  });

  // Sort logic
  if (sortBy) {
    visibleAssets = [...visibleAssets].sort((a, b) => {
      let va: string | number = "";
      let vb: string | number = "";
      switch (sortBy) {
        case "symbol":
          va = a.symbol;
          vb = b.symbol;
          break;
        case "name":
          va = a.name ?? "";
          vb = b.name ?? "";
          break;
        case "latestPrice":
          va = a.latestPrice ?? 0;
          vb = b.latestPrice ?? 0;
          break;
        case "change24h":
          va = a.change24h ?? 0;
          vb = b.change24h ?? 0;
          break;
        case "volume":
          va = a.volume ?? 0;
          vb = b.volume ?? 0;
          break;
        case "pe":
          va = a.pe ?? 0;
          vb = b.pe ?? 0;
          break;
        case "pb":
          va = a.pb ?? 0;
          vb = b.pb ?? 0;
          break;
        default:
          va = 0;
          vb = 0;
      }
      if (typeof va === "string" && typeof vb === "string") {
        return sortOrder === "asc" ? va.localeCompare(vb) : vb.localeCompare(va);
      }
      if (typeof va === "number" && typeof vb === "number") {
        return sortOrder === "asc" ? va - vb : vb - va;
      }
      return 0;
    });
  }

  // Pagination
  const totalPages = Math.ceil(visibleAssets.length / pageSize);
  const pagedAssets = visibleAssets.slice((page - 1) * pageSize, page * pageSize);

  // Reset all filters
  const resetFilters = () => {
    setSymbolFilter("");
    setNameFilter("");
    setPriceMin("");
    setPriceMax("");
    setChangeMin("");
    setChangeMax("");
    setVolumeMin("");
    setVolumeMax("");
    setPeMin("");
    setPeMax("");
    setPbMin("");
    setPbMax("");
    setPage(1);
  };

  return (
    <div className="page active" id="stocks">
      <h1 className="page-title font-bold text-2xl mb-4">Tất cả cổ phiếu</h1>

      {/* Filter Header */}
      <div className="filter-header" style={{ 
        display: "flex", 
        justifyContent: "space-between", 
        alignItems: "center", 
        marginBottom: "16px",
        flexWrap: "wrap",
        gap: "12px"
      }}>
        <div className="filter-controls" style={{ display: "flex", gap: "12px", alignItems: "center" }}>
          <button
            onClick={() => setShowFilters(!showFilters)}
            className="filter-toggle"
            style={{
              padding: "8px 16px",
              border: "1px solid #d1d5db",
              borderRadius: "6px",
              background: "#fff",
              cursor: "pointer",
              display: "flex",
              alignItems: "center",
              gap: "8px",
              fontSize: "14px"
            }}
          >
            <span>🔍</span>
            {showFilters ? "Ẩn bộ lọc" : "Hiện bộ lọc"}
          </button>
          
          <button
            onClick={resetFilters}
            className="reset-filters"
            style={{
              padding: "8px 16px",
              border: "1px solid #d1d5db",
              borderRadius: "6px",
              background: "#fff",
              cursor: "pointer",
              fontSize: "14px",
              color: "#6b7280"
            }}
          >
            Xóa bộ lọc
          </button>
        </div>

        <div className="filter-stats" style={{ 
          fontSize: "14px", 
          color: "#6b7280",
          textAlign: "right"
        }}>
          Hiển thị {pagedAssets.length} / {visibleAssets.length} mã
        </div>
      </div>

      {/* Filter Panel */}
      {showFilters && (
        <div className="filter-panel" style={{
          background: "#f8fafc",
          border: "1px solid #e2e8f0",
          borderRadius: "8px",
          padding: "20px",
          marginBottom: "20px"
        }}>
          <div className="filter-grid" style={{
            display: "grid",
            gridTemplateColumns: "repeat(auto-fit, minmax(280px, 1fr))",
            gap: "16px"
          }}>
            {/* Text Filters */}
            <div className="filter-group">
              <label style={{ display: "block", marginBottom: "8px", fontSize: "14px", fontWeight: "500" }}>
                Mã cổ phiếu
              </label>
              <input
                value={symbolFilter}
                onChange={e => { setSymbolFilter(e.target.value); setPage(1); }}
                placeholder="Nhập mã..."
                style={inputStyle}
              />
            </div>

            <div className="filter-group">
              <label style={{ display: "block", marginBottom: "8px", fontSize: "14px", fontWeight: "500" }}>
                Tên công ty
              </label>
              <input
                value={nameFilter}
                onChange={e => { setNameFilter(e.target.value); setPage(1); }}
                placeholder="Nhập tên..."
                style={inputStyle}
              />
            </div>

            {/* Price Range */}
            <div className="filter-group">
              <label style={{ display: "block", marginBottom: "8px", fontSize: "14px", fontWeight: "500" }}>
                Giá (VND)
              </label>
              <div style={{ display: "flex", gap: "8px" }}>
                <input
                  value={priceMin}
                  onChange={e => { setPriceMin(e.target.value); setPage(1); }}
                  type="number"
                  placeholder="Từ"
                  style={{ ...inputStyle, flex: 1 }}
                />
                <input
                  value={priceMax}
                  onChange={e => { setPriceMax(e.target.value); setPage(1); }}
                  type="number"
                  placeholder="Đến"
                  style={{ ...inputStyle, flex: 1 }}
                />
              </div>
            </div>

            {/* Change Range */}
            <div className="filter-group">
              <label style={{ display: "block", marginBottom: "8px", fontSize: "14px", fontWeight: "500" }}>
                Biến động (%)
              </label>
              <div style={{ display: "flex", gap: "8px" }}>
                <input
                  value={changeMin}
                  onChange={e => { setChangeMin(e.target.value); setPage(1); }}
                  type="number"
                  placeholder="Từ"
                  style={{ ...inputStyle, flex: 1 }}
                />
                <input
                  value={changeMax}
                  onChange={e => { setChangeMax(e.target.value); setPage(1); }}
                  type="number"
                  placeholder="Đến"
                  style={{ ...inputStyle, flex: 1 }}
                />
              </div>
            </div>

            {/* Volume Range */}
            <div className="filter-group">
              <label style={{ display: "block", marginBottom: "8px", fontSize: "14px", fontWeight: "500" }}>
                Khối lượng GD
              </label>
              <div style={{ display: "flex", gap: "8px" }}>
                <input
                  value={volumeMin}
                  onChange={e => { setVolumeMin(e.target.value); setPage(1); }}
                  type="number"
                  placeholder="Từ"
                  style={{ ...inputStyle, flex: 1 }}
                />
                <input
                  value={volumeMax}
                  onChange={e => { setVolumeMax(e.target.value); setPage(1); }}
                  type="number"
                  placeholder="Đến"
                  style={{ ...inputStyle, flex: 1 }}
                />
              </div>
            </div>

            {/* P/E Range */}
            <div className="filter-group">
              <label style={{ display: "block", marginBottom: "8px", fontSize: "14px", fontWeight: "500" }}>
                P/E
              </label>
              <div style={{ display: "flex", gap: "8px" }}>
                <input
                  value={peMin}
                  onChange={e => { setPeMin(e.target.value); setPage(1); }}
                  type="number"
                  placeholder="Từ"
                  style={{ ...inputStyle, flex: 1 }}
                />
                <input
                  value={peMax}
                  onChange={e => { setPeMax(e.target.value); setPage(1); }}
                  type="number"
                  placeholder="Đến"
                  style={{ ...inputStyle, flex: 1 }}
                />
              </div>
            </div>

            {/* P/B Range */}
            <div className="filter-group">
              <label style={{ display: "block", marginBottom: "8px", fontSize: "14px", fontWeight: "500" }}>
                P/B
              </label>
              <div style={{ display: "flex", gap: "8px" }}>
                <input
                  value={pbMin}
                  onChange={e => { setPbMin(e.target.value); setPage(1); }}
                  type="number"
                  placeholder="Từ"
                  style={{ ...inputStyle, flex: 1 }}
                />
                <input
                  value={pbMax}
                  onChange={e => { setPbMax(e.target.value); setPage(1); }}
                  type="number"
                  placeholder="Đến"
                  style={{ ...inputStyle, flex: 1 }}
                />
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Loading and Error States */}
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

      {/* Asset Table */}
      <AssetTable
        rows={pagedAssets}
        showChart={false}
        showStar={true}
        onRowClick={(symbol) => navigate(`/stocks/${symbol}`)}
        sortBy={sortBy}
        sortOrder={sortOrder}
        onSort={(col: string) => {
          if (sortBy === col) {
            setSortOrder((o) => (o === "asc" ? "desc" : "asc"));
          } else {
            setSortBy(col);
            setSortOrder("asc");
          }
        }}
      />

      {/* Pagination */}
      {totalPages > 1 && (
        <div style={{ display: "flex", justifyContent: "center", margin: "24px 0" }}>
          <button
            onClick={() => setPage((p) => Math.max(1, p - 1))}
            disabled={page === 1}
            style={paginationButtonStyle}
          >
            &lt; Trước
          </button>
          {Array.from({ length: totalPages }, (_, i) => (
            <button
              key={i + 1}
              onClick={() => setPage(i + 1)}
              style={{
                ...paginationButtonStyle,
                fontWeight: page === i + 1 ? "bold" : "normal",
                background: page === i + 1 ? "#3b82f6" : "#fff",
                color: page === i + 1 ? "#fff" : "#374151",
                border: page === i + 1 ? "1px solid #3b82f6" : "1px solid #d1d5db",
              }}
            >
              {i + 1}
            </button>
          ))}
          <button
            onClick={() => setPage((p) => Math.min(totalPages, p + 1))}
            disabled={page === totalPages}
            style={paginationButtonStyle}
          >
            Sau &gt;
          </button>
        </div>
      )}

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

        @media (max-width: 768px) {
          .filter-header {
            flex-direction: column;
            align-items: stretch;
          }
          
          .filter-controls {
            justify-content: space-between;
          }
          
          .filter-stats {
            text-align: center;
            margin-top: 8px;
          }
          
          .filter-grid {
            grid-template-columns: 1fr;
          }
        }
      `}</style>
    </div>
  );
};

// Style objects
const inputStyle = {
  width: "100%",
  padding: "8px 12px",
  border: "1px solid #d1d5db",
  borderRadius: "6px",
  fontSize: "14px",
  outline: "none",
  transition: "all 0.2s ease",
};

const paginationButtonStyle = {
  margin: "0 2px",
  padding: "8px 12px",
  background: "#fff",
  border: "1px solid #d1d5db",
  borderRadius: "6px",
  cursor: "pointer",
  fontSize: "14px",
  minWidth: "40px"
};

// Add hover effects via CSS
const style = document.createElement('style');
style.textContent = `
  input:focus {
    border-color: #3b82f6;
    box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
  }
  
  button:hover:not(:disabled) {
    background: #f3f4f6;
  }
  
  .filter-toggle:hover, .reset-filters:hover {
    background: #f9fafb;
    border-color: #9ca3af;
  }
`;
document.head.appendChild(style);

export default StockPage;