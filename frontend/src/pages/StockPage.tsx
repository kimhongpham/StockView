import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import AssetTable from "../components/AssetTable";

interface AssetOverview {
  id: string;
  symbol: string;
  name: string;
  exchange?: string;
  industry?: string;
  country?: string;
  marketCapitalization?: number;
  peRatio?: number;
  pbRatio?: number;
  roe?: number;
  dividendYield?: number;
  ytdChangePercent?: number;
  currentPrice?: number;
  logo?: string;
}

const StockPage: React.FC = () => {
  const [assets, setAssets] = useState<AssetOverview[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filter, setFilter] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const loadData = async () => {
      try {
        // 1️⃣ Lấy tất cả asset cơ bản
        const res = await axios.get("/api/assets");
        const assetList = Array.isArray(res.data)
          ? res.data
          : res.data.content || [];

        // 2️⃣ Gọi song song /overview cho từng symbol
        const detailedAssets = await Promise.all(
          assetList.map(async (a: any) => {
            try {
              const overviewRes = await axios.get(`/api/assets/${a.symbol}/overview`);
              return overviewRes.data;
            } catch (err) {
              console.warn(`⚠️ Không lấy được overview cho ${a.symbol}`);
              return a;
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

  // 🔍 Lọc cổ phiếu theo tên hoặc mã
  const visibleAssets = assets.filter((a) => {
    const q = filter.trim().toLowerCase();
    if (!q) return true;
    return (
      a.symbol.toLowerCase().includes(q) ||
      (a.name ?? "").toLowerCase().includes(q)
    );
  });

  return (
    <div className="page active" id="stocks">
      <h1 className="page-title font-bold text-2xl mb-4">Tất cả cổ phiếu</h1>

      <div className="flex items-center gap-3 mb-4">
        <input
          type="text"
          placeholder="Tìm kiếm mã hoặc tên..."
          value={filter}
          onChange={(e) => setFilter(e.target.value)}
          className="border border-gray-300 rounded-lg px-3 py-2 w-64"
        />
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
        rows={visibleAssets}
        showChart={false}
        showStar={true}
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

export default StockPage;
