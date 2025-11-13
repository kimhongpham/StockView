package com.recognition.service;

import com.recognition.entity.Asset;
import com.recognition.entity.Price;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AssetService {

    // Lấy toàn bộ tài sản trong DB
    List<Asset> getAllAssets();

    Map<String, Object> getAssetOverview(String code);

    boolean existsBySymbol(String symbol);

    List<Asset> searchAssets(String query);

    // Xóa asset + các giá liên quan
    void deleteAsset(UUID assetId);

    // Lấy danh sách cổ phiếu mới (chưa có trong DB)
    List<Map<String, Object>> fetchNewMarketStocks(int limit);

    // Lấy và lưu giá mới nhất cho asset
    Price fetchAndSavePrice(UUID assetId);
}
