package com.recognition.controller;

import com.recognition.service.MarketCacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Cache management endpoints for manual cache invalidation and monitoring.
 * Requires ADMIN role for cache operations.
 */
@RestController
@RequestMapping("/api/admin/cache")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cache Management", description = "Cache invalidation and monitoring endpoints")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CacheManagementController {

  private final MarketCacheService marketCacheService;

  /**
   * Check if cache is enabled.
   */
  @GetMapping("/status")
  @Operation(summary = "Check cache status")
  public ResponseEntity<Map<String, Object>> getCacheStatus() {
    boolean enabled = marketCacheService.isEnabled();
    log.info("Cache status check: enabled={}", enabled);
    return ResponseEntity.ok(Map.of(
        "enabled", enabled,
        "message", enabled ? "Cache is active" : "Cache is disabled"));
  }

  /**
   * Invalidate cached price for a specific symbol.
   */
  @DeleteMapping("/price/{symbol}")
  @Operation(summary = "Invalidate price cache for a symbol")
  public ResponseEntity<Map<String, Object>> invalidatePrice(@PathVariable String symbol) {
    if (!marketCacheService.isEnabled()) {
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
          .body(Map.of("error", "Cache is disabled"));
    }

    marketCacheService.invalidatePrice(symbol);
    log.info("✗ Cache invalidated for price: {}", symbol);
    return ResponseEntity.ok(Map.of(
        "message", "Price cache invalidated for symbol: " + symbol,
        "symbol", symbol));
  }

  /**
   * Invalidate all cached candles for a specific symbol.
   */
  @DeleteMapping("/candles/{symbol}")
  @Operation(summary = "Invalidate candle cache for a symbol")
  public ResponseEntity<Map<String, Object>> invalidateCandles(@PathVariable String symbol) {
    if (!marketCacheService.isEnabled()) {
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
          .body(Map.of("error", "Cache is disabled"));
    }

    marketCacheService.invalidateCandles(symbol);
    log.info("✗ Cache invalidated for candles: {}", symbol);
    return ResponseEntity.ok(Map.of(
        "message", "Candle cache invalidated for symbol: " + symbol,
        "symbol", symbol));
  }

  /**
   * Invalidate all cached data for a specific symbol (both price and candles).
   */
  @DeleteMapping("/{symbol}")
  @Operation(summary = "Invalidate all cache for a symbol")
  public ResponseEntity<Map<String, Object>> invalidateSymbol(@PathVariable String symbol) {
    if (!marketCacheService.isEnabled()) {
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
          .body(Map.of("error", "Cache is disabled"));
    }

    marketCacheService.invalidatePrice(symbol);
    marketCacheService.invalidateCandles(symbol);
    log.info("✗ All cache invalidated for symbol: {}", symbol);
    return ResponseEntity.ok(Map.of(
        "message", "All cache invalidated for symbol: " + symbol,
        "symbol", symbol,
        "invalidated", new String[] { "price", "candles" }));
  }

  /**
   * Invalidate all cached candles across all symbols.
   */
  @DeleteMapping("/candles")
  @Operation(summary = "Invalidate all candle caches")
  public ResponseEntity<Map<String, Object>> invalidateAllCandles() {
    if (!marketCacheService.isEnabled()) {
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
          .body(Map.of("error", "Cache is disabled"));
    }

    marketCacheService.invalidateAllCandles();
    log.info("✗ All candle caches invalidated");
    return ResponseEntity.ok(Map.of(
        "message", "All candle caches have been invalidated"));
  }

  /**
   * Clear all cached market data (prices and candles).
   * Emergency operation - use with caution.
   */
  @DeleteMapping
  @Operation(summary = "Clear all market cache")
  public ResponseEntity<Map<String, Object>> invalidateAll() {
    if (!marketCacheService.isEnabled()) {
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
          .body(Map.of("error", "Cache is disabled"));
    }

    marketCacheService.invalidateAll();
    log.warn("✗ EMERGENCY: All market caches cleared");
    return ResponseEntity.ok(Map.of(
        "message", "All market cache entries have been cleared",
        "warning", "This is an emergency cache clear operation"));
  }
}
