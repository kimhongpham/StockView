package com.recognition.service;

import com.recognition.dto.CandleDTO;
import com.recognition.dto.PriceDto;
import com.recognition.dto.response.StatisticsDTO;
import com.recognition.entity.Price;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PriceService {

    // Lấy lịch sử giá dạng Page<Price>
    Page<Price> getPriceHistory(UUID assetId, OffsetDateTime startDate, OffsetDateTime endDate, Pageable pageable);

    // Lấy giá mới nhất dạng Price entity
    Price getLatestPrice(UUID assetId);

    // Thêm giá mới từ BigDecimal
    Price addPrice(UUID assetId, BigDecimal priceValue);

    // Tính % thay đổi giá trong khoảng thời gian hours
    BigDecimal calculatePriceChange(UUID assetId, int hours);

    // Thu thập giá từ API ngoài và lưu vào DB
    PriceDto fetchAndSavePrice(UUID assetId);

    // Lấy giá mới nhất dạng DTO
    PriceDto getLatestPriceDto(UUID assetId);

    // -------------------------
    // Dạng entity chuyên dụng (cho internal)
    Page<Price> getPriceHistoryEntity(UUID assetId, OffsetDateTime startDate, OffsetDateTime endDate, Pageable pageable);

    Price addPriceEntity(UUID assetId, Price price);

    List<Price> findPriceHistoryListEntity(UUID assetId, OffsetDateTime startDate, OffsetDateTime endDate);

    Page<PriceDto> getPriceHistoryPaged(UUID assetId, OffsetDateTime startDate, OffsetDateTime endDate, Pageable pageable);

    // -------------------------
    /**
     * Lấy dữ liệu nến (candle) theo asset, interval và limit.
     */
    List<CandleDTO> getCandles(UUID assetId, String interval, int limit);

    /**
     * Tính toán thống kê giá theo range (day/week/month).
     */
    StatisticsDTO getStatistics(UUID assetId, String range);

    Map<String, Object> fetchAndSaveAllPricesFromFinnhub();
}
