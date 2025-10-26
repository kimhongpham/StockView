package com.recognition.service.impl;

import com.recognition.entity.Asset;
import com.recognition.entity.Price;
import com.recognition.exception.ResourceNotFoundException;
import com.recognition.repository.AssetRepository;
import com.recognition.repository.PriceRepository;
import com.recognition.service.AssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${FINNHUB_API_KEY}")
    private String finnhubApiKey;

    @Override
    public Map<String, Object> getAssetDetails(String code) {
        Asset asset;
        if (code.matches("^[0-9a-fA-F\\-]{36}$")) {
            asset = assetRepository.findById(UUID.fromString(code))
                    .orElseThrow(() -> new ResourceNotFoundException("Asset not found (id): " + code));
        } else {
            asset = assetRepository.findBySymbolIgnoreCase(code)
                    .orElseThrow(() -> new ResourceNotFoundException("Asset not found (symbol): " + code));
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", asset.getId());
        result.put("symbol", asset.getSymbol());
        result.put("name", asset.getName());
        result.put("description", asset.getDescription());

        try {
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
                log.warn("Finnhub returned empty or zero price for {}", asset.getSymbol());
                populatePriceFromDB(asset, result);
            }

        } catch (Exception e) {
            log.error("Error fetching asset details for {}: {}", asset.getSymbol(), e.getMessage());
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

    @Override
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

    @Override
    public boolean existsBySymbol(String symbol) {
        return assetRepository.existsBySymbol(symbol);
    }

    @Override
    @Transactional
    public void deleteAsset(UUID assetId) {
        if (!assetRepository.existsById(assetId)) {
            throw new ResourceNotFoundException("Asset not found with ID: " + assetId);
        }
        assetRepository.deleteById(assetId);
    }

    @Override
    public List<Map<String, Object>> getMarketStocks() {
        try {
            String url = "https://finnhub.io/api/v1/stock/symbol?exchange=US&token=" + finnhubApiKey;
            List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);

            if (response == null || response.isEmpty()) {
                log.warn("⚠️ Finnhub returned empty stock list.");
                return Collections.emptyList();
            }

            List<Map<String, Object>> topStocks = response.stream()
                    .filter(stock -> stock.get("symbol") != null)
                    .limit(10)
                    .toList();

            List<Map<String, Object>> result = new ArrayList<>();

            for (Map<String, Object> stock : topStocks) {
                String symbol = stock.get("symbol").toString();
                try {
                    enrichAndSaveStock(stock, symbol, result);
                } catch (Exception ex) {
                    log.warn("Error fetching quote for symbol {}: {}", symbol, ex.getMessage());
                }
            }

            log.info("Successfully fetched and stored {} market stocks.", result.size());
            return result;

        } catch (Exception e) {
            log.error("Error fetching market stocks: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional
    public List<Map<String, Object>> fetchNewMarketStocks(int limit) {
        String url = "https://finnhub.io/api/v1/stock/symbol?exchange=US&token=" + finnhubApiKey;
        List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);

        if (response == null || response.isEmpty()) {
            log.warn("⚠️ Finnhub returned empty stock list.");
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

        log.info("Successfully fetched and stored {} new market stocks.", result.size());
        return result;
    }

    private void enrichAndSaveStock(Map<String, Object> stock, String symbol, List<Map<String, Object>> result) {
        String quoteUrl = String.format("https://finnhub.io/api/v1/quote?symbol=%s&token=%s", symbol, finnhubApiKey);
        Map<String, Object> quote = restTemplate.getForObject(quoteUrl, Map.class);

        if (quote == null || quote.get("c") == null) return;

        Asset asset = assetRepository.findBySymbol(symbol)
                .orElseGet(() -> assetRepository.save(
                        Asset.builder()
                                .name(stock.getOrDefault("description", symbol).toString())
                                .symbol(symbol)
                                .description(stock.getOrDefault("type", "").toString())
                                .isActive(true)
                                .build()
                ));

        Price price = Price.builder()
                .asset(asset)
                .price(new BigDecimal(quote.get("c").toString()))
                .high24h(new BigDecimal(quote.get("h").toString()))
                .low24h(new BigDecimal(quote.get("l").toString()))
                .timestamp(OffsetDateTime.ofInstant(
                        Instant.ofEpochSecond(((Number) quote.get("t")).longValue()), ZoneOffset.UTC))
                .source("Finnhub")
                .build();

        try {
            priceRepository.save(price);
        } catch (DataIntegrityViolationException ex) {
            log.warn("⚠️ Duplicate price ignored for asset {} at {}", asset.getSymbol(), price.getTimestamp());
        }

        Map<String, Object> enriched = new LinkedHashMap<>(stock);
        enriched.put("assetId", asset.getId());
        enriched.put("price", quote.get("c"));
        enriched.put("high24h", quote.get("h"));
        enriched.put("low24h", quote.get("l"));
        enriched.put("timestamp", price.getTimestamp().toString());
        result.add(enriched);
    }

    @Override
    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }
}
