package com.recognition.service.impl;

import com.recognition.client.FinnhubClient;
import com.recognition.dto.CandleDTO;
import com.recognition.dto.PriceDto;
import com.recognition.dto.response.StatisticsDTO;
import com.recognition.entity.Asset;
import com.recognition.entity.Price;
import com.recognition.exception.ResourceNotFoundException;
import com.recognition.repository.AssetRepository;
import com.recognition.repository.PriceRepository;
import com.recognition.service.PriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PriceServiceImpl implements PriceService {

    private final PriceRepository priceRepository;
    private final AssetRepository assetRepository;
    private final FinnhubClient finnhubClient;

    @Value("${finnhub.api.key}")
    private String finnhubApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Page<Price> getPriceHistory(UUID assetId, OffsetDateTime startDate,
                                       OffsetDateTime endDate, Pageable pageable) {
        if (!assetRepository.existsById(assetId)) {
            throw new ResourceNotFoundException("Asset not found with ID: " + assetId);
        }

        if (startDate != null && endDate != null)
            return priceRepository.findByAssetIdAndTimestampBetween(assetId, startDate, endDate, pageable);
        else if (startDate != null)
            return priceRepository.findByAssetIdAndTimestampAfter(assetId, startDate, pageable);
        else if (endDate != null)
            return priceRepository.findByAssetIdAndTimestampBefore(assetId, endDate, pageable);
        else
            return priceRepository.findByAssetIdOrderByTimestampDesc(assetId, pageable);
    }

    @Override
    @Cacheable(value = "latestPrice", key = "#assetId")
    public Price getLatestPrice(UUID assetId) {
        return priceRepository.findTopByAssetIdOrderByTimestampDesc(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("No price data found for asset: " + assetId));
    }

    @Override
    @Transactional
    public Price addPrice(UUID assetId, BigDecimal priceValue) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with ID: " + assetId));

        Price price = new Price();
        price.setAsset(asset);
        price.setPrice(priceValue);
        price.setTimestamp(OffsetDateTime.now());
        return priceRepository.save(price);
    }

    @Override
    public BigDecimal calculatePriceChange(UUID assetId, int hours) {
        OffsetDateTime cutoffTime = OffsetDateTime.now().minusHours(hours);
        Price current = priceRepository.findTopByAssetIdOrderByTimestampDesc(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("No price data found"));
        Price past = priceRepository.findTopByAssetIdAndTimestampBeforeOrderByTimestampDesc(assetId, cutoffTime)
                .orElse(current);
        if (past.getPrice().compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return current.getPrice()
                .subtract(past.getPrice())
                .divide(past.getPrice(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    @Override
    @Transactional
    public PriceDto fetchAndSavePrice(UUID assetId) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found: " + assetId));

        BigDecimal priceValue;
        String source;

        try {
            priceValue = fetchPriceFromFinnhub(asset.getSymbol());
            source = "finnhub-api";
        } catch (Exception e) {
            Price lastPrice = priceRepository.findTopByAssetOrderByTimestampDesc(asset)
                    .orElseThrow(() -> new ResourceNotFoundException("No price for asset: " + asset.getSymbol()));
            priceValue = lastPrice.getPrice();
            source = lastPrice.getSource();
        }

        Price price = new Price();
        price.setAsset(asset);
        price.setPrice(priceValue);
        price.setTimestamp(OffsetDateTime.now());
        price.setSource(source);

        Price saved = priceRepository.save(price);
        return mapToDto(saved);
    }

    @Override
    public PriceDto getLatestPriceDto(UUID assetId) {
        Price price = priceRepository.findTopByAssetIdOrderByTimestampDesc(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Price not found"));
        return mapToDto(price);
    }

    @Override
    public Page<Price> getPriceHistoryEntity(UUID assetId, OffsetDateTime startDate, OffsetDateTime endDate, Pageable pageable) {
        return getPriceHistory(assetId, startDate, endDate, pageable);
    }

    @Override
    @Transactional
    public Price addPriceEntity(UUID assetId, Price price) {
        price.setAsset(assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found: " + assetId)));
        price.setTimestamp(OffsetDateTime.now());
        return priceRepository.save(price);
    }

    @Override
    public List<Price> findPriceHistoryListEntity(UUID assetId, OffsetDateTime startDate, OffsetDateTime endDate) {
        if (!assetRepository.existsById(assetId)) {
            throw new ResourceNotFoundException("Asset not found: " + assetId);
        }

        if (startDate != null && endDate != null) {
            return priceRepository.findByAssetIdAndTimestampBetweenOrderByTimestampAsc(assetId, startDate, endDate);
        } else if (startDate != null) {
            return priceRepository.findByAssetIdAndTimestampAfterOrderByTimestampAsc(assetId, startDate);
        } else if (endDate != null) {
            return priceRepository.findByAssetIdAndTimestampBeforeOrderByTimestampAsc(assetId, endDate);
        } else {
            return priceRepository.findByAssetIdOrderByTimestampAsc(assetId);
        }
    }

    @Override
    public Page<PriceDto> getPriceHistoryPaged(UUID assetId, OffsetDateTime startDate, OffsetDateTime endDate, Pageable pageable) {
        Page<Price> page;

        if (startDate != null && endDate != null) {
            page = priceRepository.findByAssetIdAndTimestampBetween(assetId, startDate, endDate, pageable);
        } else if (startDate != null) {
            page = priceRepository.findByAssetIdAndTimestampAfter(assetId, startDate, pageable);
        } else if (endDate != null) {
            page = priceRepository.findByAssetIdAndTimestampBefore(assetId, endDate, pageable);
        } else {
            page = priceRepository.findByAssetId(assetId, pageable);
        }

        return page.map(this::mapToDto);
    }

    @Override
    public List<CandleDTO> getCandles(UUID assetId, String interval, int limit) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new NoSuchElementException("Asset not found"));

        var now = OffsetDateTime.now();
        var start = switch (interval) {
            case "1d", "day" -> now.minusDays(1);
            case "1w", "week" -> now.minusWeeks(1);
            case "1m", "month" -> now.minusMonths(1);
            default -> throw new IllegalArgumentException("Invalid interval: " + interval);
        };

        List<Price> prices = priceRepository.findByAssetAndRange(assetId, start, now);
        if (prices.isEmpty()) return Collections.emptyList();

        // Lấy limit số lượng mới nhất
        List<Price> limited = prices.stream()
                .sorted(Comparator.comparing(Price::getTimestamp).reversed())
                .limit(limit)
                .sorted(Comparator.comparing(Price::getTimestamp))
                .collect(Collectors.toList());

        return limited.stream()
                .map(p -> new CandleDTO(
                        p.getTimestamp(),
                        p.getPrice(), // open = close nếu không có dữ liệu nến chi tiết
                        p.getPrice(), // high
                        p.getPrice(), // low
                        p.getPrice(), // close
                        p.getVolume()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public StatisticsDTO getStatistics(UUID assetId, String range) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new NoSuchElementException("Asset not found"));

        var now = OffsetDateTime.now();
        var start = switch (range) {
            case "day" -> now.minusDays(1);
            case "week" -> now.minusWeeks(1);
            case "month" -> now.minusMonths(1);
            default -> throw new IllegalArgumentException("Invalid range: " + range);
        };

        List<Price> prices = priceRepository.findByAssetAndRange(assetId, start, now);
        if (prices.isEmpty()) return new StatisticsDTO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, start, now);

        var min = prices.stream().map(Price::getPrice).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        var max = prices.stream().map(Price::getPrice).max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        var avg = prices.stream()
                .map(Price::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(prices.size()), BigDecimal.ROUND_HALF_UP);

        return new StatisticsDTO(min, max, avg, start, now);
    }

    private PriceDto mapToDto(Price price) {
        PriceDto dto = new PriceDto();
        dto.setId(price.getId());
        dto.setAssetId(price.getAsset().getId());
        dto.setPrice(price.getPrice());
        dto.setTimestamp(price.getTimestamp());
        dto.setVolume(price.getVolume());
        dto.setChangePercent(price.getChangePercent());
        dto.setHigh24h(price.getHigh24h());
        dto.setLow24h(price.getLow24h());
        dto.setMarketCap(price.getMarketCap());
        dto.setSource(price.getSource());
        return dto;
    }

    private BigDecimal fetchPriceFromFinnhub(String symbol) {
        String url = "https://finnhub.io/api/v1/quote?symbol=" + symbol + "&token=" + finnhubApiKey;
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null || response.get("c") == null) {
            throw new RuntimeException("Finnhub API returned no price for " + symbol);
        }

        return new BigDecimal(response.get("c").toString());
    }

    @Override
    @Transactional
    public Map<String, Object> fetchAndSaveAllPricesFromFinnhub() {
        List<Asset> assets = assetRepository.findByIsActiveTrue();

        int updated = 0;
        List<String> failed = new ArrayList<>();

        for (Asset asset : assets) {
            BigDecimal price = finnhubClient.fetchPrice(asset.getSymbol());

            if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                failed.add(asset.getSymbol());
                continue;
            }

            Price record = Price.builder()
                    .asset(asset)
                    .price(price)
                    .timestamp(OffsetDateTime.now())
                    .source("Finnhub")
                    .build();

            priceRepository.save(record);
            updated++;

            // để tránh bị rate limit Finnhub (60 req/phút)
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        }

        return Map.of(
                "message", "Fetched and saved all asset prices from Finnhub successfully.",
                "totalAssets", assets.size(),
                "updated", updated,
                "failed", failed
        );
    }
}
