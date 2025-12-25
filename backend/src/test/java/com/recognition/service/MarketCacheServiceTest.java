package com.recognition.service;

import com.recognition.dto.CandleDTO;
import com.recognition.dto.PriceDto;
import com.recognition.service.impl.MarketCacheServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for MarketCacheService.
 * Tests Redis-based caching behavior including cache hits, misses, and TTL.
 */
@SpringBootTest
@ActiveProfiles("test")
@Slf4j
@DisplayName("Market Cache Service Integration Tests")
class MarketCacheServiceTest {

  @Autowired
  private MarketCacheService marketCacheService;

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  private static final String TEST_SYMBOL = "AAPL";
  private static final String TEST_SYMBOL_2 = "MSFT";
  private static final String TEST_INTERVAL = "1d";

  @BeforeEach
  void setUp() {
    // Clear all cache before each test
    redisTemplate.getConnectionFactory().getConnection().flushDb();
    log.info("Cache cleared before test");
  }

  @Test
  @DisplayName("Should return empty Optional on price cache miss")
  void testPriceCacheMiss() {
    // Act
    Optional<PriceDto> result = marketCacheService.getPrice(TEST_SYMBOL);

    // Assert
    assertFalse(result.isPresent(), "Cache should miss on first access");
    log.info("✓ Cache miss test passed");
  }

  @Test
  @DisplayName("Should cache and retrieve price successfully")
  void testPriceCacheHit() {
    // Arrange
    PriceDto testPrice = createTestPrice(TEST_SYMBOL);

    // Act
    marketCacheService.setPrice(TEST_SYMBOL, testPrice);
    Optional<PriceDto> cached = marketCacheService.getPrice(TEST_SYMBOL);

    // Assert
    assertTrue(cached.isPresent(), "Price should be cached");
    assertEquals(testPrice.getAssetSymbol(), cached.get().getAssetSymbol());
    assertEquals(testPrice.getPrice(), cached.get().getPrice());
    log.info("✓ Price cache hit test passed");
  }

  @Test
  @DisplayName("Should return empty Optional on candles cache miss")
  void testCandlesCacheMiss() {
    // Act
    Optional<List<CandleDTO>> result = marketCacheService.getCandles(TEST_SYMBOL, TEST_INTERVAL);

    // Assert
    assertFalse(result.isPresent(), "Cache should miss on first access");
    log.info("✓ Candles cache miss test passed");
  }

  @Test
  @DisplayName("Should cache and retrieve candles successfully")
  void testCandlesCacheHit() {
    // Arrange
    List<CandleDTO> testCandles = createTestCandles(5);

    // Act
    marketCacheService.setCandles(TEST_SYMBOL, TEST_INTERVAL, testCandles);
    Optional<List<CandleDTO>> cached = marketCacheService.getCandles(TEST_SYMBOL, TEST_INTERVAL);

    // Assert
    assertTrue(cached.isPresent(), "Candles should be cached");
    assertEquals(testCandles.size(), cached.get().size());
    log.info("✓ Candles cache hit test passed");
  }

  @Test
  @DisplayName("Should invalidate price cache")
  void testInvalidatePrice() {
    // Arrange
    PriceDto testPrice = createTestPrice(TEST_SYMBOL);
    marketCacheService.setPrice(TEST_SYMBOL, testPrice);

    // Verify cache was set
    Optional<PriceDto> beforeInvalidate = marketCacheService.getPrice(TEST_SYMBOL);
    assertTrue(beforeInvalidate.isPresent(), "Price should be cached before invalidation");

    // Act
    marketCacheService.invalidatePrice(TEST_SYMBOL);

    // Assert
    Optional<PriceDto> afterInvalidate = marketCacheService.getPrice(TEST_SYMBOL);
    assertFalse(afterInvalidate.isPresent(), "Price should be invalidated");
    log.info("✓ Price invalidation test passed");
  }

  @Test
  @DisplayName("Should invalidate all candles for a symbol")
  void testInvalidateCandles() {
    // Arrange
    List<CandleDTO> candles1d = createTestCandles(5);
    List<CandleDTO> candles1w = createTestCandles(10);

    marketCacheService.setCandles(TEST_SYMBOL, "1d", candles1d);
    marketCacheService.setCandles(TEST_SYMBOL, "1w", candles1w);

    // Verify caches were set
    assertTrue(marketCacheService.getCandles(TEST_SYMBOL, "1d").isPresent());
    assertTrue(marketCacheService.getCandles(TEST_SYMBOL, "1w").isPresent());

    // Act
    marketCacheService.invalidateCandles(TEST_SYMBOL);

    // Assert
    assertFalse(marketCacheService.getCandles(TEST_SYMBOL, "1d").isPresent(),
        "1d candles should be invalidated");
    assertFalse(marketCacheService.getCandles(TEST_SYMBOL, "1w").isPresent(),
        "1w candles should be invalidated");
    log.info("✓ Multiple candles invalidation test passed");
  }

  @Test
  @DisplayName("Should invalidate all candles across all symbols")
  void testInvalidateAllCandles() {
    // Arrange
    List<CandleDTO> candlesAAPL = createTestCandles(5);
    List<CandleDTO> candlesMSFT = createTestCandles(5);

    marketCacheService.setCandles(TEST_SYMBOL, TEST_INTERVAL, candlesAAPL);
    marketCacheService.setCandles(TEST_SYMBOL_2, TEST_INTERVAL, candlesMSFT);

    // Verify caches were set
    assertTrue(marketCacheService.getCandles(TEST_SYMBOL, TEST_INTERVAL).isPresent());
    assertTrue(marketCacheService.getCandles(TEST_SYMBOL_2, TEST_INTERVAL).isPresent());

    // Act
    marketCacheService.invalidateAllCandles();

    // Assert
    assertFalse(marketCacheService.getCandles(TEST_SYMBOL, TEST_INTERVAL).isPresent(),
        "AAPL candles should be invalidated");
    assertFalse(marketCacheService.getCandles(TEST_SYMBOL_2, TEST_INTERVAL).isPresent(),
        "MSFT candles should be invalidated");
    log.info("✓ All candles invalidation test passed");
  }

