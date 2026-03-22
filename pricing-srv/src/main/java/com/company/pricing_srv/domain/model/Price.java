package com.company.pricing_srv.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Price(
        Long brandId,
        Long productId,
        int priceList,
        int priority,
        BigDecimal price,
        String currency,
        LocalDateTime startDate,
        LocalDateTime endDate
) {

    public boolean appliesAt(LocalDateTime applicationDate) {
        return !applicationDate.isBefore(startDate) && !applicationDate.isAfter(endDate);
    }
}
