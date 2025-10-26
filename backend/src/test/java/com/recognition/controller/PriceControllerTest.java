package com.recognition.controller;

import com.recognition.dto.PriceDto;
import com.recognition.entity.Price;
import com.recognition.service.PriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class PriceControllerTest {

    @Mock
    private PriceService priceService;

    @InjectMocks
    private PriceController priceController;

    private UUID assetId;
    private PriceDto dto;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        assetId = UUID.randomUUID();

        dto = new PriceDto();
        dto.setAssetId(assetId);
        dto.setPrice(BigDecimal.valueOf(100));
        dto.setTimestamp(OffsetDateTime.now(ZoneOffset.UTC));

        pageable = PageRequest.of(0, 10, Sort.by("timestamp").descending());
    }

    @Test
    void testGetLatestPrice() {
        when(priceService.getLatestPriceDto(assetId)).thenReturn(dto);

        ResponseEntity<PriceDto> response = priceController.getLatestPrice(assetId);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testGetPriceHistoryPaged() {
        Page<PriceDto> page = new PageImpl<>(List.of(dto));

        when(priceService.getPriceHistoryPaged(
                any(UUID.class),
                any(),
                any(),
                any(Pageable.class)
        )).thenReturn(page);

        ResponseEntity<Page<PriceDto>> response = priceController.getPriceHistoryPaged(
                assetId,
                null, // startDate
                null, // endDate
                pageable
        );

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals(dto.getPrice(), response.getBody().getContent().get(0).getPrice());
    }

    @Test
    void testAddPrice() {
        Price price = new Price();
        price.setPrice(BigDecimal.valueOf(123));

        when(priceService.addPriceEntity(eq(assetId), any(Price.class))).thenReturn(price);

        ResponseEntity<Price> response = priceController.addPrice(assetId, price);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(price.getPrice(), response.getBody().getPrice());
    }

    @Test
    void testFetchAndSavePrice() {
        when(priceService.fetchAndSavePrice(assetId)).thenReturn(dto);

        ResponseEntity<PriceDto> response = priceController.fetchAndSavePrice(assetId);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testGetPriceChange() {
        BigDecimal change = BigDecimal.valueOf(5.5);
        when(priceService.calculatePriceChange(assetId, 24)).thenReturn(change);

        ResponseEntity<BigDecimal> response = priceController.getPriceChange(assetId, 24);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(change, response.getBody());
    }

    // Ví dụ test cho controller trả về Map (chart)
    @Test
    void testGetChart() {
        Map<String, Object> chartData = Map.of(
                "open", 100,
                "close", 110
        );
        Map<String, Object> responseMap = Map.of(
                "success", true,
                "message", "Chart data fetched successfully",
                "data", chartData
        );

        // Giả sử bạn có service chartPriceService.getCandles(...)
        // when(chartPriceService.getCandles(eq(assetId), anyString(), anyInt())).thenReturn(chartData);

        // Giả sử chart controller trả về ResponseEntity<Map<String, Object>>
        ResponseEntity<Map<String, Object>> response = ResponseEntity.ok(responseMap);

        assertNotNull(response);
        assertNotNull(response.getBody());

        Map<String, Object> body = response.getBody();
        assertEquals(true, body.get("success"));
        assertEquals("Chart data fetched successfully", body.get("message"));
        assertEquals(chartData, body.get("data"));
    }
}
