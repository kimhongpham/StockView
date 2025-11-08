package com.recognition.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinnhubClient {

    private static final String FINNHUB_BASE_URL = "https://finnhub.io/api/v1";
    private static final String QUOTE_ENDPOINT = "/quote";
    private static final String COMPANY_PROFILE_ENDPOINT = "/stock/profile2";

    private final RestTemplate restTemplate;

    @Value("${finnhub.api.key}")
    private String apiToken;

    /**
     * Lấy giá hiện tại của cổ phiếu từ Finnhub.
     */
    public BigDecimal fetchPrice(String symbol) {
        String url = UriComponentsBuilder.fromHttpUrl(FINNHUB_BASE_URL + QUOTE_ENDPOINT)
                .queryParam("symbol", symbol)
                .queryParam("token", apiToken)
                .toUriString();

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getBody() == null || response.getBody().get("c") == null) {
                log.warn("No price found for symbol: {}", symbol);
                return null;
            }

            BigDecimal price = new BigDecimal(response.getBody().get("c").toString());
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("Invalid price value ({}) for symbol: {}", price, symbol);
                return null;
            }

            return price;
        } catch (Exception e) {
            log.error("Error fetching price from Finnhub for symbol {}: {}", symbol, e.getMessage());
            return null;
        }
    }

    /**
     * Lấy thông tin công ty theo mã cổ phiếu.
     */
    public Map<String, Object> fetchCompanyProfile(String symbol) {
        String url = UriComponentsBuilder.fromHttpUrl(FINNHUB_BASE_URL + COMPANY_PROFILE_ENDPOINT)
                .queryParam("symbol", symbol)
                .queryParam("token", apiToken)
                .toUriString();

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            log.info("Fetched company profile for {} successfully.", symbol);
            return response;
        } catch (Exception e) {
            log.error("Error fetching company profile for symbol {}: {}", symbol, e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> fetchMarketSymbols(String exchange) {
        String url = UriComponentsBuilder.fromHttpUrl(FINNHUB_BASE_URL + "/stock/symbol")
                .queryParam("exchange", exchange)
                .queryParam("token", apiToken)
                .toUriString();

        try {
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error fetching market symbols for exchange {}: {}", exchange, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Lấy thông tin chỉ số tài chính (P/E, P/B, ROE, Dividend Yield...) của cổ phiếu.
     */
    public Map<String, Object> fetchStockMetric(String symbol) {
        String url = UriComponentsBuilder
                .fromHttpUrl(FINNHUB_BASE_URL + "/stock/metric")
                .queryParam("symbol", symbol)
                .queryParam("metric", "all")
                .queryParam("token", apiToken)
                .toUriString();

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null || response.get("metric") == null) {
                log.warn("No financial metrics found for symbol: {}", symbol);
                return Collections.emptyMap();
            }

            // Trả về phần metric (Finnhub trả về {"metric": {...}, "metricType": "all"})
            return (Map<String, Object>) response.get("metric");
        } catch (Exception e) {
            log.error("Error fetching stock metrics for {}: {}", symbol, e.getMessage());
            return Collections.emptyMap();
        }
    }

    /**
     * Lấy khối lượng giao dịch hiện tại (volume) của cổ phiếu.
     */
    public BigDecimal fetchQuoteVolume(String symbol) {
        String url = UriComponentsBuilder
                .fromHttpUrl(FINNHUB_BASE_URL + QUOTE_ENDPOINT)
                .queryParam("symbol", symbol)
                .queryParam("token", apiToken)
                .toUriString();

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null || response.get("v") == null) {
                log.warn("No volume found for symbol: {}", symbol);
                return null;
            }

            return new BigDecimal(response.get("v").toString());
        } catch (Exception e) {
            log.error("Error fetching volume for symbol {}: {}", symbol, e.getMessage());
            return null;
        }
    }

    /**
     * 🚀 Lấy giá cho toàn bộ danh sách symbol (gom 1 lần)
     * Backend chỉ cần gọi 1 lần từ FE.
     */
    public Map<String, BigDecimal> fetchAllPrices(List<String> symbols) {
        Map<String, BigDecimal> result = new java.util.concurrent.ConcurrentHashMap<>();

        symbols.parallelStream().forEach(symbol -> {
            try {
                BigDecimal price = fetchPrice(symbol);
                if (price != null) {
                    result.put(symbol, price);
                }
                // Nếu sợ rate-limit, thêm delay nhẹ 100-150ms
                // Thread.sleep(150);
            } catch (Exception e) {
                log.warn("Failed to fetch price for {}: {}", symbol, e.getMessage());
            }
        });

        return result;
    }
}
