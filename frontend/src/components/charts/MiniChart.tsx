import React, { useEffect, useRef, useState } from "react";
import {
  Chart,
  LineController,
  LineElement,
  PointElement,
  LinearScale,
  TimeScale,
  Filler,
} from "chart.js";
import "chartjs-adapter-date-fns";
import { fetchPriceHistory } from "../../utils/api";
import { ChartPoint } from "../../types/asset";

Chart.register(
  LineController,
  LineElement,
  PointElement,
  LinearScale,
  TimeScale,
  Filler
);

interface MiniChartProps {
  assetId: string; // nhập asset ID (không phải symbol)
  interval?: string; // "1d", "1m", ...
  limit?: number;
  height?: number;
}

const MiniChart: React.FC<MiniChartProps> = ({
  assetId,
  interval = "1d",
  limit = 30,
  height = 60,
}) => {
  const chartRef = useRef<HTMLCanvasElement>(null);
  const chartInstance = useRef<Chart | null>(null);
  const [data, setData] = useState<ChartPoint[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!assetId) return;

    const fetchData = async () => {
      try {
        setLoading(true);

        // Lấy price history bằng assetId
        const historyData = await fetchPriceHistory(assetId, limit);
        const chartData =
          historyData.content || historyData.data || historyData || [];

        const mappedData = chartData.map((item: any) => ({
          timestamp: item.timestamp || item.date,
          close: item.price ?? item.close,
        }));

        setData(mappedData);
      } catch (err) {
        console.error("Lỗi fetch MiniChart:", err);
        setData([]);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [assetId, interval, limit]);

  useEffect(() => {
    if (!chartRef.current || !data?.length) return;
    const ctx = chartRef.current.getContext("2d");
    if (!ctx) return;

    if (chartInstance.current) chartInstance.current.destroy();

    chartInstance.current = new Chart(ctx, {
      type: "line",
      data: {
        labels: data.map((d) => new Date(d.timestamp ?? Date.now())),
        datasets: [
          {
            data: data.map((d) => d.close),
            borderColor: "#4CAF50",
            backgroundColor: "transparent",
            tension: 0.4,
            pointRadius: 0,
            fill: false,
            borderWidth: 1.5,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: { enabled: false },
        },
        scales: {
          x: {
            type: "time",
            grid: { display: false },
            ticks: { display: false },
            border: { display: false },
          },
          y: {
            display: false,
            grid: { display: false },
            border: { display: false },
          },
        },
      },
    });

    return () => {
      if (chartInstance.current) chartInstance.current.destroy();
    };
  }, [data, height]);

  if (loading) return <div style={{ height }}>—</div>;
  if (!data.length) return <div style={{ height }}>—</div>;

  return (
    <div style={{ width: "100%", height, position: "relative" }}>
      <canvas ref={chartRef} />
    </div>
  );
};

export default MiniChart;
