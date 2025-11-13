import React from "react";
import StarButton from "../components/common/StarButton";
import MiniChart from "../components/charts/MiniChart";
import { formatPrice, getChangeClass } from "../utils/format";

interface AssetRowProps {
  asset: {
    id: string;
    symbol: string;
    name: string;
    latestPrice?: number;
    change24h?: number;
    volume?: number;
    pe?: number | null;
    pb?: number | null;
  };
  onClick?: () => void;
  showChart?: boolean;
  showStar?: boolean;
}

const AssetRow: React.FC<AssetRowProps> = ({
  asset,
  onClick,
  showChart,
  showStar,
}) => (
  <tr className="asset-row" onClick={onClick}>
    {/* Cột: Mã & Tên */}
    <td className="asset-info-cell">
      <div className="asset-symbol">{asset.symbol}</div>
      <div className="asset-name">{asset.name}</div>
    </td>

    {/* Cột: Giá hiện tại */}
    <td className="price-cell">
      <div className="price-value">{formatPrice(asset.latestPrice ?? 0)}</div>
    </td>

    {/* Cột: Biến động % (24h) */}
    <td className="change-cell">
      <div className={`change-badge ${getChangeClass(asset.change24h ?? 0)}`}>
        {asset.change24h == null
          ? "—"
          : `${asset.change24h >= 0 ? "+" : ""}${asset.change24h?.toFixed(2)}%`}
      </div>
    </td>

    {/* Cột: MiniChart 30D */}
    {showChart && (
      <td className="chart-cell" style={{ width: 100, height: 60 }}>
        <MiniChart assetId={asset.id} limit={30} height={60} />
      </td>
    )}

    {/* Cột: Nút yêu thích */}
    {showStar && (
      <td className="star-cell">
        <StarButton assetSymbol={asset.symbol} />
      </td>
    )}
  </tr>
);

export default AssetRow;
