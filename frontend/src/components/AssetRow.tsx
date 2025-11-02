import React from "react";
import MiniChart from "./MiniChart";
import StarButton from "./StarButton";
import { formatNumber, formatPrice, getChangeClass } from "../utils/format";

const AssetRow = ({ asset, onClick, showChart, showStar }: any) => (
  <tr onClick={onClick} style={{ borderTop: "1px solid #eee", cursor: "pointer" }}>
    <td style={{ padding: "10px 12px" }}>
      <div style={{ fontWeight: 600 }}>{asset.symbol}</div>
      <div style={{ fontSize: 12, color: "#666" }}>{asset.name}</div>
    </td>
    <td style={{ textAlign: "right" }}>{formatPrice(asset.latestPrice)}</td>
    <td style={{ textAlign: "right" }}>
      <span className={getChangeClass(asset.change24h)} style={{ fontWeight: 600 }}>
        {asset.change24h == null ? "—" : `${asset.change24h >= 0 ? "+" : ""}${asset.change24h}%`}
      </span>
    </td>
    <td style={{ textAlign: "right" }}>{formatNumber(asset.volume)}</td>
    <td style={{ textAlign: "right" }}>{asset.pe?.toFixed(2) ?? "—"}</td>
    <td style={{ textAlign: "right" }}>{asset.pb?.toFixed(2) ?? "—"}</td>

    {showChart && (
      <td style={{ width: 160 }}>
        {asset.chart30d?.length ? (
          <MiniChart data={asset.chart30d} />
        ) : (
          <div style={{ width: 140, height: 60, background: "#f9fafb", borderRadius: 6 }} />
        )}
      </td>
    )}

    {showStar && (
      <td style={{ textAlign: "center" }}>
        <StarButton assetId={asset.id} />
      </td>
    )}
  </tr>
);

export default AssetRow;
