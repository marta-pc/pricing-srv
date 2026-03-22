package com.company.pricing_srv.infrastructure.out.persistence.adapter;

import com.company.pricing_srv.domain.model.Price;
import com.company.pricing_srv.domain.port.out.LoadPricesPort;
import com.company.pricing_srv.infrastructure.out.persistence.mapper.PriceEntityMapper;
import com.company.pricing_srv.infrastructure.out.persistence.repository.PriceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PriceRepositoryAdapter implements LoadPricesPort {

    private final PriceJpaRepository priceJpaRepository;

    @Override
    public List<Price> findByBrandIdAndProductId(Long brandId, Long productId) {
        return priceJpaRepository.findByBrandIdAndProductId(brandId, productId).stream()
                .map(PriceEntityMapper::toDomain)
                .toList();
    }
}
