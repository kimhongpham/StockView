package com.recognition.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request to add or delete watchlist item.
 */
public class WatchlistRequest {

    @NotBlank
    @Size(max = 50)
    private String symbol;

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
}
