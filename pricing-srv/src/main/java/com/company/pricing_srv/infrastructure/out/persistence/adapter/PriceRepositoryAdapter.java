package com.company.pricing_srv.infrastructure.out.persistence.adapter;

import com.company.pricing_srv.domain.model.Price;
import com.company.pricing_srv.domain.port.out.LoadPricesPort;
import com.company.pricing_srv.infrastructure.out.persistence.mapper.PriceEntityMapper;
import com.company.pricing_srv.infrastructure.out.persistence.repository.PriceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PriceRepositoryAdapter implements LoadPricesPort {

    private final PriceJpaRepository priceJpaRepository;

    @Override
    public List<Price> findApplicablePrices(Long brandId, Long productId, LocalDateTime applicationDate) {
        return priceJpaRepository.findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(brandId, productId, applicationDate, applicationDate).stream()
                .map(PriceEntityMapper::toDomain)
                .toList();
    }
}
