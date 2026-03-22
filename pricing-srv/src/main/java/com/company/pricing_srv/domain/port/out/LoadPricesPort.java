package com.company.pricing_srv.domain.port.out;

import com.company.pricing_srv.domain.model.Price;

import java.util.List;

public interface LoadPricesPort {

    List<Price> findByBrandIdAndProductId(Long brandId, Long productId);
}