  @Test
  @DisplayName("Should invalidate all cache data")
  void testInvalidateAll() {
    // Arrange
    PriceDto testPrice = createTestPrice(TEST_SYMBOL);
    List<CandleDTO> testCandles = createTestCandles(5);

    marketCacheService.setPrice(TEST_SYMBOL, testPrice);
    marketCacheService.setCandles(TEST_SYMBOL, TEST_INTERVAL, testCandles);

    // Verify caches were set
    assertTrue(marketCacheService.getPrice(TEST_SYMBOL).isPresent());
    assertTrue(marketCacheService.getCandles(TEST_SYMBOL, TEST_INTERVAL).isPresent());

    // Act
    marketCacheService.invalidateAll();

    // Assert
    assertFalse(marketCacheService.getPrice(TEST_SYMBOL).isPresent(),
        "Price should be cleared");
    assertFalse(marketCacheService.getCandles(TEST_SYMBOL, TEST_INTERVAL).isPresent(),
        "Candles should be cleared");
    log.info("✓ All cache invalidation test passed");
  }

  @Test
  @DisplayName("Should isolate cache entries for different symbols")
  void testCacheIsolationBySymbol() {
    // Arrange
    PriceDto priceAAPL = createTestPrice(TEST_SYMBOL);
    PriceDto priceMSFT = createTestPrice(TEST_SYMBOL_2);

    // Act
    marketCacheService.setPrice(TEST_SYMBOL, priceAAPL);
    marketCacheService.setPrice(TEST_SYMBOL_2, priceMSFT);

    // Assert
    Optional<PriceDto> cachedAAPL = marketCacheService.getPrice(TEST_SYMBOL);
    Optional<PriceDto> cachedMSFT = marketCacheService.getPrice(TEST_SYMBOL_2);

    assertTrue(cachedAAPL.isPresent());
    assertTrue(cachedMSFT.isPresent());
    assertEquals(TEST_SYMBOL, cachedAAPL.get().getAssetSymbol());
    assertEquals(TEST_SYMBOL_2, cachedMSFT.get().getAssetSymbol());
    log.info("✓ Cache isolation by symbol test passed");
  }

  @Test
  @DisplayName("Should isolate cache entries for different intervals")
  void testCacheIsolationByInterval() {
    // Arrange
    List<CandleDTO> candles1d = createTestCandles(5);
    List<CandleDTO> candles1w = createTestCandles(10);

    // Act
    marketCacheService.setCandles(TEST_SYMBOL, "1d", candles1d);
    marketCacheService.setCandles(TEST_SYMBOL, "1w", candles1w);

    // Assert
    Optional<List<CandleDTO>> cached1d = marketCacheService.getCandles(TEST_SYMBOL, "1d");
    Optional<List<CandleDTO>> cached1w = marketCacheService.getCandles(TEST_SYMBOL, "1w");

    assertTrue(cached1d.isPresent());
    assertTrue(cached1w.isPresent());
    assertEquals(5, cached1d.get().size(), "1d candles should have 5 entries");
    assertEquals(10, cached1w.get().size(), "1w candles should have 10 entries");
    log.info("✓ Cache isolation by interval test passed");
  }

  @Test
  @DisplayName("Should handle case-insensitive symbol lookups")
  void testCaseInsensitiveSymbol() {
    // Arrange
    PriceDto testPrice = createTestPrice(TEST_SYMBOL);
    marketCacheService.setPrice(TEST_SYMBOL, testPrice);

    // Act
    Optional<PriceDto> lowercase = marketCacheService.getPrice(TEST_SYMBOL.toLowerCase());
    Optional<PriceDto> uppercase = marketCacheService.getPrice(TEST_SYMBOL.toUpperCase());

    // Assert
    assertTrue(lowercase.isPresent(), "Lowercase lookup should work");
    assertTrue(uppercase.isPresent(), "Uppercase lookup should work");
    log.info("✓ Case-insensitive symbol test passed");
  }

  @Test
  @DisplayName("Should report cache enabled status")
  void testCacheEnabledStatus() {
    // Act
    boolean enabled = marketCacheService.isEnabled();

    // Assert
    assertTrue(enabled, "Cache should be enabled in test profile");
    log.info("✓ Cache enabled status test passed");
  }

  /**
   * Helper method to create test price data.
   */
  private PriceDto createTestPrice(String symbol) {
    PriceDto price = new PriceDto();
    price.setAssetSymbol(symbol);
    price.setAssetName("Test Asset " + symbol);
    price.setPrice(BigDecimal.valueOf(150.50));
    price.setTimestamp(OffsetDateTime.now());
    price.setChangePercent(BigDecimal.valueOf(2.5));
    price.setVolume(BigDecimal.valueOf(1000000));
    return price;
  }

  /**
   * Helper method to create test candle data.
   */
  private List<CandleDTO> createTestCandles(int count) {
    return List.of(
        new CandleDTO(
            OffsetDateTime.now().minusHours(count),
            BigDecimal.valueOf(150.0),
            BigDecimal.valueOf(152.0),
            BigDecimal.valueOf(149.5),
            BigDecimal.valueOf(151.0),
            1000000L));
  }
}
