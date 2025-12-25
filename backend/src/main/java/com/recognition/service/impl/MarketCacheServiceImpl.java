package com.recognition.service.impl;

import com.recognition.dto.CandleDTO;
import com.recognition.dto.PriceDto;
import com.recognition.service.MarketCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Redis-based implementation of MarketCacheService.
 * Uses Spring Data Redis for distributed caching of market data.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MarketCacheServiceImpl implements MarketCacheService {

  private final RedisTemplate<String, Object> redisTemplate;

  @Value("${cache.enabled:true}")
  private boolean cacheEnabled;

  @Value("${cache.price.ttl:30}")
  private long priceTtlSeconds;

  @Value("${cache.candle.ttl:300}")
  private long candleTtlSeconds;

  private static final String PRICE_CACHE_KEY_PREFIX = "market:";
  private static final String PRICE_CACHE_KEY_SUFFIX = ":price";
  private static final String CANDLE_CACHE_KEY_PREFIX = "market:";
  private static final String CANDLE_CACHE_KEY_SUFFIX = ":candles";

  @Override
  public Optional<PriceDto> getPrice(String symbol) {
    if (!cacheEnabled) {
      log.debug("Cache is disabled, skipping price lookup for {}", symbol);
      return Optional.empty();
    }

    try {
      String cacheKey = buildPriceCacheKey(symbol);
      Object cachedValue = redisTemplate.opsForValue().get(cacheKey);

      if (cachedValue instanceof PriceDto priceDto) {
        log.debug("✓ Cache HIT: price for {}", symbol);
        return Optional.of(priceDto);
      }

      log.debug("✗ Cache MISS: price for {}", symbol);
      return Optional.empty();
    } catch (Exception e) {
      log.warn("Error retrieving price from cache for {}: {}", symbol, e.getMessage());
      return Optional.empty();
    }
  }

  @Override
  public void setPrice(String symbol, PriceDto priceDto) {
    if (!cacheEnabled) {
      log.debug("Cache is disabled, skipping price storage for {}", symbol);
      return;
    }

    try {
      String cacheKey = buildPriceCacheKey(symbol);
      redisTemplate.opsForValue().set(cacheKey, priceDto, priceTtlSeconds, TimeUnit.SECONDS);
      log.debug("✓ Cached price for {} with TTL: {}s", symbol, priceTtlSeconds);
    } catch (Exception e) {
      log.warn("Error storing price in cache for {}: {}", symbol, e.getMessage());
    }
  }

  @Override
  public Optional<List<CandleDTO>> getCandles(String symbol, String interval) {
    if (!cacheEnabled) {
      log.debug("Cache is disabled, skipping candles lookup for {}", symbol);
      return Optional.empty();
    }

    try {
      String cacheKey = buildCandleCacheKey(symbol, interval);
      Object cachedValue = redisTemplate.opsForValue().get(cacheKey);

      if (cachedValue instanceof List<?> candles) {
        log.debug("✓ Cache HIT: candles for {} interval={}", symbol, interval);
        @SuppressWarnings("unchecked")
        List<CandleDTO> candleList = (List<CandleDTO>) candles;
        return Optional.of(candleList);
      }

      log.debug("✗ Cache MISS: candles for {} interval={}", symbol, interval);
      return Optional.empty();
    } catch (Exception e) {
      log.warn("Error retrieving candles from cache for {} interval={}: {}", symbol, interval, e.getMessage());
      return Optional.empty();
    }
  }

  @Override
  public void setCandles(String symbol, String interval, List<CandleDTO> candles) {
    if (!cacheEnabled) {
      log.debug("Cache is disabled, skipping candles storage for {}", symbol);
      return;
    }

    try {
      String cacheKey = buildCandleCacheKey(symbol, interval);
      redisTemplate.opsForValue().set(cacheKey, candles, candleTtlSeconds, TimeUnit.SECONDS);
      log.debug("✓ Cached {} candles for {} interval={} with TTL: {}s",
          candles.size(), symbol, interval, candleTtlSeconds);
    } catch (Exception e) {
      log.warn("Error storing candles in cache for {} interval={}: {}", symbol, interval, e.getMessage());
    }
  }

  @Override
  public void invalidatePrice(String symbol) {
    if (!cacheEnabled) {
      return;
    }

    try {
      String cacheKey = buildPriceCacheKey(symbol);
      Boolean deleted = redisTemplate.delete(cacheKey);
      log.debug("✗ Invalidated price for {} (deleted: {})", symbol, deleted);
    } catch (Exception e) {
      log.warn("Error invalidating price cache for {}: {}", symbol, e.getMessage());
    }
  }

  @Override
  public void invalidateCandles(String symbol) {
    if (!cacheEnabled) {
      return;
    }

    try {
      // Invalidate all candle intervals for this symbol
      String pattern = buildCandleCacheKeyPattern(symbol);
      var keys = redisTemplate.keys(pattern);

      if (keys != null && !keys.isEmpty()) {
        Long deletedCount = redisTemplate.delete(keys);
        log.debug("✗ Invalidated {} candle cache entries for {}", deletedCount, symbol);
      }
    } catch (Exception e) {
      log.warn("Error invalidating candles cache for {}: {}", symbol, e.getMessage());
    }
  }

  @Override
  public void invalidateAllCandles() {
    if (!cacheEnabled) {
      return;
    }

    try {
      String pattern = CANDLE_CACHE_KEY_PREFIX + "*" + CANDLE_CACHE_KEY_SUFFIX;
      var keys = redisTemplate.keys(pattern);

      if (keys != null && !keys.isEmpty()) {
        Long deletedCount = redisTemplate.delete(keys);
        log.debug("✗ Invalidated all {} candle cache entries", deletedCount);
      }
    } catch (Exception e) {
      log.warn("Error invalidating all candles cache: {}", e.getMessage());
    }
  }

  @Override
  public void invalidateAll() {
    if (!cacheEnabled) {
      return;
    }

    try {
      invalidateAllCandles();

      String pricePattern = PRICE_CACHE_KEY_PREFIX + "*" + PRICE_CACHE_KEY_SUFFIX;
      var keys = redisTemplate.keys(pricePattern);

      if (keys != null && !keys.isEmpty()) {
        Long deletedCount = redisTemplate.delete(keys);
        log.debug("✗ Invalidated all {} price cache entries", deletedCount);
      }

      log.info("✗ All market cache entries invalidated");
    } catch (Exception e) {
      log.warn("Error invalidating all cache: {}", e.getMessage());
    }
  }

  @Override
  public boolean isEnabled() {
    return cacheEnabled;
  }

  /**
   * Build cache key for price data.
   * Format: market:{symbol}:price
   */
  private String buildPriceCacheKey(String symbol) {
    return PRICE_CACHE_KEY_PREFIX + symbol.toUpperCase() + PRICE_CACHE_KEY_SUFFIX;
  }

  /**
   * Build cache key for candle data.
   * Format: market:{symbol}:candles:{interval}
   */
  private String buildCandleCacheKey(String symbol, String interval) {
    return CANDLE_CACHE_KEY_PREFIX + symbol.toUpperCase() + CANDLE_CACHE_KEY_SUFFIX + ":" + interval.toLowerCase();
  }

  /**
   * Build pattern for matching all candle keys for a symbol.
   * Format: market:{symbol}:candles:*
   */
  private String buildCandleCacheKeyPattern(String symbol) {
    return CANDLE_CACHE_KEY_PREFIX + symbol.toUpperCase() + CANDLE_CACHE_KEY_SUFFIX + ":*";
  }
}
