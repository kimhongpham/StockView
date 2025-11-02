import React from "react";
import { Asset, PriceDto } from "../types/asset";
// import { formatVolume } from "../utils/format";
import { TrendingUp, TrendingDown } from "lucide-react";

interface StockTableProps {
  title: string;
  stocks: Asset[];
  prices: Record<string, PriceDto>;
  onSelect: (symbol: string) => void;
}

const StockTable: React.FC<StockTableProps> = ({
  title,
  stocks,
  prices,
  onSelect,
}) => (
  <div
    style={{
      background: "#fff",
      borderRadius: 12,
      padding: 20,
      boxShadow: "0 2px 6px rgba(0,0,0,0.05)",
      transition: "0.3s",
    }}
  >
    <h2
      style={{
        marginBottom: 15,
        fontSize: 16,
        fontWeight: 600,
        color: "#111827",
      }}
    >
      {title}
    </h2>

    <table style={{ width: "100%", borderCollapse: "collapse" }}>
      <thead>
        <tr style={{ borderBottom: "1px solid #e5e7eb" }}>
          <th style={{ padding: 10, textAlign: "left", color: "#6b7280" }}>
            Mã
          </th>
          <th style={{ padding: 10, textAlign: "right", color: "#6b7280" }}>
            % thay đổi
          </th>
          <th style={{ padding: 10, textAlign: "right", color: "#6b7280" }}>
            Khối lượng
          </th>
        </tr>
      </thead>

      <tbody>
        {stocks.map((stock) => {
          const price = prices[stock.id];
          const change = price?.changePercent ?? 0;
          const color = change >= 0 ? "#16a34a" : "#dc2626";
          const Icon = change >= 0 ? TrendingUp : TrendingDown;

          return (
            <tr
              key={stock.id}
              onClick={() => onSelect(stock.symbol)}
              style={{
                borderBottom: "1px solid #f3f4f6",
                cursor: "pointer",
                transition: "background 0.2s",
              }}
              onMouseEnter={(e) =>
                (e.currentTarget.style.backgroundColor = "#f9fafb")
              }
              onMouseLeave={(e) =>
                (e.currentTarget.style.backgroundColor = "transparent")
              }
            >
              <td style={{ padding: 10, fontWeight: 500 }}>{stock.symbol}</td>

              <td
                style={{
                  padding: 10,
                  textAlign: "right",
                  color,
                  fontWeight: 500,
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "flex-end",
                  gap: 4,
                }}
              >
                <Icon size={14} />
                {change >= 0 ? "+" : ""}
                {change.toFixed(2)}%
              </td>

              {/* <td style={{ padding: 10, textAlign: "right" }}>
                {formatVolume(price?.volume || 0)}
              </td> */}
            </tr>
          );
        })}
      </tbody>
    </table>
  </div>
);

export default StockTable;
