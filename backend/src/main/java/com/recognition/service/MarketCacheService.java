package com.recognition.service;

import com.recognition.dto.CandleDTO;
import com.recognition.dto.PriceDto;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing cached market data (prices & candlesticks).
 * Provides abstraction layer for caching operations with configurable TTL.
 */
public interface MarketCacheService {

  /**
   * Get cached price for a symbol.
   * 
   * @param symbol Asset symbol (e.g., "AAPL", "BTC")
   * @return Optional containing cached price or empty if cache miss
   */
  Optional<PriceDto> getPrice(String symbol);

  /**
   * Set price in cache with configured TTL.
   * 
   * @param symbol   Asset symbol
   * @param priceDto Price data to cache
   */
  void setPrice(String symbol, PriceDto priceDto);

  /**
   * Get cached candles for a symbol with interval.
   * 
   * @param symbol   Asset symbol
   * @param interval Time interval (1d, 1w, 1m, all)
   * @return Optional containing cached candlesticks or empty if cache miss
   */
  Optional<List<CandleDTO>> getCandles(String symbol, String interval);

  /**
   * Set candles in cache with configured TTL.
   * 
   * @param symbol   Asset symbol
   * @param interval Time interval
   * @param candles  Candlestick data to cache
   */
  void setCandles(String symbol, String interval, List<CandleDTO> candles);

  /**
   * Invalidate (remove) cached price for a symbol.
   * Called when real-time updates are received from provider.
   * 
   * @param symbol Asset symbol
   */
  void invalidatePrice(String symbol);

  /**
   * Invalidate all cached candles for a symbol.
   * Called when new price data arrives.
   * 
   * @param symbol Asset symbol
   */
  void invalidateCandles(String symbol);

  /**
   * Invalidate all cached candles across all symbols.
   * Called during full market data refresh.
   */
  void invalidateAllCandles();

  /**
   * Invalidate all cached data (prices and candles).
   * Used for manual cache clearing or emergency reset.
   */
  void invalidateAll();

  /**
   * Check if cache service is enabled.
   * 
   * @return true if caching is active, false otherwise
   */
  boolean isEnabled();
}
