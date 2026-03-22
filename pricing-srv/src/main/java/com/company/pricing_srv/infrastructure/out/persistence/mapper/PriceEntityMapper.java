package com.company.pricing_srv.infrastructure.out.persistence.mapper;

import com.company.pricing_srv.domain.model.Price;
import com.company.pricing_srv.infrastructure.out.persistence.entity.PriceEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PriceEntityMapper {

    public static Price toDomain(PriceEntity entity) {
        return new Price(
                entity.getBrandId(),
                entity.getProductId(),
                entity.getPriceList(),
                entity.getPriority(),
                entity.getPrice(),
                entity.getCurrency(),
                entity.getStartDate(),
                entity.getEndDate()
        );
    }
}
