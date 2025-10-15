package com.recognition.dto;

import com.recognition.entity.Asset;
import java.util.UUID;

public class AssetDto {
  private UUID id;
  private String name;
  private String symbol;
  private Boolean isActive;

  // Constructor mặc định
  public AssetDto() {}

  // Constructor đầy đủ
  public AssetDto(UUID id, String name, String symbol, Boolean isActive) {
    this.id = id;
    this.name = name;
    this.symbol = symbol;
    this.isActive = isActive;
  }

  // Getters
  public UUID getId() { return id; }
  public String getName() { return name; }
  public String getSymbol() { return symbol; }
  public Boolean getIsActive() { return isActive; }

  // Setters
  public void setId(UUID id) { this.id = id; }
  public void setName(String name) { this.name = name; }
  public void setSymbol(String symbol) { this.symbol = symbol; }
  public void setIsActive(Boolean isActive) { this.isActive = isActive; }

  // Builder
  public static class Builder {
    private UUID id;
    private String name;
    private String symbol;
    private Boolean isActive;

    public Builder id(UUID id) { this.id = id; return this; }
    public Builder name(String name) { this.name = name; return this; }
    public Builder symbol(String symbol) { this.symbol = symbol; return this; }
    public Builder isActive(Boolean isActive) { this.isActive = isActive; return this; }

    public AssetDto build() {
      return new AssetDto(id, name, symbol, isActive);
    }
  }
}
