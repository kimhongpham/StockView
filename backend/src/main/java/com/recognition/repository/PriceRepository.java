package com.recognition.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.recognition.entity.Asset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.recognition.entity.Price;

// ========================== PriceRepository.java ==========================
@Repository
public interface PriceRepository extends JpaRepository<Price, UUID> {

    // ✅ UC2
    Page<Price> findByAssetIdOrderByTimestampDesc(UUID assetId, Pageable pageable);
    Page<Price> findByAssetIdOrderByTimestampAsc(UUID assetId, Pageable pageable);
    Page<Price> findByAssetId(UUID assetId, Pageable pageable);
    Page<Price> findByAssetIdAndTimestampBetween(UUID assetId, OffsetDateTime start, OffsetDateTime end, Pageable pageable);
    Page<Price> findByAssetIdAndTimestampAfter(UUID assetId, OffsetDateTime start, Pageable pageable);
    Page<Price> findByAssetIdAndTimestampBefore(UUID assetId, OffsetDateTime end, Pageable pageable);
    List<Price> findByAssetIdAndTimestampBetweenOrderByTimestampAsc(UUID assetId, OffsetDateTime start, OffsetDateTime end);
    List<Price> findByAssetIdAndTimestampAfterOrderByTimestampAsc(UUID assetId, OffsetDateTime start);
    List<Price> findByAssetIdAndTimestampBeforeOrderByTimestampAsc(UUID assetId, OffsetDateTime end);
    List<Price> findByAssetIdOrderByTimestampAsc(UUID assetId);

    // ✅ UC1
    Optional<Price> findTopByAssetIdAndTimestampBeforeOrderByTimestampDesc(UUID assetId, OffsetDateTime timestamp);
    Optional<Price> findTopByAssetOrderByTimestampDesc(Asset asset);
    Optional<Price> findTopByAssetIdOrderByTimestampDesc(UUID assetId);
    Optional<Price> findTopByAssetIdAndSourceOrderByTimestampDesc(UUID assetId, String source);


    // ✅ UC2
    @Query("""
            SELECT p FROM Price p
            WHERE p.asset.id = :assetId
            AND p.timestamp >= :startDate
            AND p.timestamp <= :endDate
            ORDER BY p.timestamp
            """)
    List<Price> findPricesInRange(@Param("assetId") UUID assetId,
                                  @Param("startDate") OffsetDateTime startDate,
                                  @Param("endDate") OffsetDateTime endDate);

    // ❌ UC11 – tạm ẩn
//    @Query(value = """
//            SELECT
//                date_trunc(:interval, timestamp) as timestamp,
//                AVG(price) as price,
//                SUM(volume) as volume,
//                AVG(change_percent) as change_percent,
//                MIN(price) as low_price,
//                MAX(price) as high_price
//            FROM prices
//            WHERE asset_id = :assetId
//            AND timestamp BETWEEN :startDate AND :endDate
//            GROUP BY date_trunc(:interval, timestamp)
//            ORDER BY timestamp
//            """, nativeQuery = true)
//    List<Price> findAggregatedPrices(@Param("assetId") UUID assetId,
//            @Param("startDate") OffsetDateTime startDate,
//            @Param("endDate") OffsetDateTime endDate,
//            @Param("interval") String interval);

    // 🔶 Chưa dùng – để sau
    @Query("SELECT COUNT(p) FROM Price p WHERE p.asset.id = :assetId")
    long countByAssetId(@Param("assetId") UUID assetId);

    // ✅ UC15
    void deleteByAssetIdAndTimestampBefore(UUID assetId, OffsetDateTime cutoffDate);

    // ✅ UC2
    List<Price> findByAssetIdAndSourceAndTimestampBetween(UUID assetId, String source,
                                                          OffsetDateTime start, OffsetDateTime end);

    // 🔶 Chưa dùng – để sau
    List<Price> findTop10ByAssetIdOrderByTimestampDesc(UUID assetId);
    List<Price> findTop5ByAssetIdAndSourceOrderByTimestampDesc(UUID assetId, String source);

    // ✅ UC2
    Optional<Price> findTopByAssetIdOrderByTimestampAsc(UUID assetId);

    // ✅ UC2
    @Query("""
            SELECT p FROM Price p
            WHERE p.asset.id = :assetId
              AND p.timestamp BETWEEN :startDate AND :endDate
            ORDER BY p.timestamp DESC
            """)
    List<Price> findPricesInRangeDesc(@Param("assetId") UUID assetId,
                                      @Param("startDate") OffsetDateTime startDate,
                                      @Param("endDate") OffsetDateTime endDate,
                                      Pageable pageable);

    // ✅ UC15
    void deleteByTimestampBefore(OffsetDateTime cutoffDate);
}
