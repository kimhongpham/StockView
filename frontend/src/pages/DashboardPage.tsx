import React from 'react';
import { stockData, trendingStocksData } from '../data/mockData';
import { formatNumber } from '../utils/formatters';

export const DashboardPage: React.FC = () => {
  return (
    <div className="page active" id="dashboard">
      <h1 className="page-title">Dashboard</h1>

      {/* KPI Cards */}
      <div className="kpi-cards">
        <div className="kpi-card">
          <div className="kpi-title">Total Balance</div>
          <div className="kpi-value">$245,678.90</div>
          <div className="kpi-change positive">▲ +12.45 (+1.00%)</div>
        </div>
        {/* ... thêm các KPI cards khác */}
      </div>

      {/* Trending Stocks Section */}
      <div className="chart-container">
        <h2 className="chart-title">Cổ Phiếu Theo Xu Hướng</h2>
        <div className="index-tabs">
          {['Chỉ số', 'Chứng Khoán', 'Hàng hóa', 'Tiền tệ', 'ETF', 'Trái Phiếu', 'Các quỹ', 'Tiền điện tử'].map(
            (tab) => (
              <div key={tab} className={`index-tab ${tab === 'Chứng Khoán' ? 'active' : ''}`}>
                {tab}
              </div>
            )
          )}
        </div>
        <table className="data-table">
          <thead>
            <tr>
              <th>Tên</th>
              <th>Lần cuối</th>
              <th>Cao</th>
              <th>Thấp</th>
              <th>T.đối</th>
              <th>% T.đối</th>
              <th>KL</th>
              <th>Thời gian</th>
            </tr>
          </thead>
          <tbody>
            {trendingStocksData.map((stock, index) => (
              <tr key={index}>
                <td><strong>{stock.name}</strong></td>
                <td>{formatNumber(stock.last)}</td>
                <td>{formatNumber(stock.high)}</td>
                <td>{formatNumber(stock.low)}</td>
                <td className={stock.change >= 0 ? 'positive' : 'negative'}>
                  {stock.change >= 0 ? '+' : ''}{formatNumber(stock.change)}
                </td>
                <td className={stock.change >= 0 ? 'positive' : 'negative'}>
                  {stock.change >= 0 ? '+' : ''}{stock.changePercent.toFixed(2)}%
                </td>
                <td>{stock.volume}</td>
                <td>{stock.time}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Dashboard Layout với 3 columns */}
      <div className="dashboard-layout">
        {/* Left Column */}
        <div className="dashboard-column">
          {/* Account Balance và Trading Form */}
        </div>
        
        {/* Middle Column */}
        <div className="dashboard-column">
          {/* Charts */}
        </div>
        
        {/* Right Column */}
        <div className="dashboard-column">
          {/* Stock Prices Table */}
        </div>
      </div>
    </div>
  );
};