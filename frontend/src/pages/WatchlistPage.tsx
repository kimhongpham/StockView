import React, { useState } from 'react';
import { watchlistData } from '../data/mockData';
import { AddStockModal } from '../components/modals/AddStockModal';

export const WatchlistPage: React.FC = () => {
  const [showAddModal, setShowAddModal] = useState(false);

  return (
    <>
      <div className="page" id="favorit">
        <h1 className="page-title">Watchlist</h1>

        <div className="chart-container">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
            <h2 className="chart-title">My Watchlist</h2>
            <button className="btn btn-primary" onClick={() => setShowAddModal(true)}>
              + Watchlist
            </button>
          </div>

          <div className="search-bar" style={{ marginBottom: '20px', width: '100%' }}>
            <i>🔍</i>
            <input type="text" placeholder="Search stocks..." />
          </div>

          <table className="data-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Price</th>
                <th>Value</th>
                <th>Chart</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {watchlistData.map((stock) => (
                <tr key={stock.code}>
                  <td>{stock.name}</td>
                  <td>${stock.price.toFixed(2)}</td>
                  <td>${stock.value.toFixed(2)}T</td>
                  <td>
                    <div className="stock-mini-chart"></div>
                  </td>
                  <td>
                    <button className="btn btn-primary" style={{ padding: '5px 10px' }}>
                      Buy
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      <AddStockModal 
        isOpen={showAddModal} 
        onClose={() => setShowAddModal(false)} 
        onAddStock={(stockCode) => {
          alert(`Đã thêm ${stockCode} vào watchlist!`);
          setShowAddModal(false);
        }} 
      />
    </>
  );
};

export default WatchlistPage;