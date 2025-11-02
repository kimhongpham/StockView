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
  data?: { timestamp: number | string; price: number }[];
  width?: number | string;
  height?: number;
}

const MiniChart: React.FC<MiniChartProps> = ({
  data,
  width = "100%",
  height = 60,
}) => {
  if (!data || data.length === 0) {
    return (
      <div
        style={{
          width,
          height,
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          opacity: 0.5,
          fontSize: "0.9rem",
        }}
      >
        —
      </div>
    );
  }

  // Chuẩn hoá dữ liệu (timestamp → số)
  const series = data.map((d) => ({
    ...d,
    ts:
      typeof d.timestamp === "number"
        ? d.timestamp
        : new Date(d.timestamp).getTime(),
  }));

  const first = series[0]?.price ?? 0;
  const last = series[series.length - 1]?.price ?? 0;
  const positive = last >= first;
  const color = positive ? "#16a34a" : "#dc2626";

  return (
    <div style={{ width, height }}>
      <ResponsiveContainer width="100%" height={height}>
        <AreaChart
          data={series}
          margin={{ top: 2, right: 2, left: 2, bottom: 0 }}
        >
          <defs>
            <linearGradient id="gradMini" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stopColor={color} stopOpacity={0.25} />
              <stop offset="100%" stopColor={color} stopOpacity={0.02} />
            </linearGradient>
          </defs>

          <XAxis dataKey="ts" hide />
          <YAxis domain={["dataMin", "dataMax"]} hide />

          <Tooltip
            contentStyle={{
              backgroundColor: "rgba(17, 24, 39, 0.85)",
              border: "none",
              borderRadius: "6px",
              padding: "6px 8px",
            }}
            labelStyle={{ color: "#e5e7eb", fontSize: "0.75rem" }}
            itemStyle={{ color: "#f8fafc", fontSize: "0.8rem" }}
            formatter={(value: any) =>
              typeof value === "number"
                ? [value.toLocaleString("vi-VN"), "Giá"]
                : [value, ""]
            }
            labelFormatter={(label) =>
              new Date(Number(label)).toLocaleDateString("vi-VN")
            }
          />

          <Area
            type="monotone"
            dataKey="price"
            stroke={color}
            fill="url(#gradMini)"
            strokeWidth={1.6}
            dot={false}
            isAnimationActive={false} // ⚡ tắt animation cho hiệu năng cao khi có nhiều chart
          />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  );
};

export default MiniChart;
