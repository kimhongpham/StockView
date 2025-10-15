package com.recognition.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recognition.entity.AlertLog;

@Repository
public interface AlertLogRepository extends JpaRepository<AlertLog, UUID> {
  List<AlertLog> findByAlertId(UUID alertId);

  List<AlertLog> findByNotificationStatus(String notificationStatus);
}
