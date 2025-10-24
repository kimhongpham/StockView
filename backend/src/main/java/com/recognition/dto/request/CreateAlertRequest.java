package com.recognition.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateAlertRequest {
  private UUID assetId;
  private String alertType;
  private BigDecimal thresholdValue;
  private String conditionType;
  private String notificationMethod;
  private String notificationTarget;

  // Getters and setters

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
}
