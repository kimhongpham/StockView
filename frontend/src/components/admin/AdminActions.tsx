import React from "react";

interface Props {
  onReload: () => void;
  onFetchNew: () => void;
  onUpdateAll: () => void;
  loading: boolean;
}

const AdminActions: React.FC<Props> = ({ onReload, onFetchNew, onUpdateAll, loading }) => (
  <div className="admin-actions-container">
    <button 
      onClick={onReload} 
      disabled={loading} 
      className="admin-btn btn-secondary"
    >
      <span className="btn-icon">🔄</span>
      Reload Assets
    </button>
    <button 
      onClick={onFetchNew} 
      disabled={loading} 
      className="admin-btn btn-success"
    >
      <span className="btn-icon">📥</span>
      Fetch New Stocks
    </button>
    <button 
      onClick={onUpdateAll} 
      disabled={loading} 
      className="admin-btn btn-primary"
    >
      <span className="btn-icon">⚡</span>
      Update All Prices
    </button>
    
    {loading && (
      <div className="loading-overlay">
        <div className="spinner"></div>
      </div>
    )}
  </div>
);

export default AdminActions;