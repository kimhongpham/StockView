package com.recognition.controller;

import com.recognition.dto.PriceDto;
import com.recognition.entity.Price;
import com.recognition.service.PriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/prices")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Prices", description = "Price management and market data")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PriceController {

    private final PriceService priceService;

    // ✅ UC1: Xem giá thị trường (lấy giá mới nhất)
    @GetMapping("/{assetId}/latest")
    public ResponseEntity<PriceDto> getLatestPrice(@PathVariable UUID assetId) {
        log.info("Fetching latest price for asset: {}", assetId);
        PriceDto latestPrice = priceService.getLatestPriceDto(assetId);
        return ResponseEntity.ok(latestPrice);
    }

    // ✅ UC1: Tính phần trăm thay đổi giá
    @GetMapping("/{assetId}/change")
    @Operation(summary = "Calculate price change", description = "Calculate the percentage change of price in a time range (hours)")
    public ResponseEntity<BigDecimal> getPriceChange(
            @Parameter(description = "Asset ID") @PathVariable UUID assetId,
            @Parameter(description = "Time period in hours") @RequestParam(defaultValue = "24") int hours) {

        log.info("Calculating price change for asset: {} over {} hours", assetId, hours);
        BigDecimal change = priceService.calculatePriceChange(assetId, hours);
        return ResponseEntity.ok(change);
    }

    // ✅ Lịch sử giá (phân trang) - nhận LocalDate dễ dùng
    @GetMapping("/{assetId}/history/paged")
    public ResponseEntity<Page<PriceDto>> getPriceHistoryPaged(
            @PathVariable UUID assetId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {

        OffsetDateTime start = startDate != null ? startDate.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime() : null;
        OffsetDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC) : null;

        Page<PriceDto> page = priceService.getPriceHistoryPaged(assetId, start, end, pageable);
        return ResponseEntity.ok(page);
    }

    // ✅ Lịch sử giá (danh sách, vẫn dùng Page để giới hạn số bản ghi)
    @GetMapping("/{assetId}/history/list")
    public ResponseEntity<Page<PriceDto>> getPriceHistoryList(
            @PathVariable UUID assetId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {

        OffsetDateTime start = startDate != null ? startDate.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime() : null;
        OffsetDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC) : null;

        Page<PriceDto> prices = priceService.getPriceHistoryPaged(assetId, start, end, pageable);
        return ResponseEntity.ok(prices);
    }

    // ✅ UC15: Thêm giá mới
    @PostMapping("/{assetId}")
    @Operation(summary = "Add new price", description = "Add a new price record for an asset")
    public ResponseEntity<Price> addPrice(
            @Parameter(description = "Asset ID") @PathVariable UUID assetId,
            @Valid @RequestBody Price price) {

        log.info("Adding new price for asset: {} with value: {}", assetId, price.getPrice());
        Price newPrice = priceService.addPriceEntity(assetId, price);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPrice);
    }

    // ✅ UC15: Lấy giá mới nhất từ API ngoài
    @PostMapping("/{assetId}/fetch")
    public ResponseEntity<PriceDto> fetchAndSavePrice(@PathVariable UUID assetId) {
        log.info("Fetching and saving latest price for asset: {}", assetId);
        PriceDto fetchedPrice = priceService.fetchAndSavePrice(assetId);
        return ResponseEntity.status(HttpStatus.CREATED).body(fetchedPrice);
    }

    // ✅ CRUD phụ: Xóa giá
//    @DeleteMapping("/{id}")
//    @Operation(summary = "Delete price", description = "Delete a price record by ID")
//    public ResponseEntity<Void> deletePrice(@PathVariable UUID id) {
//        log.info("Deleting price record with ID: {}", id);
//        priceService.deletePrice(id);
//        return ResponseEntity.noContent().build();
//    }
}
