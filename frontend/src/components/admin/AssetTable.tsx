import React from "react";
import { Asset } from "../../types/asset";
import { fetchAndSaveAssetPrice } from "../../utils/api";

interface Props {
  assets: Asset[];
  onDelete: (id: string) => Promise<void>;
  loading: boolean;
}

const AssetTable: React.FC<Props> = ({ assets, onDelete, loading }) => {
  const handleUpdatePrice = async (id: string) => {
    try {
      await fetchAndSaveAssetPrice(id);
      alert(`✅ Updated price for asset ${id}`);
    } catch {
      alert(`❌ Failed to update price for ${id}`);
    }
  };

  if (assets.length === 0) {
    return (
      <div className="admin-table-empty-state">
        <p>No assets found</p>
      </div>
    );
  }

  return (
    <div className="admin-table-container">
      <table className="admin-table">
        <thead className="admin-table-header">
          <tr>
            <th className="admin-table-th text-center" style={{ width: "60px" }}>#</th>
            <th className="admin-table-th">Symbol</th>
            <th className="admin-table-th">Name</th>
            <th className="admin-table-th">Type</th>
            <th className="admin-table-th actions-column">Actions</th>
          </tr>
        </thead>
        <tbody className="admin-table-body">
          {assets.map((asset, index) => (
            <tr key={asset.id} className="admin-table-row">
              <td className="admin-table-td text-center">{index + 1}</td>
              <td className="admin-table-td font-mono">{asset.symbol.toUpperCase()}</td>
              <td className="admin-table-td">{asset.name}</td>
              <td className="admin-table-td">
                <span className="asset-type-badge">{asset.type}</span>
              </td>
              <td className="admin-table-td actions-cell">
                <div className="actions-container">
                  <button
                    onClick={() => handleUpdatePrice(asset.id)}
                    className="btn-action btn-update"
                    disabled={loading}
                    title="Update Price"
                  >
                    <span className="action-icon">🔄</span>
                  </button>
                  <button
                    onClick={() => onDelete(asset.id)}
                    className="btn-action btn-delete"
                    disabled={loading}
                    title="Delete Asset"
                  >
                    <span className="action-icon">🗑️</span>
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      
      {loading && (
        <div className="table-loading-overlay">
          <div className="spinner"></div>
        </div>
      )}
    </div>
  );
};

export default AssetTable;