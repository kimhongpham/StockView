// src/pages/AdminPage.tsx
import React, { useEffect, useState } from "react";
import { useAssetStore } from "../store/assetStore";
import { fetchNewMarketStocks, fetchAndSaveAllPrices } from "../utils/api";
import AdminActions from "../components/admin/AdminActions";
import AssetTable from "../components/admin/AssetTable";
import "../styles/AdminPage.css";

const AdminPage: React.FC = () => {
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
      await fetchAllAssets(); // ✅ cập nhật lại danh sách
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
      await fetchAndSaveAllPrices();
      setMessage("✅ Updated all asset prices successfully");
    } catch (e) {
      console.error(e);
      setMessage("❌ Failed to update all prices");
    } finally {
      setIsBusy(false);
    }
  };

  const handleDelete = async (id: string) => {
    await deleteAsset(id);
    setMessage("🗑️ Asset deleted successfully");
  };

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

      <AssetTable assets={assets} onDelete={handleDelete} loading={loading || isBusy} />
    </div>
  );
};

export default AdminPage;
