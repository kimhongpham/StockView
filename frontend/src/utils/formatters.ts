export const formatNumber = (num: number): string => {
  const absNum = Math.abs(num);
  
  if (absNum >= 1000) {
    const formatted = absNum.toLocaleString();
    return num < 0 ? `-${formatted}` : formatted;
  }
  
  const formatted = absNum.toFixed(1).replace('.0', '');
  return num < 0 ? `-${formatted}` : formatted;
};