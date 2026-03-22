package com.company.pricing_srv.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ApplicablePrice(
        Long brandId,
        Long productId,
        int priceList,
        LocalDateTime startDate,
        LocalDateTime endDate,
        BigDecimal price,
        String currency
) {
}
