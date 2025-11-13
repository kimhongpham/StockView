package com.recognition.controller;

import com.recognition.dto.PriceDto;
import com.recognition.dto.CandleDTO;
import com.recognition.dto.response.StatisticsDTO;
import com.recognition.dto.response.PriceResponse;
import com.recognition.entity.Price;
import com.recognition.exception.InvalidSortPropertyException;
import com.recognition.service.AsyncPriceService;
import com.recognition.service.PriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@RestController
@RequestMapping("/api/prices")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Prices", description = "Price management, chart, and statistics")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PriceController {

    private final PriceService priceService;
    private final AsyncPriceService asyncPriceService;

    //1. Lấy giá mới nhất theo asset
    @GetMapping("/{assetId}/latest")
    public ResponseEntity<PriceDto> getLatestPrice(@PathVariable UUID assetId) {
        log.info("Fetching latest price for asset: {}", assetId);
        PriceDto latestPrice = priceService.getLatestPriceDto(assetId);
        return ResponseEntity.ok(latestPrice);
    }

    // 2. Lịch sử giá có phân trang
    @GetMapping("/{assetId}/history/paged")
    public ResponseEntity<Page<PriceResponse>> getPriceHistoryPaged(
            @PathVariable UUID assetId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @Parameter(
                    description = "Sorting field and direction (allowed: timestamp, price, volume, high24h, low24h, marketCap)",
                    example = "timestamp,desc",
                    schema = @Schema(allowableValues = {
                            "timestamp,asc", "timestamp,desc",
                            "price,asc", "price,desc",
                            "volume,asc", "volume,desc",
                            "high24h,asc", "high24h,desc",
                            "low24h,asc", "low24h,desc",
                            "marketCap,asc", "marketCap,desc"
                    })
            )
            @PageableDefault(sort = "timestamp", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        // Danh sách các field được phép sort
        List<String> validSorts = List.of("timestamp", "price", "volume", "high24h", "low24h", "marketCap");

        // Kiểm tra xem người dùng có nhập sai field không
        for (Sort.Order order : pageable.getSort()) {
            if (!validSorts.contains(order.getProperty())) {
                throw new InvalidSortPropertyException(order.getProperty());
            }
        }

        // Chuyển đổi LocalDate → OffsetDateTime (UTC)
        OffsetDateTime start = startDate != null
                ? startDate.atStartOfDay().atOffset(ZoneOffset.UTC)
                : null;
        OffsetDateTime end = endDate != null
                ? endDate.atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC)
                : null;

        // Lấy dữ liệu phân trang
        Page<PriceDto> page = priceService.getPriceHistoryPaged(assetId, start, end, pageable);

        // Map sang PriceResponse
        return ResponseEntity.ok(page.map(this::mapToResponse));
    }

    // 3. Dữ liệu biểu đồ
    @GetMapping("/{assetId}/chart")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getChart(
            @PathVariable UUID assetId,
            @RequestParam(defaultValue = "1d") String interval,
            @RequestParam(defaultValue = "100") int limit
    ) {
        List<CandleDTO> data = priceService.getCandles(assetId, interval, limit);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Chart data fetched successfully",
                "data", data
        ));
    }

    // 4. Tính phần trăm thay đổi giá
    @GetMapping("/{assetId}/change")
    @Operation(summary = "Calculate price change", description = "Calculate the percentage change of price in a time range (hours)")
    public ResponseEntity<BigDecimal> getPriceChange(
            @Parameter(description = "Asset ID") @PathVariable UUID assetId,
            @Parameter(description = "Time period in hours") @RequestParam(defaultValue = "24") int hours) {

        log.info("Calculating price change for asset: {} over {} hours", assetId, hours);
        BigDecimal change = priceService.calculatePriceChange(assetId, hours);
        return ResponseEntity.ok(change);
    }

    // 5. Thống kê tổng hợp
    @GetMapping("/{assetId}/stats")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getStats(
            @PathVariable UUID assetId,
            @RequestParam(defaultValue = "day") String range
    ) {
        StatisticsDTO stats = priceService.getStatistics(assetId, range);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Statistics calculated successfully",
                "data", stats
        ));
    }

    // 6. Theo giá mới thủ công
    @PostMapping("/{assetId}")
    @Operation(summary = "Add new price", description = "Add a new price record for an asset")
    public ResponseEntity<Price> addPrice(
            @Parameter(description = "Asset ID") @PathVariable UUID assetId,
            @Valid @RequestBody Price price) {

        log.info("Adding new price for asset: {} with value: {}", assetId, price.getPrice());
        Price newPrice = priceService.addPriceEntity(assetId, price);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPrice);
    }

    // 7. Lấy giá mới từ Finnhub
    @PostMapping("/{assetId}/fetch")
    public ResponseEntity<PriceDto> fetchAndSavePrice(@PathVariable UUID assetId) {
        log.info("Fetching and saving latest price for asset: {}", assetId);
        PriceDto fetchedPrice = priceService.fetchAndSavePrice(assetId);
        return ResponseEntity.status(HttpStatus.CREATED).body(fetchedPrice);
    }

    // 8. Khởi tạo job async
    @PostMapping("/fetch-all/start")
    public ResponseEntity<?> startFetchAll() {
        String jobId = asyncPriceService.startJob();
        return ResponseEntity.ok(Map.of(
                "message", "Price update job started",
                "jobId", jobId
        ));
    }

    // 9. Kiểm tra tiến độ job
    @GetMapping("/fetch-all/status/{jobId}")
    public ResponseEntity<?> getStatus(@PathVariable String jobId) {
        return ResponseEntity.ok(asyncPriceService.getJobStatus(jobId));
    }

    // 10. Top tăng hoặc giảm giá
    @GetMapping("/top")
    @Operation(summary = "Get top gainers or losers",
            description = "Return the top assets with highest or lowest price change percentage")
    public ResponseEntity<?> getTopMovers(
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "gainers") String type
    ) {
        List<PriceDto> result = priceService.getTopMovers(type, limit);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "type", type,
                "count", result.size(),
                "data", result
        ));
    }

    // 11. Chuyển PriceDto → PriceResponse
    private PriceResponse mapToResponse(PriceDto dto) {
        PriceResponse response = new PriceResponse();
        response.setAssetId(dto.getAssetId());
        response.setPrice(dto.getPrice());
        response.setTimestamp(dto.getTimestamp());
        response.setVolume(dto.getVolume() != null ? dto.getVolume().longValue() : null);
        response.setChangePercent(dto.getChangePercent());
        response.setHigh24h(dto.getHigh24h());
        response.setLow24h(dto.getLow24h());
        response.setMarketCap(dto.getMarketCap());
        response.setSource(dto.getSource());
        return response;
    }
}
