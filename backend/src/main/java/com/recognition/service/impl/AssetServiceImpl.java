package com.recognition.service.impl;

import com.recognition.client.FinnhubClient;
import com.recognition.dto.PriceDto;
import com.recognition.entity.Asset;
import com.recognition.entity.Price;
import com.recognition.exception.ResourceNotFoundException;
import com.recognition.repository.AssetRepository;
import com.recognition.repository.PriceRepository;
import com.recognition.service.AssetService;
import com.recognition.service.PriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;
    private final PriceRepository priceRepository;
    private final FinnhubClient finnhubClient;
    private final PriceService priceService;

    @Override
    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    @Override
    public Map<String, Object> getAssetOverview(String code) {
        // Tìm asset trong DB
        Asset asset;
        if (code.matches("^[0-9a-fA-F\\-]{36}$")) {
            asset = assetRepository.findById(UUID.fromString(code))
                    .orElseThrow(() -> new ResourceNotFoundException("Asset not found (id): " + code));
        } else {
            asset = assetRepository.findBySymbolIgnoreCase(code)
                    .orElseThrow(() -> new ResourceNotFoundException("Asset not found (symbol): " + code));
        }

        // Tạo map kết quả cơ bản
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", asset.getId());
        result.put("symbol", asset.getSymbol());
        result.put("name", asset.getName());
        result.put("description", asset.getDescription());
        result.put("isActive", asset.getIsActive());
        result.put("createdAt", asset.getCreatedAt());
        result.put("updatedAt", asset.getUpdatedAt());

        // Lấy bản ghi giá mới nhất
        Price latestPrice = priceRepository
                .findTopByAssetOrderByTimestampDesc(asset)
                .orElse(null);

        if (latestPrice != null) {
            result.put("currentPrice", latestPrice.getPrice());
            result.put("changePercent", latestPrice.getChangePercent());
            result.put("volume", latestPrice.getVolume());
            result.put("high24h", latestPrice.getHigh24h());
            result.put("low24h", latestPrice.getLow24h());
            result.put("marketCap", latestPrice.getMarketCap());
            result.put("timestamp", latestPrice.getTimestamp());
            result.put("source", latestPrice.getSource());
        } else {
            result.put("currentPrice", null);
            result.put("source", "Database (no price yet)");
        }

// 🔹 Bổ sung thêm metrics từ Asset
        result.put("marketCap_static", asset.getMarketCap());
        result.put("peRatio", asset.getPeRatio());
        result.put("pbRatio", asset.getPbRatio());
        result.put("eps", asset.getEps());
        result.put("bookValue", asset.getBookValue());
        result.put("evToEbitda", asset.getEvToEbitda());
        result.put("sharesOutstanding", asset.getSharesOutstanding());

        return result;
    }

    @Override
    public boolean existsBySymbol(String symbol) {
        return assetRepository.existsBySymbol(symbol);
    }

    @Override
    public List<Asset> searchAssets(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return assetRepository.findBySymbolContainingIgnoreCaseOrNameContainingIgnoreCase(query, query);
    }

    @Override
    @Transactional
    public void deleteAsset(UUID assetId) {
        log.info("Attempting to delete asset with ID: {}", assetId);
        try {
            if (!assetRepository.existsById(assetId)) {
                log.warn("Asset not found: {}", assetId);
                throw new ResourceNotFoundException("Asset not found with ID: " + assetId);
            }

            priceRepository.deleteAllByAssetId(assetId);
            log.info("Deleted all prices linked to asset {}", assetId);

            assetRepository.deleteById(assetId);
            log.info("Asset deleted successfully: {}", assetId);

        } catch (DataIntegrityViolationException e) {
            log.error("Constraint violation while deleting asset {}: {}", assetId, e.getMessage());
            throw new RuntimeException("Cannot delete asset due to existing references (FK constraint)");
        } catch (Exception e) {
            log.error("Unexpected error while deleting asset {}: {}", assetId, e.getMessage(), e);
            throw new RuntimeException("Failed to delete asset: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public List<Map<String, Object>> fetchNewMarketStocks(int limit) {
        List<Map<String, Object>> response = finnhubClient.fetchMarketSymbols("US");

        if (response == null || response.isEmpty()) {
            log.warn("Finnhub returned empty stock list.");
            return Collections.emptyList();
        }

        List<Map<String, Object>> newStocks = response.stream()
                .filter(stock -> stock.get("symbol") != null)
                .filter(stock -> !assetRepository.existsBySymbol(stock.get("symbol").toString()))
                .limit(limit)
                .toList();

        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> stock : newStocks) {
            String symbol = stock.get("symbol").toString();
            try {
                enrichAndSaveStock(stock, symbol, result);
            } catch (Exception ex) {
                log.warn("Error fetching quote for {}: {}", symbol, ex.getMessage());
            }
        }

        log.info("✅ Added {} new assets to database.", result.size());
        return result;
    }

    private void enrichAndSaveStock(Map<String, Object> stock, String symbol, List<Map<String, Object>> result) {
        BigDecimal currentPrice = finnhubClient.fetchPrice(symbol);
        if (currentPrice == null) {
            log.warn("No valid price returned for {}", symbol);
            return;
        }

        Map<String, Object> quote = new HashMap<>();
        quote.put("c", currentPrice);
        quote.put("h", currentPrice);
        quote.put("l", currentPrice);
        quote.put("t", Instant.now().getEpochSecond());

        // Lấy hoặc tạo mới Asset
        Asset asset = assetRepository.findBySymbol(symbol)
                .orElseGet(() -> assetRepository.save(
                        Asset.builder()
                                .name(stock.getOrDefault("description", symbol).toString())
                                .symbol(symbol)
                                .description(stock.getOrDefault("type", "").toString())
                                .isActive(true)
                                .build()
                ));

        // ✅ Lấy thêm metrics từ Finnhub (nếu có)
        Map<String, Object> metrics = finnhubClient.fetchStockMetrics(symbol);
        if (metrics != null && !metrics.isEmpty()) {
            try {
                asset.setMarketCap(toBigDecimal(metrics.get("marketCapitalization")));
                asset.setVolume(toBigDecimal(metrics.get("volume")));
                asset.setSharesOutstanding(toBigDecimal(metrics.get("shareOutstanding")));
                asset.setPeRatio(toBigDecimal(metrics.get("peNormalizedAnnual")));
                asset.setPbRatio(toBigDecimal(metrics.get("pbAnnual")));
                asset.setEvToEbitda(toBigDecimal(metrics.get("evToEbitdaAnnual")));
                asset.setEps(toBigDecimal(metrics.get("epsAnnual")));
                asset.setBookValue(toBigDecimal(metrics.get("bookValuePerShareAnnual")));
                assetRepository.save(asset);
            } catch (Exception e) {
                log.warn("Error enriching asset metrics for {}: {}", symbol, e.getMessage());
            }
        }

        // Xử lý timestamp hợp lệ
        long ts = ((Number) quote.get("t")).longValue();
        if (ts == 0) {
            log.warn("Skipping invalid timestamp (0) for {}", symbol);
            return;
        }

        OffsetDateTime timestamp = OffsetDateTime.ofInstant(Instant.ofEpochSecond(ts), ZoneOffset.UTC);

        // Kiểm tra trùng khóa
        Optional<Price> existing = priceRepository.findByAssetAndTimestampAndSource(asset, timestamp, "Finnhub");
        if (existing.isPresent()) {
            log.info("Price already exists for {} at {}, skipping.", symbol, timestamp);
            return;
        }

        Price price = Price.builder()
                .asset(asset)
                .price(new BigDecimal(quote.get("c").toString()))
                .high24h(new BigDecimal(quote.get("h").toString()))
                .low24h(new BigDecimal(quote.get("l").toString()))
                .timestamp(timestamp)
                .source("Finnhub")
                .build();

        try {
            priceRepository.save(price);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Duplicate price ignored for asset {} at {}", asset.getSymbol(), price.getTimestamp());
        }

        // Kết quả trả ra
        Map<String, Object> enriched = new LinkedHashMap<>(stock);
        enriched.put("assetId", asset.getId());
        enriched.put("price", quote.get("c"));
        enriched.put("high24h", quote.get("h"));
        enriched.put("low24h", quote.get("l"));
        enriched.put("timestamp", price.getTimestamp().toString());
        enriched.put("marketCap", asset.getMarketCap());
        enriched.put("volume", asset.getVolume());
        enriched.put("pe", asset.getPeRatio());
        enriched.put("pb", asset.getPbRatio());

        result.add(enriched);
    }

    @Override
    @Transactional
    public Price fetchAndSavePrice(UUID assetId) {
        PriceDto priceDto = priceService.fetchAndSavePrice(assetId);

        return priceRepository.findById(priceDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Price not found after save: " + priceDto.getId()));
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return null;

        return switch (value) {
            case BigDecimal bd -> bd;
            case Integer i -> BigDecimal.valueOf(i);
            case Long l -> BigDecimal.valueOf(l);
            case Float f -> new BigDecimal(Float.toString(f));
            case Double d -> BigDecimal.valueOf(d);
            case Number n -> new BigDecimal(n.toString());
            default -> {
                try {
                    yield new BigDecimal(value.toString());
                } catch (NumberFormatException e) {
                    yield null;
                }
            }
        };
    }
}
