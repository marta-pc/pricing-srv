package com.company.pricing_srv.domain.model;

import java.time.LocalDateTime;

public record PriceQuery(
        LocalDateTime applicationDate,
        Long productId,
        Long brandId
) {
}
