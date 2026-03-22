package com.company.pricing_srv.domain.service;

import com.company.pricing_srv.domain.model.Price;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@UtilityClass
public class ApplicablePriceSelector {

    public Optional<Price> select(List<Price> candidatePrices, LocalDateTime applicationDate) {
        return candidatePrices.stream()
                .filter(price -> price.appliesAt(applicationDate))
                .max(Comparator.comparingInt(Price::priority)
                        .thenComparing(Price::startDate));
    }
}
