import React, { useEffect, useState } from "react";
import { useAssetStore } from "../../store/assetStore";
import { fetchNewMarketStocks, startFetchAllPrices, getFetchAllStatus} from "../../utils/api";
import AdminActions from "../../components/admin/AdminActions";
import AssetTable from "../../components/admin/AdminAssetTable";
import "../../styles/pages/AdminPage.css";

const AdminPage: React.FC = () => {
  // Sort và phân trang
  const [sortBy, setSortBy] = useState<string>("");
  const [sortOrder, setSortOrder] = useState<"asc" | "desc">("asc");
  const [page, setPage] = useState(1);
  const pageSize = 20;
  const { assets, fetchAllAssets, deleteAsset, loading } = useAssetStore();
  const [message, setMessage] = useState<string | null>(null);
  const [isBusy, setIsBusy] = useState(false);

  useEffect(() => {
    fetchAllAssets();
  }, []);

  const handleFetchNewAssets = async () => {
    setIsBusy(true);
    try {
      await fetchNewMarketStocks();
      await fetchAllAssets();
      setMessage("✅ Fetched new market stocks successfully");
    } catch (e) {
      console.error(e);
      setMessage("❌ Failed to fetch new market stocks");
    } finally {
      setIsBusy(false);
    }
  };

  const handleUpdateAllPrices = async () => {
    setIsBusy(true);
    try {
      const { jobId } = await startFetchAllPrices();
      setMessage("⏳ Updating all prices, please wait...");

      // Poll trạng thái mỗi 5s
      const interval = setInterval(async () => {
        const status = await getFetchAllStatus(jobId);
        if (status.status === "DONE") {
          clearInterval(interval);
          setIsBusy(false);
          setMessage("✅ All prices updated successfully");
        } else if (status.status === "FAILED") {
          clearInterval(interval);
          setIsBusy(false);
          setMessage("❌ Failed to update prices");
        }
      }, 5000);
    } catch (err) {
      console.error(err);
      setMessage("❌ Failed to start update job");
      setIsBusy(false);
    }
  };

  const handleDelete = async (id: string) => {
    await deleteAsset(id);
    setMessage("🗑️ Asset deleted successfully");
  };

  // Sort assets
  let sortedAssets = [...assets];
  if (sortBy) {
    sortedAssets.sort((a, b) => {
      let va: string = "";
      let vb: string = "";
      switch (sortBy) {
        case "symbol":
          va = a.symbol ?? "";
          vb = b.symbol ?? "";
          break;
        case "name":
          va = a.name ?? "";
          vb = b.name ?? "";
          break;
        case "type":
          va = a.type ?? "";
          vb = b.type ?? "";
          break;
        default:
          va = "";
          vb = "";
      }
      return sortOrder === "asc" ? va.localeCompare(vb) : vb.localeCompare(va);
    });
  }

  // Phân trang
  const totalPages = Math.ceil(sortedAssets.length / pageSize);
  const pagedAssets = sortedAssets.slice((page - 1) * pageSize, page * pageSize);

  return (
    <div className="p-6 space-y-4">
      <h1 className="page-title">Admin Dashboard</h1>

      {message && (
        <div className="p-2 bg-blue-100 text-blue-800 rounded">{message}</div>
      )}

      <AdminActions
        onReload={fetchAllAssets}
        onFetchNew={handleFetchNewAssets}
        onUpdateAll={handleUpdateAllPrices}
        loading={isBusy}
      />

      <AssetTable
        assets={pagedAssets}
        onDelete={handleDelete}
        loading={loading || isBusy}
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
            style={{ marginRight: 8, padding: "6px 12px" }}
          >
            &lt; Trước
          </button>
          {Array.from({ length: totalPages }, (_, i) => (
            <button
              key={i + 1}
              onClick={() => setPage(i + 1)}
              style={{
                margin: "0 2px",
                padding: "6px 12px",
                fontWeight: page === i + 1 ? "bold" : "normal",
                background: page === i + 1 ? "#e5e7eb" : "#fff",
                border: "1px solid #ddd",
                borderRadius: 4,
                cursor: "pointer"
              }}
            >
              {i + 1}
            </button>
          ))}
          <button
            onClick={() => setPage((p) => Math.min(totalPages, p + 1))}
            disabled={page === totalPages}
            style={{ marginLeft: 8, padding: "6px 12px" }}
          >
            Sau &gt;
          </button>
        </div>
      )}
    </div>
  );
};

export default AdminPage;
