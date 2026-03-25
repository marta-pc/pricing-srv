package com.company.pricing_srv.domain.service;

import com.company.pricing_srv.domain.model.Price;
import lombok.experimental.UtilityClass;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@UtilityClass
public class ApplicablePriceSelector {

    public Optional<Price> select(List<Price> candidatePrices) {
        return candidatePrices.stream()
                .max(Comparator.comparingInt(Price::priority)
                        .thenComparing(Price::startDate));
    }
}
