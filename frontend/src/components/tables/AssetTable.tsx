import React from "react";
import AssetRow from "../AssetRow";

interface AssetTableProps {
  rows: any[];
  onRowClick?: (symbol: string) => void;
  showChart?: boolean;
  showStar?: boolean;
  sortBy?: string;
  sortOrder?: "asc" | "desc";
  onSort?: (col: string) => void;
}

const AssetTable: React.FC<AssetTableProps> = ({ 
  rows, 
  onRowClick, 
  showChart, 
  showStar, 
  sortBy, 
  sortOrder, 
  onSort 
}) => (
  <div className="asset-table-container">
    <table className="asset-table">
      <thead>
        <tr>
          <th 
            className="table-header sortable" 
            onClick={() => onSort?.("symbol")}
          >
            <div className="header-content">
              <span>Cổ phiếu</span>
              {sortBy === "symbol" && (
                <SortIcon direction={sortOrder ?? "asc"} />
              )}
            </div>
          </th>
          <th 
            className="table-header sortable" 
            onClick={() => onSort?.("latestPrice")}
          >
            <div className="header-content">
              <span>Giá hiện tại</span>
              {sortBy === "latestPrice" && (
                <SortIcon direction={sortOrder ?? "asc"} />
              )}
            </div>
          </th>
          <th 
            className="table-header sortable" 
            onClick={() => onSort?.("change24h")}
          >
            <div className="header-content">
              <span>Biến động % (24h)</span>
              {sortBy === "change24h" && (
                <SortIcon direction={sortOrder ?? "asc"} />
              )}
            </div>
          </th>
          {showChart && (
            <th className="table-header">
              <span>Biểu đồ 30D</span>
            </th>
          )}
          {showStar && (
            <th className="table-header">
              <span>Yêu thích</span>
            </th>
          )}
        </tr>
      </thead>
      <tbody>
        {rows.length === 0 ? (
          <tr className="empty-row">
            <td 
              colSpan={3 + (showChart ? 1 : 0) + (showStar ? 1 : 0)} 
              className="empty-state"
            >
              <div className="empty-content">
                <div className="empty-icon">📈</div>
                <div className="empty-text">Không có dữ liệu cổ phiếu</div>
              </div>
            </td>
          </tr>
        ) : (
          rows.map((asset) => (
            <AssetRow
              key={asset.id}
              asset={asset}
              showChart={showChart}
              showStar={showStar}
              onClick={() => onRowClick?.(asset.symbol)}
            />
          ))
        )}
      </tbody>
    </table>
  </div>
);

// Sort Icon Component
const SortIcon: React.FC<{ direction: "asc" | "desc" }> = ({ direction }) => (
  <span className={`sort-icon ${direction}`}>
    {direction === "asc" ? "↑" : "↓"}
  </span>
);

export default AssetTable;