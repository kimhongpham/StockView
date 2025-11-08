import React, { useEffect, useRef } from "react";
import {
  Chart,
  LineController,
  LineElement,
  PointElement,
  LinearScale,
  TimeScale,
  Title,
  Tooltip,
  Legend,
  CategoryScale,
  Filler, // ← Thêm Filler plugin
} from "chart.js";
import "chartjs-adapter-date-fns";
import { ChartPoint } from "../../types/asset";

// Register tất cả các component, bao gồm Filler
Chart.register(
  LineController,
  LineElement,
  PointElement,
  LinearScale,
  TimeScale,
  Title,
  Tooltip,
  Legend,
  CategoryScale,
  Filler // ← Register Filler plugin
);

interface ChartSectionProps {
  data: ChartPoint[];
  selectedStock: string;
  loading: boolean;
}

const ChartSection: React.FC<ChartSectionProps> = ({
  data,
  selectedStock,
  loading,
}) => {
  const chartRef = useRef<HTMLCanvasElement>(null);
  const chartInstance = useRef<Chart | null>(null);

  useEffect(() => {
    if (!chartRef.current || !data?.length) return;

    const ctx = chartRef.current.getContext("2d");
    if (!ctx) return;

    // Hủy chart cũ nếu có
    if (chartInstance.current) {
      chartInstance.current.destroy();
    }

    // Tạo gradient cho vùng dưới đường line
    const gradient = ctx.createLinearGradient(0, 0, 0, 300);
    gradient.addColorStop(0, "rgba(76, 175, 80, 0.3)");
    gradient.addColorStop(1, "rgba(76, 175, 80, 0.05)");

    // Tạo biểu đồ mới
    chartInstance.current = new Chart(ctx, {
      type: "line",
      data: {
        labels: data.map((d) => new Date(d.timestamp ?? Date.now())),
        datasets: [
          {
            label: selectedStock,
            data: data.map((d) => d.close),
            borderColor: "#4CAF50",
            backgroundColor: gradient,
            tension: 0.4,
            pointRadius: 0,
            pointHoverRadius: 4,
            pointBackgroundColor: "#4CAF50",
            pointBorderColor: "#ffffff",
            pointBorderWidth: 2,
            fill: true,
            borderWidth: 2,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        interaction: { mode: "index" as const, intersect: false },
        plugins: {
          legend: { display: false },
          tooltip: {
            backgroundColor: "rgba(0,0,0,0.7)",
            callbacks: {
              title: (context) => {
                const xValue = context[0].parsed.x ?? Date.now();
                const date = new Date(xValue);
                return date.toLocaleDateString("vi-VN", {
                  day: "numeric",
                  month: "numeric",
                });
              },
              label: (context) =>
                `Giá: ${context.parsed.y?.toLocaleString("vi-VN") || "—"}`,
            },
          },
        },
        scales: {
          x: {
            type: "time",
            time: {
              unit: "day",
              tooltipFormat: "dd/MM",
              displayFormats: { day: "dd/MM" },
            },
            grid: { display: false },
            border: { display: false },
            ticks: {
              maxTicksLimit: 8,
              color: "#666",
              font: { size: 11 },
            },
          },
          y: {
            position: "right",
            grid: { color: "rgba(0,0,0,0.05)" },
            border: { display: false },
            ticks: {
              maxTicksLimit: 8,
              color: "#666",
              font: { size: 11 },
              callback: (value) => value.toLocaleString("vi-VN"),
            },
          },
        },
      },
    });

    // Cleanup khi component unmount
    return () => {
      if (chartInstance.current) chartInstance.current.destroy();
    };
  }, [data, selectedStock]);

  if (loading) return <div>Loading chart...</div>;

  return (
    <div
      style={{
        width: "100%",
        height: 300,
        position: "relative",
      }}
    >
      <canvas ref={chartRef} />
    </div>
  );
};

export default ChartSection;
