package com.recognition.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recognition.entity.Alert;

@Repository
public interface AlertRepository extends JpaRepository<Alert, UUID> {
  List<Alert> findByUserId(UUID userId);

  List<Alert> findByAssetId(UUID assetId);

  List<Alert> findByIsActive(Boolean isActive);

  List<Alert> findByAssetIdAndIsActive(UUID assetId, Boolean isActive);
}
