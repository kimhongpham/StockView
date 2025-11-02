package com.recognition.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.recognition.entity.Asset;

@Repository
public interface AssetRepository extends JpaRepository<Asset, UUID>, JpaSpecificationExecutor<Asset> {

    Optional<Asset> findBySymbolIgnoreCase(String symbol);

    Optional<Asset> findBySymbol(String symbol);

    boolean existsBySymbol(String symbol);

    boolean existsById(UUID assetId);

    void deleteById(UUID assetId);

    List<Asset> findByIsActiveTrue();
}
