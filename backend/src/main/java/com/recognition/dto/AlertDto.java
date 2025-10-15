package com.recognition.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class AlertDto {
  private UUID id;
  private UUID userId;
  private UUID assetId;
  private String alertType;
  private BigDecimal thresholdValue;
  private String conditionType;
  private Boolean isActive;
  private String notificationMethod;
  private String notificationTarget;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;
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
