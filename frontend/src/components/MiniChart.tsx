import React from "react";
import {
  ResponsiveContainer,
  AreaChart,
  Area,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";

interface MiniChartProps {
  data: { timestamp: number | string; price: number }[] | undefined;
  width?: number | string;
  height?: number;
}

const MiniChart: React.FC<MiniChartProps> = ({ data, width = "100%", height = 60 }) => {
  if (!data || data.length === 0) {
    return <div style={{ width, height, display: "flex", alignItems: "center", justifyContent: "center", opacity: 0.6 }}>—</div>;
  }

  // normalize timestamp to readable or numeric
  const series = data.map((d) => ({
    ...d,
    // recharts handles numeric x well; use index or timestamp
    ts: typeof d.timestamp === "number" ? d.timestamp : new Date(d.timestamp).getTime(),
  }));

  // color based on trend
  const first = series[0]?.price ?? 0;
  const last = series[series.length - 1]?.price ?? 0;
  const positive = last >= first;
  const color = positive ? "#16a34a" : "#dc2626"; // green/red (don't set theme globally)

  return (
    <div style={{ width, height }}>
      <ResponsiveContainer width="100%" height={height}>
        <AreaChart data={series}>
          <defs>
            <linearGradient id="grad" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stopColor={color} stopOpacity={0.24} />
              <stop offset="100%" stopColor={color} stopOpacity={0.02} />
            </linearGradient>
          </defs>
          <XAxis dataKey="ts" hide />
          <YAxis domain={["dataMin", "dataMax"]} hide />
          <Tooltip
            formatter={(value: any) => {
              if (typeof value === "number") return [value.toLocaleString(), "Giá"];
              return [value, ""];
            }}
            labelFormatter={(label) => {
              const d = new Date(Number(label));
              return d.toLocaleDateString();
            }}
          />
          <Area type="monotone" dataKey="price" stroke={color} fill="url(#grad)" dot={false} />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  );
};

export default MiniChart;
