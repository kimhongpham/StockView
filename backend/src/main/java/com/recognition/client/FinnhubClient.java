package com.recognition.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;

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

    // Lấy giá hiện tại cổ phiếu từ Finnhub
    public BigDecimal fetchPrice(String symbol) {
        String url = UriComponentsBuilder.fromHttpUrl(FINNHUB_BASE_URL + QUOTE_ENDPOINT)
                .queryParam("symbol", symbol)
                .queryParam("token", apiToken)
                .toUriString();

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getBody() == null || response.getBody().get("c") == null) {
                log.warn(" No price found for symbol: {}", symbol);
                return null;
            }

            BigDecimal price = new BigDecimal(response.getBody().get("c").toString());
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                log.warn(" Invalid price value ({}) for symbol: {}", price, symbol);
                return null;
            }

            return price;
        } catch (Exception e) {
            log.error(" Error fetching price for {}: {}", symbol, e.getMessage());
            return null;
        }
    }

    // Lấy danh sách mã cổ phiếu theo sàn
    public List<Map<String, Object>> fetchMarketSymbols(String exchange) {
        String url = UriComponentsBuilder.fromHttpUrl(FINNHUB_BASE_URL + "/stock/symbol")
                .queryParam("exchange", exchange)
                .queryParam("token", apiToken)
                .toUriString();

        try {
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            return response.getBody();
        } catch (Exception e) {
            log.error(" Error fetching market symbols for exchange {}: {}", exchange, e.getMessage());
            return Collections.emptyList();
        }
    }

    // Lấy thông tin chỉ số tài chính (P/E, P/B, ROE, Dividend Yield...) của cổ phiếu.
    public Map<String, Object> fetchStockMetrics(String symbol) {
        String url = String.format(
                "%s/stock/metric?symbol=%s&metric=all&token=%s",
                FINNHUB_BASE_URL, symbol, apiToken
        );
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return (Map<String, Object>) response.getBody().get("metric");
        } catch (Exception e) {
            log.warn(" Failed to fetch metrics for {}: {}", symbol, e.getMessage());
            return Collections.emptyMap();
        }
    }

    // Lấy khối lượng giao dịch hiện tại của cổ phiếu
    public BigDecimal fetchQuoteVolume(String symbol) {
        String url = UriComponentsBuilder
                .fromHttpUrl(FINNHUB_BASE_URL + QUOTE_ENDPOINT)
                .queryParam("symbol", symbol)
                .queryParam("token", apiToken)
                .toUriString();

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null || response.get("v") == null) {
                log.warn(" No volume found for symbol: {}", symbol);
                return null;
            }

            return new BigDecimal(response.get("v").toString());
        } catch (Exception e) {
            log.error(" Error fetching volume for {}: {}", symbol, e.getMessage());
            return null;
        }
    }

    // Lấy giá cho toàn bộ danh sách cổ phiếu theo mã cổ phiếu
    public Map<String, BigDecimal> fetchAllPrices(List<String> symbols) {
        Map<String, BigDecimal> result = new ConcurrentHashMap<>();

        if (symbols == null || symbols.isEmpty()) {
            return result;
        }

        int threadLimit = Math.min(5, symbols.size()); // Giới hạn 5 luồng để tránh bị Finnhub chặn
        ExecutorService executor = Executors.newFixedThreadPool(threadLimit);

        long startTime = System.currentTimeMillis();

        try {
            List<Future<?>> futures = new ArrayList<>();
            for (String symbol : symbols) {
                futures.add(executor.submit(() -> {
                    try {
                        BigDecimal price = fetchPrice(symbol);
                        if (price != null) {
                            result.put(symbol, price);
                        }
                        Thread.sleep(100);
                    } catch (Exception ex) {
                        log.warn(" Failed to fetch price for {}: {}", symbol, ex.getMessage());
                    }
                }));
            }

            for (Future<?> f : futures) {
                try {
                    f.get(15, TimeUnit.SECONDS); // timeout riêng từng tác vụ
                } catch (TimeoutException e) {
                    log.warn(" Timeout fetching one of symbols");
                }
            }

        } catch (Exception e) {
            log.error(" Error in fetchAllPrices: {}", e.getMessage());
        } finally {
            executor.shutdown();
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info(" fetchAllPrices completed for {} symbols in {} ms", symbols.size(), duration);

        return result;
    }
}
