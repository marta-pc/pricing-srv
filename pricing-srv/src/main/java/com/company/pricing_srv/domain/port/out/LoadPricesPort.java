package com.company.pricing_srv.domain.port.out;

import com.company.pricing_srv.domain.model.Price;

import java.time.LocalDateTime;
import java.util.List;

public interface LoadPricesPort {

    List<Price> findApplicablePrices(Long brandId, Long productId, LocalDateTime applicationDate);
}
