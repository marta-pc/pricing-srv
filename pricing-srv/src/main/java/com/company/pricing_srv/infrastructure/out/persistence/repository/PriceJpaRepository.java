package com.company.pricing_srv.infrastructure.out.persistence.repository;

import com.company.pricing_srv.infrastructure.out.persistence.entity.PriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceJpaRepository extends JpaRepository<PriceEntity, Long> {

    List<PriceEntity> findByBrandIdAndProductId(Long brandId, Long productId);
}
