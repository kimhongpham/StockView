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
  Filler,
} from "chart.js";
import "chartjs-adapter-date-fns";
import { ChartPoint } from "../../types/asset";

// Register Chart.js components
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
  Filler
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

    // Cleanup old chart
    if (chartInstance.current) {
      chartInstance.current.destroy();
    }

    // Get theme colors from CSS variables
    const computedStyle = getComputedStyle(document.documentElement);
    const primaryColor = computedStyle.getPropertyValue("--primary-500").trim();
    const borderColor = computedStyle.getPropertyValue("--border-color").trim();

    // Create gradient for fill
    const gradient = ctx.createLinearGradient(0, 0, 0, 300);
    gradient.addColorStop(0, `rgba(0, 128, 208, 0.2)`);
    gradient.addColorStop(1, `rgba(0, 128, 208, 0.02)`);

    // Create chart
    chartInstance.current = new Chart(ctx, {
      type: "line",
      data: {
        labels: data.map((d) => new Date(d.timestamp ?? Date.now())),
        datasets: [
          {
            label: selectedStock,
            data: data.map((d) => d.close),
            borderColor: primaryColor,
            backgroundColor: gradient,
            tension: 0.3,
            pointRadius: 0,
            pointHoverRadius: 5,
            pointBackgroundColor: primaryColor,
            pointBorderColor: "white",
            pointBorderWidth: 2,
            fill: true,
            borderWidth: 2.5,
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
            backgroundColor: "rgba(0, 0, 0, 0.75)",
            titleColor: "white",
            bodyColor: "white",
            borderColor: "rgba(255, 255, 255, 0.2)",
            borderWidth: 1,
            padding: 12,
            displayColors: false,
            caretPadding: 12,
            cornerRadius: 8,
            titleFont: { size: 13, weight: "bold" },
            bodyFont: { size: 12 },
            callbacks: {
              title: (context) => {
                const xValue = context[0].parsed.x ?? Date.now();
                const date = new Date(xValue);
                return date.toLocaleDateString("en-US", {
                  month: "short",
                  day: "numeric",
                  year: "numeric",
                });
              },
              label: (context) => {
                const value = context.parsed.y;
                return `Price: ${typeof value === "number" ? value.toLocaleString("en-US", {
                  minimumFractionDigits: 2,
                  maximumFractionDigits: 2,
                }) : "—"}`;
              },
            },
          },
        },
        scales: {
          x: {
            type: "time",
            time: {
              unit: "day",
              tooltipFormat: "MMM d, yyyy",
              displayFormats: { day: "MMM d" },
            },
            grid: {
              display: true,
              color: borderColor,
              lineWidth: 0.5,
            },
            border: { display: false },
            ticks: {
              maxTicksLimit: 8,
              color: "rgba(0, 0, 0, 0.5)",
              font: { size: 11, weight: "normal" },
            },
          },
          y: {
            position: "right" as const,
            grid: {
              display: true,
              color: borderColor,
              lineWidth: 0.5,
            },
            border: { display: false },
            ticks: {
              maxTicksLimit: 8,
              color: "rgba(0, 0, 0, 0.5)",
              font: { size: 11, weight: "normal" },
              callback: (value) => {
                if (typeof value === "number") {
                  return value.toLocaleString("en-US", {
                    minimumFractionDigits: 0,
                    maximumFractionDigits: 0,
                  });
                }
                return "";
              },
            },
          },
        },
      },
    });

    // Cleanup on unmount
    return () => {
      if (chartInstance.current) {
        chartInstance.current.destroy();
      }
    };
  }, [data, selectedStock]);

  if (loading) {
    return (
      <div
        style={{
          width: "100%",
          height: 300,
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          color: "var(--text-muted)",
        }}
      >
        Loading chart...
      </div>
    );
  }

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
