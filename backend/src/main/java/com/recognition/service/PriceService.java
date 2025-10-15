package com.recognition.service;

import com.recognition.dto.PriceDto;
import com.recognition.entity.Asset;
import com.recognition.entity.Price;
import com.recognition.exception.ResourceNotFoundException;
import com.recognition.repository.AssetRepository;
import com.recognition.repository.PriceRepository;
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
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PriceService {

    private final PriceRepository priceRepository;
    private final AssetRepository assetRepository;

    @Value("${finnhub.api.key}")
    private String finnhubApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // ✅ Xem lịch sử giá
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

    // ✅ Xem giá mới nhất
    @Cacheable(value = "latestPrice", key = "#assetId")
    public Price getLatestPrice(UUID assetId) {
        return priceRepository.findTopByAssetIdOrderByTimestampDesc(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("No price data found for asset: " + assetId));
    }

    // ✅ Thêm giá mới
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

    // ✅ Tính % thay đổi giá
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

    // ✅ Thu thập giá tự động từ API ngoài
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

    public PriceDto getLatestPriceDto(UUID assetId) {
        Price price = priceRepository.findTopByAssetIdOrderByTimestampDesc(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Price not found"));
        return mapToDto(price);
    }

    private PriceDto mapToDto(Price price) {
        PriceDto dto = new PriceDto();
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

    private BigDecimal fetchStockPrice(String symbol) {
        String url = "https://finnhub.io/api/v1/quote?symbol=" + symbol + "&token=" + finnhubApiKey;
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        return BigDecimal.valueOf(Double.parseDouble(response.get("c").toString()));
    }

    public Price getLatestPriceEntity(UUID assetId) {
        return priceRepository.findTopByAssetIdOrderByTimestampDesc(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("No price data found for asset: " + assetId));
    }

    public Page<Price> getPriceHistoryEntity(UUID assetId, OffsetDateTime startDate, OffsetDateTime endDate, Pageable pageable) {
        return priceRepository.findByAssetIdOrderByTimestampDesc(assetId, pageable);
    }

    // ✅ Lấy danh sách giá theo assetId và khoảng thời gian (list)
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

    // ✅ Lấy trang giá theo assetId và khoảng thời gian (paged)
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

    @Transactional
    public Price addPriceEntity(UUID assetId, Price price) {
        price.setAsset(assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found: " + assetId)));
        price.setTimestamp(OffsetDateTime.now());
        return priceRepository.save(price);
    }
}
