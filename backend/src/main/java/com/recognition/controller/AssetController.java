package com.recognition.controller;

import com.recognition.entity.Price;
import com.recognition.service.AssetService;
import com.recognition.service.PriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Assets", description = "Asset management operations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AssetController {

    private final AssetService assetService;
    private final PriceService priceService;

    // ------------------------------
    // MARKET DATA
    // ------------------------------

    @GetMapping("/market/stocks")
    @Operation(summary = "Get market stocks", description = "Retrieve a list of top stocks from external API (Finnhub)")
    public ResponseEntity<List<Map<String, Object>>> getMarketStocks() {
        log.info("Fetching market stocks from external API (Finnhub)");
        List<Map<String, Object>> stocks = assetService.getMarketStocks();
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/{code}/details")
    @Operation(summary = "Get asset details", description = "Retrieve detailed information of an asset (stock, crypto, metal) by its symbol")
    public ResponseEntity<Map<String, Object>> getAssetDetails(
            @Parameter(description = "Asset code (symbol)") @PathVariable String code) {

        log.info("Fetching asset details for symbol: {}", code);
        Map<String, Object> details = assetService.getAssetDetails(code);
        return ResponseEntity.ok(details);
    }

    // ------------------------------
    // PRICE FETCH & SAVE
    // ------------------------------

    @PostMapping("/prices/{assetId}/fetch")
    @Operation(summary = "Fetch and save latest price", description = "Fetch latest price from external API (Finnhub / Crypto) and save to DB")
    public ResponseEntity<Price> fetchAndSavePrice(
            @Parameter(description = "Asset ID") @PathVariable UUID assetId) {

        log.info("Fetching and saving latest price for asset: {}", assetId);
        Price price = assetService.fetchAndSavePrice(assetId);
        return ResponseEntity.ok(price);
    }
}
