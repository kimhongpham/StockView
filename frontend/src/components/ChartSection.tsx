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
} from "chart.js";
import "chartjs-adapter-date-fns";
import { ChartPoint } from "../types/asset";

Chart.register(
  LineController,
  LineElement,
  PointElement,
  LinearScale,
  TimeScale,
  Title,
  Tooltip,
  Legend,
  CategoryScale
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

    // Tạo biểu đồ mới
    chartInstance.current = new Chart(ctx, {
      type: "line",
      data: {
        labels: data.map((d) => new Date(d.timestamp)),
        datasets: [
          {
            label: selectedStock,
            data: data.map((d) => d.close), // ✅ dùng giá đóng cửa
            borderColor: "#4CAF50",
            backgroundColor: "rgba(76, 175, 80, 0.2)",
            tension: 0.2,
            pointRadius: 2,
          },
        ],
      },
      options: {
        responsive: true,
        interaction: {
          mode: "index" as const,
          intersect: false,
        },
        plugins: {
          tooltip: {
            enabled: true,
            callbacks: {
              label: (context) =>
                `Giá: ${context.parsed.y.toLocaleString("vi-VN")} USD`,
            },
          },
          legend: { display: false },
          title: {
            display: true,
            text: `Biểu đồ giá 30 ngày - ${selectedStock}`,
          },
        },
        scales: {
          x: {
            type: "time",
            time: {
              unit: "day",
              tooltipFormat: "dd/MM/yyyy",
              displayFormats: { day: "dd/MM" },
            },
            title: { display: true, text: "Ngày" },
          },
          y: {
            title: { display: true, text: "Giá (USD)" },
            beginAtZero: false,
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
    <div style={{ width: "100%", height: 300 }}>
      <canvas ref={chartRef} />
    </div>
  );
};

export default ChartSection;
