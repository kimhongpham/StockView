import React from "react";
import AssetRow from "./AssetRow";

interface AssetTableProps {
  rows: any[];
  onRowClick?: (symbol: string) => void;
  showChart?: boolean;
  showStar?: boolean;
}

const AssetTable: React.FC<AssetTableProps> = ({ rows, onRowClick, showChart, showStar }) => (
  <div className="table-container" style={{ overflowX: "auto" }}>
    <table className="data-table stock-table" style={{ width: "100%", borderCollapse: "collapse" }}>
      <thead>
        <tr>
          <th style={{ textAlign: "left", padding: "8px 12px" }}>Cổ phiếu</th>
          <th style={{ padding: "8px 12px" }}>Giá hiện tại</th>
          <th style={{ padding: "8px 12px" }}>Biến động % (24h)</th>
          <th style={{ padding: "8px 12px" }}>KLGD</th>
          <th style={{ padding: "8px 12px" }}>P/E</th>
          <th style={{ padding: "8px 12px" }}>P/B</th>
          {showChart && <th style={{ padding: "8px 12px" }}>Biểu đồ 30D</th>}
        </tr>
      </thead>
      <tbody>
        {rows.length === 0 ? (
          <tr>
            <td colSpan={showChart ? 7 : 6} style={{ textAlign: "center", padding: 20 }}>
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
