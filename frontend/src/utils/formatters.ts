export const formatNumber = (num: number): string => {
  const absNum = Math.abs(num);
  
  if (absNum >= 1000) {
    const formatted = absNum.toLocaleString();
    return num < 0 ? `-${formatted}` : formatted;
  }
  
  const formatted = absNum.toFixed(1).replace('.0', '');
  return num < 0 ? `-${formatted}` : formatted;
};

export const formatVolume = (num: number): string => {
  const absNum = Math.abs(num);

  if (absNum >= 1_000_000_000) return (num / 1_000_000_000).toFixed(2) + "B";
  if (absNum >= 1_000_000) return (num / 1_000_000).toFixed(2) + "M";
  if (absNum >= 1_000) return (num / 1_000).toFixed(2) + "K";
  return num.toString();
};
