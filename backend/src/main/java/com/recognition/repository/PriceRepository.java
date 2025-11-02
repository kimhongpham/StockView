package com.recognition.repository;

import com.recognition.entity.Asset;
import com.recognition.entity.Price;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PriceRepository extends JpaRepository<Price, UUID> {

    void deleteAllByAssetId(UUID assetId);

    Optional<Price> findTopByAssetOrderByTimestampDesc(Asset asset);
    Optional<Price> findTopByAssetIdOrderByTimestampDesc(UUID assetId);
    Optional<Price> findTopByAssetIdAndTimestampBeforeOrderByTimestampDesc(UUID assetId, OffsetDateTime timestamp);
    Optional<Price> findByAssetAndTimestampAndSource(Asset asset, OffsetDateTime timestamp, String source);

    Page<Price> findByAssetIdOrderByTimestampDesc(UUID assetId, Pageable pageable);
    Page<Price> findByAssetId(UUID assetId, Pageable pageable);
    Page<Price> findByAssetIdAndTimestampBetween(UUID assetId, OffsetDateTime start, OffsetDateTime end, Pageable pageable);
    Page<Price> findByAssetIdAndTimestampAfter(UUID assetId, OffsetDateTime start, Pageable pageable);
    Page<Price> findByAssetIdAndTimestampBefore(UUID assetId, OffsetDateTime end, Pageable pageable);

    List<Price> findByAssetIdAndTimestampBetweenOrderByTimestampAsc(UUID assetId, OffsetDateTime start, OffsetDateTime end);
    List<Price> findByAssetIdAndTimestampAfterOrderByTimestampAsc(UUID assetId, OffsetDateTime start);
    List<Price> findByAssetIdAndTimestampBeforeOrderByTimestampAsc(UUID assetId, OffsetDateTime end);

    List<Price> findByAssetIdOrderByTimestampAsc(UUID assetId);

    @Query("SELECT p FROM Price p WHERE p.asset.id = :assetId AND p.timestamp BETWEEN :start AND :end ORDER BY p.timestamp ASC")
    List<Price> findByAssetAndTimestampBetweenOrderByTimestampAsc(UUID assetId, OffsetDateTime start, OffsetDateTime end);

    @Query("""
            SELECT p FROM Price p
            WHERE p.asset.id = :assetId
              AND p.timestamp BETWEEN :start AND :end
            ORDER BY p.timestamp ASC
           """)
    List<Price> findByAssetAndRange(@Param("assetId") UUID assetId,
                                    @Param("start") OffsetDateTime start,
                                    @Param("end") OffsetDateTime end);

    @Query("""
    SELECT p
    FROM Price p
    JOIN FETCH p.asset a
    WHERE p.timestamp = (
        SELECT MAX(p2.timestamp)
        FROM Price p2
        WHERE p2.asset.id = p.asset.id
    )
    AND p.changePercent IS NOT NULL
    ORDER BY p.changePercent DESC
    """)
    List<Price> findTopGainers(Pageable pageable);

    @Query("""
    SELECT p
    FROM Price p
    WHERE p.timestamp = (
        SELECT MAX(p2.timestamp)
        FROM Price p2
        WHERE p2.asset.id = p.asset.id
    )
    AND p.changePercent IS NOT NULL
    ORDER BY p.changePercent ASC
    """)
    List<Price> findTopLosers(Pageable pageable);
}
