import React from "react";

interface Props {
  onReload: () => void;
  onFetchNew: () => void;
  onUpdateAll: () => void;
  loading: boolean;
}

const AdminActions: React.FC<Props> = ({ onReload, onFetchNew, onUpdateAll, loading }) => (
  <div className="space-x-2">
    <button onClick={onReload} disabled={loading} className="px-4 py-2 bg-gray-500 text-white rounded">
      Reload Assets
    </button>
    <button onClick={onFetchNew} disabled={loading} className="px-4 py-2 bg-green-600 text-white rounded">
      Fetch New Market Stocks
    </button>
    <button onClick={onUpdateAll} disabled={loading} className="px-4 py-2 bg-blue-600 text-white rounded">
      Update All Prices
    </button>
  </div>
);

export default AdminActions;
