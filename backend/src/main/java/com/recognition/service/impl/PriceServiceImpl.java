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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
            log.warn("Finnhub fetch failed for {} — using last known price", asset.getSymbol());
            Price lastPrice = priceRepository.findTopByAssetOrderByTimestampDesc(asset)
                    .orElseThrow(() -> new ResourceNotFoundException("No price for asset: " + asset.getSymbol()));
            priceValue = lastPrice.getPrice();
            source = lastPrice.getSource();
        }

        // 🔹 Lấy khối lượng giao dịch (volume)
        BigDecimal volume = null;
        try {
            volume = finnhubClient.fetchQuoteVolume(asset.getSymbol());
        } catch (Exception e) {
            log.warn("Unable to fetch volume for {}: {}", asset.getSymbol(), e.getMessage());
        }

        // ✅ Convert BigDecimal → Long để phù hợp với entity
        Long volumeValue = (volume != null) ? volume.longValue() : null;

        // 🔹 Lấy giá trước đó
        Price previousPrice = priceRepository.findTopByAssetOrderByTimestampDesc(asset).orElse(null);

        BigDecimal changePercent = null;
        if (previousPrice != null && previousPrice.getPrice() != null
                && previousPrice.getPrice().compareTo(BigDecimal.ZERO) != 0
                && priceValue != null) {
            BigDecimal diff = priceValue.subtract(previousPrice.getPrice());
            changePercent = diff
                    .divide(previousPrice.getPrice(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            log.info("Change for {}: {} -> {} = {}%", asset.getSymbol(), previousPrice.getPrice(), priceValue, changePercent);
        }

        // 🔹 Bỏ qua nếu giá trùng nhau (tránh spam record)
        if (previousPrice != null && previousPrice.getPrice().compareTo(priceValue) == 0) {
            log.info("⏸ No price change for {}, skipping insert.", asset.getSymbol());
            return mapToDto(previousPrice);
        }

        // 🔹 Lưu bản ghi giá mới
        Price price = Price.builder()
                .asset(asset)
                .price(priceValue)
                .timestamp(OffsetDateTime.now())
                .source(source)
                .changePercent(changePercent)
                .volume(volumeValue) // ✅ kiểu Long khớp với entity
                .build();

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

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime start;

        // Nếu interval = null hoặc không hợp lệ → lấy toàn bộ
        if (interval == null || interval.isBlank()) interval = "all";

        switch (interval.toLowerCase()) {
            case "1d", "day" -> start = now.minusDays(1);
            case "1w", "week" -> start = now.minusWeeks(1);
            case "1m", "month" -> start = now.minusMonths(1);
            case "all" -> start = OffsetDateTime.MIN;
            default -> throw new IllegalArgumentException("Invalid interval: " + interval);
        }

        // 🔹 Lấy danh sách giá theo khoảng thời gian (hoặc toàn bộ)
        List<Price> prices = "all".equalsIgnoreCase(interval)
                ? priceRepository.findByAssetIdOrderByTimestampAsc(assetId)
                : priceRepository.findByAssetAndTimestampBetweenOrderByTimestampAsc(assetId, start, now);

        if (prices.isEmpty()) return Collections.emptyList();

        // 🔹 Giới hạn số lượng bản ghi cuối cùng (nếu có)
        List<Price> limited = prices.size() > limit
                ? prices.subList(prices.size() - limit, prices.size())
                : prices;

        // 🔹 Chuyển sang CandleDTO
        return limited.stream()
                .map(p -> new CandleDTO(
                        p.getTimestamp(),
                        p.getPrice(), // open
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
        dto.setAssetName(price.getAsset().getName());
        dto.setAssetSymbol(price.getAsset().getSymbol());
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
        BigDecimal price = finnhubClient.fetchPrice(symbol);
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("FinnhubClient returned invalid or null price for " + symbol);
        }
        return price;
    }

    @Override
    @Transactional
    public Map<String, Object> fetchAndSaveAllPricesFromFinnhub() {
        List<Asset> assets = assetRepository.findByIsActiveTrue();

        int updated = 0;
        List<String> failed = new ArrayList<>();

        for (Asset asset : assets) {
            try {
                BigDecimal price = finnhubClient.fetchPrice(asset.getSymbol());

                if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                    failed.add(asset.getSymbol());
                    continue;
                }

                OffsetDateTime timestamp = OffsetDateTime.now();

                // Bỏ qua nếu timestamp = 0 hoặc null (do Finnhub trả sai)
                if (timestamp == null || timestamp.toEpochSecond() == 0) {
                    log.warn("Skipping invalid timestamp for asset {}", asset.getSymbol());
                    continue;
                }

                // Tính phần trăm thay đổi so với giá trước đó
                java.util.concurrent.atomic.AtomicReference<BigDecimal> changePercentRef = new java.util.concurrent.atomic.AtomicReference<>(null);
                priceRepository.findTopByAssetOrderByTimestampDesc(asset).ifPresent(prev -> {
                    BigDecimal prevPrice = prev.getPrice();
                    if (prevPrice != null && prevPrice.compareTo(BigDecimal.ZERO) != 0) {
                        BigDecimal diff = price.subtract(prevPrice);
                        BigDecimal percent = diff.divide(prevPrice, 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100));
                        changePercentRef.set(percent);
                    }
                });

                // Kiểm tra xem bản ghi này đã tồn tại chưa
                Optional<Price> existing = priceRepository.findByAssetAndTimestampAndSource(asset, timestamp, "Finnhub");

                if (existing.isPresent()) {
                    // Update bản ghi cũ
                    Price existingPrice = existing.get();
                    existingPrice.setPrice(price);
                    existingPrice.setChangePercent(changePercentRef.get());
                    priceRepository.save(existingPrice);
                    log.info("Updated existing price for asset {} at {}", asset.getSymbol(), timestamp);
                } else {
                    // Tạo bản ghi mới
                    Price record = Price.builder()
                            .asset(asset)
                            .price(price)
                            .timestamp(timestamp)
                            .source("Finnhub")
                            .changePercent(changePercentRef.get())
                            .build();

                    priceRepository.save(record);
                    log.info("Inserted new price for asset {} at {}", asset.getSymbol(), timestamp);
                }

                updated++;

                // tránh bị rate-limit API (60 requests/phút)
                Thread.sleep(1000);

            } catch (Exception ex) {
                log.warn("Failed to fetch or save price for {}: {}", asset.getSymbol(), ex.getMessage());
                failed.add(asset.getSymbol());
            }
        }

        return Map.of(
                "message", "Fetched and saved all asset prices from Finnhub successfully.",
                "totalAssets", assets.size(),
                "updated", updated,
                "failed", failed
        );
    }

    @Override
    public List<PriceDto> getTopMovers(String type, int limit) {
        Pageable pageable = Pageable.ofSize(limit);
        List<Price> prices;

        if ("gainers".equalsIgnoreCase(type)) {
            prices = priceRepository.findTopGainers(pageable);
        } else if ("losers".equalsIgnoreCase(type)) {
            prices = priceRepository.findTopLosers(pageable);
        } else {
            throw new IllegalArgumentException("Invalid type: " + type + ". Use 'gainers' or 'losers'.");
        }

        return prices.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
}
