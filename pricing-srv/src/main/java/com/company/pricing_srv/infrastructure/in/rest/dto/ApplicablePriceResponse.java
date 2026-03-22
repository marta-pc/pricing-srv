package com.company.pricing_srv.infrastructure.in.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ApplicablePriceResponse(
        Long brandId,
        Long productId,
        int priceList,
        LocalDateTime startDate,
        LocalDateTime endDate,
        BigDecimal price,
        String currency
) {
}
