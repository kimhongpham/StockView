package com.recognition.controller;

import com.recognition.config.SecurityConfig;
import com.recognition.entity.Asset;
import com.recognition.entity.Price;
import com.recognition.exception.ResourceNotFoundException;
import com.recognition.service.AssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AssetController.class)
@AutoConfigureMockMvc(addFilters = false)
class AssetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AssetService assetService;

    private Asset mockAsset;

    @BeforeEach
    void setup() {
        mockAsset = new Asset();
        mockAsset.setId(UUID.randomUUID());
        mockAsset.setSymbol("AAPL");
        mockAsset.setName("Apple Inc.");
    }

    @Test
    void testGetAllAssets() throws Exception {
        Mockito.when(assetService.getAllAssets()).thenReturn(List.of(mockAsset));

        mockMvc.perform(get("/api/assets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].symbol").value("AAPL"));
    }

    @Test
    void testCheckAssetExists() throws Exception {
        Mockito.when(assetService.existsBySymbol("AAPL")).thenReturn(true);

        mockMvc.perform(get("/api/assets/exists/AAPL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(true));
    }

    @Test
    void testGetAssetOverview() throws Exception {
        Map<String, Object> overview = Map.of("symbol", "AAPL", "company", "Apple Inc.");
        Mockito.when(assetService.getAssetOverview("AAPL")).thenReturn(overview);

        mockMvc.perform(get("/api/assets/AAPL/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.company").value("Apple Inc."));
    }

    @Test
    void testDeleteAsset_Success() throws Exception {
        mockMvc.perform(delete("/api/assets/{id}", UUID.randomUUID()))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteAsset_NotFound() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new ResourceNotFoundException("Not found")).when(assetService).deleteAsset(id);

        mockMvc.perform(delete("/api/assets/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFetchNewMarketStocks() throws Exception {
        List<Map<String, Object>> newStocks = List.of(Map.of("symbol", "TSLA"));
        Mockito.when(assetService.fetchNewMarketStocks(10)).thenReturn(newStocks);

        mockMvc.perform(get("/api/assets/market/stocks/new"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].symbol").value("TSLA"));
    }

    @Test
    void testFetchAndSaveLatestPrice() throws Exception {
        Price mockPrice = new Price();
        mockPrice.setId(UUID.randomUUID());
        mockPrice.setPrice(new java.math.BigDecimal("123.45"));

        Mockito.when(assetService.fetchAndSavePrice(any(UUID.class))).thenReturn(mockPrice);

        mockMvc.perform(post("/api/assets/prices/{id}/fetch", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(123.45));
    }

    @Test
    void testSearchAssets() throws Exception {
        Mockito.when(assetService.searchAssets("apple")).thenReturn(List.of(mockAsset));

        mockMvc.perform(get("/api/assets/search").param("query", "apple"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].symbol").value("AAPL"));
    }
}
