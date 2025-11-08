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

const AssetTable: React.FC<AssetTableProps> = ({ rows, onRowClick, showChart, showStar, onSort }) => (
  <div className="table-container" style={{ overflowX: "auto" }}>
    <table className="data-table stock-table" style={{ width: "100%", borderCollapse: "collapse" }}>
      <thead>
        <tr>
          <th style={{ textAlign: "left", padding: "8px 12px", cursor: "pointer" }} onClick={() => onSort?.("symbol")}>Cổ phiếu</th>
          <th style={{ padding: "8px 12px", cursor: "pointer" }} onClick={() => onSort?.("latestPrice")}>Giá hiện tại</th>
          <th style={{ padding: "8px 12px", cursor: "pointer" }} onClick={() => onSort?.("change24h")}>Biến động % (24h)</th>
          <th style={{ padding: "8px 12px", cursor: "pointer" }} onClick={() => onSort?.("volume")}>KLGD</th>
          <th style={{ padding: "8px 12px", cursor: "pointer" }} onClick={() => onSort?.("pe")}>P/E</th>
          <th style={{ padding: "8px 12px", cursor: "pointer" }} onClick={() => onSort?.("pb")}>P/B</th>
          {showChart && <th style={{ padding: "8px 12px" }}>Biểu đồ 30D</th>}
          {showStar && <th style={{ padding: "8px 12px" }}>Yêu thích</th>}
        </tr>
      </thead>
      <tbody>
        {rows.length === 0 ? (
          <tr>
              <td colSpan={6 + (showChart ? 1 : 0) + (showStar ? 1 : 0)} style={{ textAlign: "center", padding: 20 }}>
              Không có dữ liệu.
            </td>
          </tr>
        ) : (
          rows.map((r) => (
            <AssetRow
              key={r.id}
              asset={r}
              showChart={showChart}
              showStar={showStar}
              onClick={() => onRowClick?.(r.symbol)}
            />
          ))
        )}
      </tbody>
    </table>
  </div>
);

export default AssetTable;
