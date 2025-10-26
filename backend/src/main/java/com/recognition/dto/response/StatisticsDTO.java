package com.recognition.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * DTO tính toán thống kê giá trong khoảng thời gian.
 */
public record StatisticsDTO(
        BigDecimal minPrice,
        BigDecimal maxPrice,
        BigDecimal avgPrice,
        OffsetDateTime from,
        OffsetDateTime to
) {}
