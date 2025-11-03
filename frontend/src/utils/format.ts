// ===== NUMBER FORMATTING UTILITIES =====

/**
 * Format number with locale string, fallback to em dash for null/undefined
 */
export const formatNumber = (value?: number | null): string => {
  if (value == null) return "—";
  return value.toLocaleString();
};

/**
 * Format price with conditional decimal places
 * - Prices < 1000: 2 decimal places
 * - Prices >= 1000: rounded to whole number with thousand separators
 */
export const formatPrice = (price?: number | null): string => {
  if (price == null) return "—";
  
  return price < 1000 
    ? price.toFixed(2)
    : Math.round(price).toLocaleString();
};

/**
 * Get CSS class name based on value change (positive/negative/neutral)
 */
export const getChangeClass = (value?: number | null): string => {
  if (value == null) return "change-neutral";
  
  return value >= 0 ? "change-positive" : "change-negative";
};

// Alternative version with more detailed classes
export const getChangeVariant = (value?: number | null): string => {
  if (value == null) return "change-neutral";
  
  if (value > 0) return "change-positive-strong";
  if (value === 0) return "change-neutral";
  if (value > -0.05) return "change-negative-weak";
  
  return "change-negative-strong";
};