import React from 'react';
import { stockData } from '../data/mockData';

export const StockPage: React.FC = () => {
  return (
    <div className="page" id="stock">
      <h1 className="page-title">Stock Market</h1>

      <div className="stock-tabs">
        {['Finance', 'Energy', 'Material', 'Technology', 'Healthcare'].map((tab) => (
          <div key={tab} className={`stock-tab ${tab === 'Finance' ? 'active' : ''}`}>
            {tab}
          </div>
        ))}
      </div>

      <div className="stock-layout">
        <div className="chart-container">
          <h2 className="chart-title">Stock List</h2>
          <table className="data-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Price</th>
                <th>Value</th>
                <th>Watchlist</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {stockData.map((stock) => (
                <tr key={stock.code}>
                  <td>{stock.name}</td>
                  <td>${stock.price.toFixed(2)}</td>
                  <td>${stock.value.toFixed(2)}T</td>
                  <td>
                    <canvas className="stock-mini-chart"></canvas>
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

        <div className="chart-container">
          <h2 className="chart-title">Stock Market</h2>
          <canvas id="stockMarketChart"></canvas>
        </div>
      </div>
    </div>
  );
};