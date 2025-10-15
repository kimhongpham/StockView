package com.recognition.service;

import com.recognition.entity.Asset;
import com.recognition.entity.Price;
import com.recognition.exception.ResourceNotFoundException;
import com.recognition.repository.AssetRepository;
import com.recognition.repository.PriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AssetService {

    private final AssetRepository assetRepository;
    private final PriceRepository priceRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${FINNHUB_API_KEY}")
    private String finnhubApiKey;

    /**
     * Lấy thông tin chi tiết asset theo symbol
     */
    public Map<String, Object> getAssetDetails(String code) {
        Asset asset = assetRepository.findBySymbolIgnoreCase(code)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found: " + code));

        Map<String, Object> result = new HashMap<>();
        result.put("symbol", asset.getSymbol());
        result.put("name", asset.getName());
        result.put("description", asset.getDescription());

        try {
            String url = "https://finnhub.io/api/v1/quote?symbol=" + code + "&token=" + finnhubApiKey;
            Map<String, Object> apiResponse = restTemplate.getForObject(url, Map.class);

            if (apiResponse != null && !apiResponse.isEmpty() && ((Number) apiResponse.get("c")).doubleValue() != 0.0) {
                result.put("currentPrice", apiResponse.get("c"));
                result.put("high", apiResponse.get("h"));
                result.put("low", apiResponse.get("l"));
                result.put("open", apiResponse.get("o"));
                result.put("previousClose", apiResponse.get("pc"));
                result.put("timestamp", apiResponse.get("t"));
            } else {
                // fallback DB
                Price price = priceRepository.findTopByAssetOrderByTimestampDesc(asset)
                        .orElseThrow(() -> new ResourceNotFoundException("No price data for asset: " + code));
                result.put("currentPrice", price.getPrice());
                result.put("high", price.getHigh24h());
                result.put("low", price.getLow24h());
                result.put("timestamp", price.getTimestamp());
            }

        } catch (Exception e) {
            log.error("Error fetching asset details for {}: {}", code, e.getMessage());
            Price price = priceRepository.findTopByAssetOrderByTimestampDesc(asset)
                    .orElseThrow(() -> new ResourceNotFoundException("No price data for asset: " + code));
            result.put("currentPrice", price.getPrice());
            result.put("high", price.getHigh24h());
            result.put("low", price.getLow24h());
            result.put("timestamp", price.getTimestamp());
        }

        return result;
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
            String url = "https://finnhub.io/api/v1/stock/symbol?exchange=US&token=" + finnhubApiKey;
            List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);

            if (response == null || response.isEmpty()) {
                log.warn("Finnhub returned empty stock list.");
                return Collections.emptyList();
            }

            // Lọc top 10 symbol hợp lệ
            return response.stream()
                    .filter(stock -> stock.get("symbol") != null)
                    .limit(10)
                    .toList();

        } catch (Exception e) {
            log.error("Error fetching market stocks from Finnhub: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
