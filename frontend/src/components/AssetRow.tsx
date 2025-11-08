import MiniChart from "../components/charts/MiniChart";
import StarButton from "../components/common/StarButton";
import { formatNumber, formatPrice, getChangeClass } from "../utils/format";

const AssetRow = ({ asset, onClick, showChart, showStar }: any) => (
  <tr
    onClick={onClick}
    style={{ borderTop: "1px solid #eee", cursor: "pointer" }}
  >
    <td style={{ padding: "10px 12px" }}>
      <div style={{ fontWeight: 600 }}>{asset.symbol}</div>
      <div style={{ fontSize: 12, color: "#666" }}>{asset.name}</div>
    </td>
    <td style={{ textAlign: "right" }}>
      {formatPrice(asset.latestPrice ?? 0)}
    </td>
    <td style={{ textAlign: "right" }}>
      <span
        className={getChangeClass(asset.change24h ?? 0)}
        style={{ fontWeight: 600 }}
      >
        {asset.change24h == null
          ? "—"
          : `${asset.change24h >= 0 ? "+" : ""}${asset.change24h}%`}
      </span>
    </td>
    <td style={{ textAlign: "right" }}>{formatNumber(asset.volume ?? 0)}</td>
    <td style={{ textAlign: "right" }}>{asset.pe?.toFixed(2) ?? "—"}</td>
    <td style={{ textAlign: "right" }}>{asset.pb?.toFixed(2) ?? "—"}</td>

    {/* Biểu đồ 30D luôn render trước Yêu thích để khớp header */}
    {showChart && (
      <td style={{ width: 160 }}>
        {asset.chart30d?.length ? (
          <MiniChart data={asset.chart30d} />
        ) : (
          <div
            style={{
              width: 140,
              height: 60,
              background: "#f9fafb",
              borderRadius: 6,
            }}
          />
        )}
      </td>
    )}
    {showStar && (
      <td style={{ textAlign: "center" }}>
        <StarButton assetSymbol={asset.symbol} />
      </td>
    )}
  </tr>
);

export default AssetRow;
