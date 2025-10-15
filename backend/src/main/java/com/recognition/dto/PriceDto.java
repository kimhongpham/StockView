package com.recognition.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class PriceDto {
  private UUID assetId;
  private BigDecimal price;
  private OffsetDateTime timestamp;

  private Long volume;
  private BigDecimal changePercent;
  private BigDecimal high24h;
  private BigDecimal low24h;
  private BigDecimal marketCap;
  private String source;

  // Getters & Setters
  public UUID getAssetId() {
    return assetId;
  }

  public void setAssetId(UUID assetId) {
    this.assetId = assetId;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public OffsetDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(OffsetDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public Long getVolume() {
    return volume;
  }

  public void setVolume(Long volume) {
    this.volume = volume;
  }

  public BigDecimal getChangePercent() {
    return changePercent;
  }

  public void setChangePercent(BigDecimal changePercent) {
    this.changePercent = changePercent;
  }

  public BigDecimal getHigh24h() {
    return high24h;
  }

  public void setHigh24h(BigDecimal high24h) {
    this.high24h = high24h;
  }

  public BigDecimal getLow24h() {
    return low24h;
  }

  public void setLow24h(BigDecimal low24h) {
    this.low24h = low24h;
  }

  public BigDecimal getMarketCap() {
    return marketCap;
  }

  public void setMarketCap(BigDecimal marketCap) {
    this.marketCap = marketCap;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }
}
