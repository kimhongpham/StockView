package com.recognition.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * DTO dùng để trả dữ liệu nến (candle) cho frontend.
 */
public record CandleDTO(
        OffsetDateTime timestamp,
        BigDecimal open,
        BigDecimal high,
        BigDecimal low,
        BigDecimal close,
        Long volume
) {}