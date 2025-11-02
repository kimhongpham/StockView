export const formatNumber = (n?: number | null) => {
  if (n == null) return "—";
  return n.toLocaleString();
};

export const formatPrice = (p?: number | null) => {
  if (p == null) return "—";
  return p < 1000 ? p.toFixed(2) : Math.round(p).toLocaleString();
};

export const getChangeClass = (v?: number | null) => {
  if (v == null) return "";
  return v >= 0 ? "text-green-600" : "text-red-600";
};
