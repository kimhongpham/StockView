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

  return (
    <table className="min-w-full border mt-4">
      <thead>
        <tr className="bg-gray-200">
          <th className="p-2 border">Symbol</th>
          <th className="p-2 border">Name</th>
          <th className="p-2 border">Type</th>
          <th className="p-2 border">Actions</th>
        </tr>
      </thead>
      <tbody>
        {assets.map((asset) => (
          <tr key={asset.id}>
            <td className="border p-2">{asset.symbol}</td>
            <td className="border p-2">{asset.name}</td>
            <td className="border p-2">{asset.type}</td>
            <td className="border p-2 space-x-2">
              <button
                onClick={() => handleUpdatePrice(asset.id)}
                className="px-3 py-1 bg-blue-500 text-white rounded"
                disabled={loading}
              >
                Update Price
              </button>
              <button
                onClick={() => onDelete(asset.id)}
                className="px-3 py-1 bg-red-500 text-white rounded"
                disabled={loading}
              >
                Delete
              </button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

export default AssetTable;
