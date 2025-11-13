package com.recognition.controller;

import com.recognition.dto.PriceDto;
import com.recognition.dto.response.StatisticsDTO;
import com.recognition.entity.Price;
import com.recognition.service.AsyncPriceService;
import com.recognition.service.PriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PriceController.class)
class PriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Thay @Mock thành @MockBean
    @MockBean
    private PriceService priceService;

    @MockBean
    private AsyncPriceService asyncPriceService;

    private UUID assetId;
    private PriceDto mockPrice;

    @BeforeEach
    void setup() {
        assetId = UUID.randomUUID();
        mockPrice = new PriceDto();
        mockPrice.setAssetId(assetId);
        mockPrice.setPrice(BigDecimal.valueOf(120.5));
        mockPrice.setTimestamp(OffsetDateTime.now());
    }

    @Test
    void testGetLatestPrice() throws Exception {
        Mockito.when(priceService.getLatestPriceDto(assetId)).thenReturn(mockPrice);

        mockMvc.perform(get("/api/prices/{id}/latest", assetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(120.5));
    }

    @Test
    void testAddPrice() throws Exception {
        Price price = new Price();
        price.setId(UUID.randomUUID());
        price.setPrice(BigDecimal.valueOf(200));

        Mockito.when(priceService.addPriceEntity(any(UUID.class), any(Price.class))).thenReturn(price);

        mockMvc.perform(post("/api/prices/{id}", assetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"price\":200}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.price").value(200));
    }

    @Test
    void testFetchAndSavePrice() throws Exception {
        Mockito.when(priceService.fetchAndSavePrice(assetId)).thenReturn(mockPrice);

        mockMvc.perform(post("/api/prices/{id}/fetch", assetId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.price").value(120.5));
    }

    @Test
    void testStartFetchAll() throws Exception {
        Mockito.when(asyncPriceService.startJob()).thenReturn("job123");

        mockMvc.perform(post("/api/prices/fetch-all/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobId").value("job123"));
    }

    @Test
    void testGetStatus() throws Exception {
        Mockito.when(asyncPriceService.getJobStatus("job123")).thenReturn(Map.of("progress", 50));

        mockMvc.perform(get("/api/prices/fetch-all/status/job123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.progress").value(50));
    }

    @Test
    void testGetPriceHistoryPaged() throws Exception {
        Page<PriceDto> page = new PageImpl<>(List.of(mockPrice));
        Mockito.when(priceService.getPriceHistoryPaged(any(), any(), any(), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/prices/{id}/history/paged", assetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].price").value(120.5));
    }

    @Test
    void testGetChart() throws Exception {
        Mockito.when(priceService.getCandles(any(), anyString(), anyInt()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/prices/{id}/chart", assetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testGetPriceChange() throws Exception {
        Mockito.when(priceService.calculatePriceChange(assetId, 24))
                .thenReturn(BigDecimal.valueOf(3.5));

        mockMvc.perform(get("/api/prices/{id}/change", assetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(3.5));
    }

    @Test
    void testGetStats() throws Exception {
        StatisticsDTO stats = new StatisticsDTO(
                BigDecimal.valueOf(100),   // minPrice
                BigDecimal.valueOf(200),   // maxPrice
                BigDecimal.valueOf(150),   // avgPrice
                OffsetDateTime.now().minusDays(1), // from
                OffsetDateTime.now()                // to
        );

        Mockito.when(priceService.getStatistics(assetId, "day")).thenReturn(stats);

        mockMvc.perform(get("/api/prices/{id}/stats", assetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.avgPrice").value(150));
    }

    @Test
    void testGetTopMovers() throws Exception {
        Mockito.when(priceService.getTopMovers("gainers", 5))
                .thenReturn(List.of(mockPrice));

        mockMvc.perform(get("/api/prices/top")
                        .param("type", "gainers")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].price").value(120.5));
    }
}
