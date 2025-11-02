package com.recognition.controller;

import com.recognition.entity.Asset;
import com.recognition.entity.Price;
import com.recognition.exception.ResourceNotFoundException;
import com.recognition.service.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Assets", description = "Asset management and market operations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AssetController {

    private final AssetService assetService;

    /**
     * Retrieve all assets stored in the database.
     */
    @GetMapping
    @Operation(summary = "Get all assets", description = "Retrieve all assets from the local database")
    public ResponseEntity<List<Asset>> getAllAssets() {
        log.info("Fetching all assets from the database");
        List<Asset> assets = assetService.getAllAssets();
        return ResponseEntity.ok(assets);
    }

    /**
     * Check if an asset exists by symbol.
     */
    @GetMapping("/exists/{symbol}")
    @Operation(summary = "Check if asset exists", description = "Check whether an asset with the given symbol exists in the database")
    public ResponseEntity<Map<String, Object>> checkAssetExists(
            @Parameter(description = "Asset symbol (e.g. AAPL, BTC)") @PathVariable String symbol) {
        log.info("Checking existence of asset with symbol: {}", symbol);
        try {
            boolean exists = assetService.existsBySymbol(symbol);
            return ResponseEntity.ok(Map.of("symbol", symbol, "exists", exists));
        } catch (Exception e) {
            log.error("Error checking existence for symbol {}: {}", symbol, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unable to check existence", "symbol", symbol));
        }
    }

    /**
     * Delete an asset by its ID.
     */
    @DeleteMapping("/{assetId}")
    @Operation(summary = "Delete asset", description = "Delete an asset by its ID along with related price data")
    public ResponseEntity<?> deleteAsset(@PathVariable UUID assetId) {
        log.info("API request to delete asset: {}", assetId);
        try {
            assetService.deleteAsset(assetId);
            return ResponseEntity.ok(Map.of(
                    "message", "Asset deleted successfully",
                    "assetId", assetId.toString()
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage(), "assetId", assetId.toString()));
        } catch (Exception e) {
            log.error("Failed to delete asset {}: {}", assetId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete asset", "assetId", assetId.toString()));
        }
    }

    /**
     * Retrieve both asset and company information in one call.
     */
    @GetMapping("/{symbol}/overview")
    @Operation(summary = "Get asset overview", description = "Retrieve both asset details and company information for the given symbol")
    public ResponseEntity<?> getAssetOverview(
            @Parameter(description = "Asset symbol (e.g. AAPL, BTC)") @PathVariable String symbol) {
        log.info("Fetching asset overview for symbol: {}", symbol);
        try {
            Map<String, Object> overview = assetService.getAssetOverview(symbol);
            return ResponseEntity.ok(overview);
        } catch (Exception e) {
            log.error("Failed to fetch asset overview for {}: {}", symbol, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Unable to fetch asset overview", "symbol", symbol));
        }
    }

    /**
     * Fetch and store new market stocks that are not yet in the database.
     */
    @GetMapping("/market/stocks/new")
    @Operation(summary = "Fetch new market stocks", description = "Fetch and store up to 10 new stocks from Finnhub that are not yet in the database")
    public ResponseEntity<?> fetchNewMarketStocks() {
        log.info("Fetching NEW market stocks not existing in DB");
        try {
            List<Map<String, Object>> newStocks = assetService.fetchNewMarketStocks(10);
            return ResponseEntity.ok(newStocks);
        } catch (Exception e) {
            log.error("Failed to fetch new market stocks: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "Unable to fetch new market stocks"));
        }
    }

    /**
     * Fetch latest price for a given asset and save it to the database.
     */
    @PostMapping("/prices/{assetId}/fetch")
    @Operation(summary = "Fetch and save latest price", description = "Fetch latest price from external API and save it to the database")
    public ResponseEntity<?> fetchAndSaveLatestPrice(
            @Parameter(description = "Asset ID") @PathVariable UUID assetId) {
        log.info("Fetching and saving latest price for asset: {}", assetId);
        try {
            Price price = assetService.fetchAndSavePrice(assetId);
            return ResponseEntity.ok(price);
        } catch (Exception e) {
            log.error("Failed to fetch/save price for asset {}: {}", assetId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "Unable to fetch latest price", "assetId", assetId.toString()));
        }
    }
}
