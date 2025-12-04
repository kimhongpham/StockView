import React from "react";
import { Asset, PriceDto } from "../../types/asset";
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
  <div className="data-table-container" style={{
    background: "var(--card-bg)",
    borderRadius: "var(--border-radius-lg)",
    padding: "var(--spacing-6)",
    boxShadow: "var(--shadow-sm)",
    border: "1px solid var(--border-color)",
    transition: "all var(--transition-normal)",
  }}>
    <h2 style={{
      marginBottom: "var(--spacing-6)",
      fontSize: "1.1rem",
      fontWeight: "var(--font-weight-semibold)",
      color: "var(--text-color)",
      letterSpacing: "-0.01em",
    }}>
      {title}
    </h2>

    <table className="data-table" style={{ width: "100%" }}>
      <thead>
        <tr>
          <th>Symbol</th>
          <th style={{ textAlign: "right" }}>Change %</th>
          <th style={{ textAlign: "right" }}>Volume</th>
        </tr>
      </thead>

      <tbody>
        {stocks.map((stock) => {
          const price = prices[stock.id];
          const change = price?.changePercent ?? 0;
          const color = change >= 0 ? "var(--positive-color)" : "var(--negative-color)";
          const Icon = change >= 0 ? TrendingUp : TrendingDown;

          return (
            <tr
              key={stock.id}
              onClick={() => onSelect(stock.symbol)}
              style={{
                cursor: "pointer",
                transition: "background-color var(--transition-fast)",
              }}
            >
              <td style={{ fontWeight: "var(--font-weight-medium)" }}>
                {stock.symbol}
              </td>

              <td
                style={{
                  textAlign: "right",
                  color,
                  fontWeight: "var(--font-weight-medium)",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "flex-end",
                  gap: "var(--spacing-2)",
                }}
              >
                <Icon size={16} />
                {change >= 0 ? "+" : ""}
                {change.toFixed(2)}%
              </td>

              <td style={{ textAlign: "right", color: "var(--text-secondary)" }}>
                {price?.volume ? `${(price.volume / 1000000).toFixed(2)}M` : "-"}
              </td>
            </tr>
          );
        })}
      </tbody>
    </table>

    {stocks.length === 0 && (
      <div style={{
        textAlign: "center",
        padding: "var(--spacing-6)",
        color: "var(--text-muted)",
      }}>
        No data available
      </div>
    )}
  </div>
);

export default StockTable;
