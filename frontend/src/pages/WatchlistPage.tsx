import React, { useState, useEffect, useRef } from 'react';
import { watchlistData, stockData } from '../data/mockData';
import { AddStockModal } from '../components/modals/AddStockModal';
import Chart from 'chart.js/auto';

export const WatchlistPage: React.FC = () => {
  const [showAddModal, setShowAddModal] = useState(false);
  const favoritStockChartRef = useRef<HTMLCanvasElement>(null);
  const favoritStockChartInstance = useRef<Chart>();

  useEffect(() => {
    // Initialize chart when component mounts
    if (favoritStockChartRef.current) {
      // Destroy existing chart
      if (favoritStockChartInstance.current) {
        favoritStockChartInstance.current.destroy();
      }

      // Create new chart
      favoritStockChartInstance.current = new Chart(favoritStockChartRef.current, {
        type: "line",
        data: {
          labels: ["9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00"],
          datasets: [
            {
              label: "AAPL",
              data: [170, 172, 171, 173, 174, 175, 176],
              borderColor: "#2563eb",
              backgroundColor: "rgba(37, 99, 235, 0.1)",
              tension: 0.4,
              fill: true,
            },
          ],
        },
        options: {
          responsive: true,
          plugins: {
            legend: {
              display: false,
            },
          },
        },
      });
    }

    // Cleanup function
    return () => {
      if (favoritStockChartInstance.current) {
        favoritStockChartInstance.current.destroy();
      }
    };
  }, []);

  return (
    <>
      <div className="page active" id="favorit">
        <h1 className="page-title">Watchlist</h1>

        <div className="chart-container">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
            <h2 className="chart-title">My Watchlist</h2>
            <button className="btn btn-primary" id="addToWatchlistBtn" onClick={() => setShowAddModal(true)}>
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
            <tbody id="watchlistTable">
              {watchlistData.map((stock) => {
                const changeClass = stock.change >= 0 ? "positive" : "negative";
                const changeSymbol = stock.change >= 0 ? "▲" : "▼";
                
                return (
                  <tr key={stock.code}>
                    <td>{stock.name}</td>
                    <td>${stock.price.toFixed(2)}</td>
                    <td>${stock.value.toFixed(2)}T</td>
                    <td>
                      <div className="stock-mini-chart" id={`watchlist-chart-${stock.code}`}></div>
                    </td>
                    <td>
                      <button className="btn btn-primary" style={{ padding: '5px 10px' }}>
                        Buy
                      </button>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>

        <div className="stock-layout">
          <div className="chart-container">
            <h2 className="chart-title">Stock</h2>
            <canvas id="favoritStockChart" ref={favoritStockChartRef}></canvas>
          </div>

          <div className="chart-container">
            <h2 className="chart-title">Stock Market</h2>
            <div className="stock-list">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Code</th>
                    <th>Price</th>
                    <th>Change</th>
                  </tr>
                </thead>
                <tbody id="favoritStockList">
                  {stockData.slice(0, 5).map((stock) => {
                    const changeClass = stock.change >= 0 ? "positive" : "negative";
                    const changeSymbol = stock.change >= 0 ? "▲" : "▼";
                    
                    return (
                      <tr key={stock.code}>
                        <td>{stock.code}</td>
                        <td>${stock.price.toFixed(2)}</td>
                        <td className={changeClass}>
                          {changeSymbol} {Math.abs(stock.change).toFixed(2)} ({Math.abs(stock.changePercent).toFixed(2)}%)
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          </div>
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