package com.recognition.service;

import com.recognition.entity.Asset;
import com.recognition.entity.Price;
import com.recognition.exception.ResourceNotFoundException;
import com.recognition.repository.AssetRepository;
import com.recognition.repository.PriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AssetService {

    @Autowired
    private final AssetRepository assetRepository;
    private final PriceRepository priceRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${FINNHUB_API_KEY}")
    private String finnhubApiKey;

    /**
     * Lấy thông tin chi tiết asset theo symbol
     */
    public Map<String, Object> getAssetDetails(String code) {
        Asset asset;

        // 1️⃣ Kiểm tra xem code là UUID hay symbol
        if (code.matches("^[0-9a-fA-F\\-]{36}$")) {
            // Nếu là UUID → tìm theo id
            asset = assetRepository.findById(UUID.fromString(code))
                    .orElseThrow(() -> new ResourceNotFoundException("Asset not found (id): " + code));
        } else {
            // Nếu là symbol → tìm theo symbol (ignore case)
            asset = assetRepository.findBySymbolIgnoreCase(code)
                    .orElseThrow(() -> new ResourceNotFoundException("Asset not found (symbol): " + code));
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", asset.getId());
        result.put("symbol", asset.getSymbol());
        result.put("name", asset.getName());
        result.put("description", asset.getDescription());

        try {
            // 2️⃣ Gọi Finnhub để lấy giá mới nhất
            String url = "https://finnhub.io/api/v1/quote?symbol=" + asset.getSymbol() + "&token=" + finnhubApiKey;
            Map<String, Object> apiResponse = restTemplate.getForObject(url, Map.class);

            if (apiResponse != null && !apiResponse.isEmpty() && ((Number) apiResponse.get("c")).doubleValue() != 0.0) {
                result.put("source", "Finnhub");
                result.put("currentPrice", apiResponse.get("c"));
                result.put("high", apiResponse.get("h"));
                result.put("low", apiResponse.get("l"));
                result.put("open", apiResponse.get("o"));
                result.put("previousClose", apiResponse.get("pc"));
                result.put("timestamp",
                        Instant.ofEpochSecond(((Number) apiResponse.get("t")).longValue()).toString());
            } else {
                log.warn("⚠️ Finnhub returned empty or zero price for {}", asset.getSymbol());
                populatePriceFromDB(asset, result);
            }

        } catch (Exception e) {
            log.error("❌ Error fetching asset details for {}: {}", asset.getSymbol(), e.getMessage());
            populatePriceFromDB(asset, result);
        }

        return result;
    }

    private void populatePriceFromDB(Asset asset, Map<String, Object> result) {
        Price price = priceRepository.findTopByAssetOrderByTimestampDesc(asset)
                .orElse(null);

        if (price != null) {
            result.put("source", "Database");
            result.put("currentPrice", price.getPrice());
            result.put("high", price.getHigh24h());
            result.put("low", price.getLow24h());
            result.put("timestamp", price.getTimestamp());
        } else {
            result.put("source", "None");
            result.put("currentPrice", null);
        }
    }

    /**
     * Fetch và lưu giá mới cho asset
     */
    @Transactional
    public Price fetchAndSavePrice(UUID assetId) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found: " + assetId));

        BigDecimal priceValue;
        String source;

        try {
            priceValue = fetchPriceFromFinnhub(asset.getSymbol());
            source = "finnhub-api";
        } catch (Exception e) {
            Price lastPrice = priceRepository.findTopByAssetOrderByTimestampDesc(asset)
                    .orElseThrow(() -> new ResourceNotFoundException("No price available for asset"));
            priceValue = lastPrice.getPrice();
            source = "mock-db";
        }

        Price price = new Price();
        price.setAsset(asset);
        price.setPrice(priceValue);
        price.setTimestamp(OffsetDateTime.now());
        price.setSource(source);
        return priceRepository.save(price);
    }

    private BigDecimal fetchPriceFromFinnhub(String symbol) {
        String url = "https://finnhub.io/api/v1/quote?symbol=" + symbol + "&token=" + finnhubApiKey;
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null || response.isEmpty() || response.get("c") == null) {
            throw new RuntimeException("Finnhub API returned no price for " + symbol);
        }

        return new BigDecimal(response.get("c").toString());
    }

    public boolean existsBySymbol(String symbol) {
        return assetRepository.existsBySymbol(symbol);
    }

    @Transactional
    public void deleteAsset(UUID assetId) {
        if (!assetRepository.existsById(assetId)) {
            throw new ResourceNotFoundException("Asset not found with ID: " + assetId);
        }
        assetRepository.deleteById(assetId);
    }

    public List<Map<String, Object>> getMarketStocks() {
        try {
            // 1️⃣ Lấy danh sách cổ phiếu từ Finnhub
            String url = "https://finnhub.io/api/v1/stock/symbol?exchange=US&token=" + finnhubApiKey;
            List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);

            if (response == null || response.isEmpty()) {
                log.warn("⚠️ Finnhub returned empty stock list.");
                return Collections.emptyList();
            }

            // 2️⃣ Lọc top 10 cổ phiếu có symbol hợp lệ
            List<Map<String, Object>> topStocks = response.stream()
                    .filter(stock -> stock.get("symbol") != null)
                    .limit(10)
                    .toList();

            List<Map<String, Object>> result = new ArrayList<>();

            for (Map<String, Object> stock : topStocks) {
                String symbol = stock.get("symbol").toString();

                try {
                    // 3️⃣ Lấy quote (giá) từng cổ phiếu
                    String quoteUrl = String.format("https://finnhub.io/api/v1/quote?symbol=%s&token=%s", symbol, finnhubApiKey);
                    Map<String, Object> quote = restTemplate.getForObject(quoteUrl, Map.class);

                    if (quote == null || quote.get("c") == null) continue;

                    // 4️⃣ Tìm asset theo symbol, nếu chưa có thì tự tạo
                    Asset asset = assetRepository.findBySymbol(symbol)
                            .orElseGet(() -> {
                                Asset newAsset = Asset.builder()
                                        .name(stock.getOrDefault("description", symbol).toString())
                                        .symbol(symbol)
                                        .description(stock.getOrDefault("type", "").toString())
                                        .isActive(true)
                                        .build();
                                Asset saved = assetRepository.save(newAsset);
                                log.info("🆕 Created new asset: {} ({})", saved.getName(), saved.getSymbol());
                                return saved;
                            });

                    // 5️⃣ Gộp thông tin trả về
                    Map<String, Object> enriched = new LinkedHashMap<>(stock);
                    enriched.put("assetId", asset.getId());
                    enriched.put("price", quote.get("c"));
                    enriched.put("high24h", quote.get("h"));
                    enriched.put("low24h", quote.get("l"));
                    enriched.put("timestamp", Instant.ofEpochSecond(((Number) quote.get("t")).longValue()).toString());

                    result.add(enriched);

                } catch (Exception ex) {
                    log.warn("⚠️ Error fetching quote for symbol {}: {}", symbol, ex.getMessage());
                }
            }

            log.info("✅ Successfully fetched {} market stocks from Finnhub.", result.size());
            return result;

        } catch (Exception e) {
            log.error("❌ Error fetching market stocks from Finnhub: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

//    public Map<String, Object> getMarketSummary() {
//        Map<String, Object> summary = new HashMap<>();
//        summary.put("totalAssets", assetRepository.countByIsActiveTrue());
//        summary.put("averageChange", priceRepository.getAverageChangePercent());
//        summary.put("topGainer", priceRepository.findTopGainerSymbol());
//        summary.put("topLoser", priceRepository.findTopLoserSymbol());
//        return summary;
//    }
//
//    public List<Map<String, Object>> getTrendingAssets() {
//        return jdbcTemplate.queryForList("""
//        SELECT a.symbol, a.name, p.price, p.high_24h, p.low_24h,
//               p.change_percent, p.volume, p.timestamp
//        FROM latest_prices p
//        JOIN assets a ON p.asset_id = a.id
//        WHERE a.is_active = true
//        ORDER BY ABS(p.change_percent) DESC
//        LIMIT 10
//    """);
//    }
//
//    public List<Map<String, Object>> getTopGainers() {
//        return jdbcTemplate.queryForList("""
//        SELECT a.symbol, a.name, p.price, p.change_percent
//        FROM latest_prices p
//        JOIN assets a ON p.asset_id = a.id
//        WHERE a.is_active = true
//        ORDER BY p.change_percent DESC
//        LIMIT 5
//    """);
//    }
//
//    public List<Map<String, Object>> getTopLosers() {
//        return jdbcTemplate.queryForList("""
//        SELECT a.symbol, a.name, p.price, p.change_percent
//        FROM latest_prices p
//        JOIN assets a ON p.asset_id = a.id
//        WHERE a.is_active = true
//        ORDER BY p.change_percent ASC
//        LIMIT 5
//    """);
//    }
}
