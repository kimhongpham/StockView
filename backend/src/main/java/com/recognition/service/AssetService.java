package com.recognition.service;

import com.recognition.entity.Asset;
import com.recognition.entity.Price;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AssetService {

    // 🔹 Lấy toàn bộ tài sản trong DB
    List<Asset> getAllAssets();

    // 🔹 Gộp getAssetDetails + getCompanyInfo
    Map<String, Object> getAssetOverview(String code);

    // 🔹 Lấy danh sách cổ phiếu mới (chưa có trong DB)
    List<Map<String, Object>> fetchNewMarketStocks(int limit);

    // 🔹 Lấy và lưu giá mới nhất cho asset
    Price fetchAndSavePrice(UUID assetId);

    // 🔹 Kiểm tra symbol đã tồn tại chưa
    boolean existsBySymbol(String symbol);

    // 🔹 Xóa asset + các giá liên quan
    void deleteAsset(UUID assetId);
}
