package com.recognition.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FinnhubClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${finnhub.api.key}")
    private String apiToken;

    public BigDecimal fetchPrice(String symbol) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl("https://finnhub.io/api/v1/quote")
                    .queryParam("symbol", symbol)
                    .queryParam("token", apiToken)
                    .toUriString();

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null || response.get("c") == null) return null;

            BigDecimal price = new BigDecimal(response.get("c").toString());
            if (price.compareTo(BigDecimal.ZERO) <= 0) return null;

            return price;
        } catch (Exception e) {
            return null; // tránh throw để controller có thể gom lỗi
        }
    }
}
