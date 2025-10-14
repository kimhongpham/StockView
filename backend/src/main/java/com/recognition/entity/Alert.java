package com.recognition.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "alerts", indexes = {
    @Index(name = "idx_alerts_user_id", columnList = "user_id"),
    @Index(name = "idx_alerts_asset_id", columnList = "asset_id"),
    @Index(name = "idx_alerts_is_active", columnList = "is_active"),
    @Index(name = "idx_alerts_active_asset", columnList = "asset_id, is_active", unique = false)
})
public class Alert {

  @Id
  @GeneratedValue
  @Column(columnDefinition = "UUID DEFAULT gen_random_uuid()")
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "asset_id", nullable = false)
  private UUID assetId;

  @Column(name = "alert_type", nullable = false, length = 50)
  private String alertType;

  @Column(name = "threshold_value", nullable = false, precision = 18, scale = 8)
  private BigDecimal thresholdValue;

  @Column(name = "condition_type", nullable = false, length = 20)
  private String conditionType;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true;

  @Column(name = "notification_method", nullable = false, length = 50)
  private String notificationMethod;

  @Column(name = "notification_target", nullable = false, length = 255)
  private String notificationTarget;

  @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT NOW()")
  private OffsetDateTime createdAt;

  @Column(name = "updated_at", columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT NOW()")
  private OffsetDateTime updatedAt;

  @Column(name = "last_triggered")
  private OffsetDateTime lastTriggered;

  // Getters and setters

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public UUID getAssetId() {
    return assetId;
  }

  public void setAssetId(UUID assetId) {
    this.assetId = assetId;
  }

  public String getAlertType() {
    return alertType;
  }

  public void setAlertType(String alertType) {
    this.alertType = alertType;
  }

  public BigDecimal getThresholdValue() {
    return thresholdValue;
  }

  public void setThresholdValue(BigDecimal thresholdValue) {
    this.thresholdValue = thresholdValue;
  }

  public String getConditionType() {
    return conditionType;
  }

  public void setConditionType(String conditionType) {
    this.conditionType = conditionType;
  }

  public Boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  public String getNotificationMethod() {
    return notificationMethod;
  }

  public void setNotificationMethod(String notificationMethod) {
    this.notificationMethod = notificationMethod;
  }

  public String getNotificationTarget() {
    return notificationTarget;
  }

  public void setNotificationTarget(String notificationTarget) {
    this.notificationTarget = notificationTarget;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public OffsetDateTime getLastTriggered() {
    return lastTriggered;
  }

  public void setLastTriggered(OffsetDateTime lastTriggered) {
    this.lastTriggered = lastTriggered;
  }
}
