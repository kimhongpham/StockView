package com.recognition.service;

import com.recognition.entity.Asset;
import com.recognition.entity.Price;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AssetService {

    Map<String, Object> getAssetDetails(String code);
    Map<String, Object> getCompanyInfo(String symbol);

    Price fetchAndSavePrice(UUID assetId);

    boolean existsBySymbol(String symbol);

    void deleteAsset(UUID assetId);

    List<Map<String, Object>> getMarketStocks();
    List<Map<String, Object>> fetchNewMarketStocks(int limit);
    List<Asset> getAllAssets();
}
