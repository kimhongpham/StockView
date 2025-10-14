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
@Table(name = "alert_logs", indexes = {
    @Index(name = "idx_alert_logs_alert_id", columnList = "alert_id"),
    @Index(name = "idx_alert_logs_triggered_at", columnList = "triggered_at"),
    @Index(name = "idx_alert_logs_status", columnList = "notification_status")
})
public class AlertLog {

  @Id
  @GeneratedValue
  @Column(columnDefinition = "UUID DEFAULT gen_random_uuid()")
  private UUID id;

  @Column(name = "alert_id", nullable = false)
  private UUID alertId;

  @Column(name = "triggered_price", nullable = false, precision = 18, scale = 8)
  private BigDecimal triggeredPrice;

  @Column(name = "triggered_at", columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT NOW()")
  private OffsetDateTime triggeredAt;

  @Column(name = "notification_status", nullable = false, length = 50)
  private String notificationStatus;

  @Column(name = "message_content", columnDefinition = "TEXT")
  private String messageContent;

  @Column(name = "error_message", columnDefinition = "TEXT")
  private String errorMessage;

  @Column(name = "retry_count", nullable = false)
  private Integer retryCount = 0;

  // Getters and setters

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getAlertId() {
    return alertId;
  }

  public void setAlertId(UUID alertId) {
    this.alertId = alertId;
  }

  public BigDecimal getTriggeredPrice() {
    return triggeredPrice;
  }

  public void setTriggeredPrice(BigDecimal triggeredPrice) {
    this.triggeredPrice = triggeredPrice;
  }

  public OffsetDateTime getTriggeredAt() {
    return triggeredAt;
  }

  public void setTriggeredAt(OffsetDateTime triggeredAt) {
    this.triggeredAt = triggeredAt;
  }

  public String getNotificationStatus() {
    return notificationStatus;
  }

  public void setNotificationStatus(String notificationStatus) {
    this.notificationStatus = notificationStatus;
  }

  public String getMessageContent() {
    return messageContent;
  }

  public void setMessageContent(String messageContent) {
    this.messageContent = messageContent;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public Integer getRetryCount() {
    return retryCount;
  }

  public void setRetryCount(Integer retryCount) {
    this.retryCount = retryCount;
  }
}
