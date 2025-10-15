package com.recognition.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.recognition.entity.Asset;

@Repository
public interface AssetRepository extends JpaRepository<Asset, UUID>, JpaSpecificationExecutor<Asset> {

    Optional<Asset> findBySymbolIgnoreCase(String symbol);

    Optional<Asset> findBySymbol(String symbol);

    boolean existsBySymbol(String symbol);

    List<Asset> findByIsActiveTrue();

    @Query("SELECT a FROM Asset a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Asset> findByNameContainingIgnoreCase(@Param("name") String name);

    // Kiểm tra name có tồn tại chưa
    boolean existsByName(String name);

    // Tìm asset active theo symbol
    Optional<Asset> findBySymbolAndIsActiveTrue(String symbol);
}
