package com.company.pricing_srv.domain.service;

import com.company.pricing_srv.domain.model.Price;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicablePriceSelectorTest {

    @Test
    void shouldSelectHighestPriorityWhenMultiplePricesOverlap() {
        Price selectedPrice = ApplicablePriceSelector
                .select(overlappingPrices(), LocalDateTime.parse("2020-06-14T16:30:00"))
                .orElseThrow();

        assertThat(selectedPrice.priceList()).isEqualTo(3);
    }

    @Test
    void shouldSelectMostRecentWhenSamePriority() {
        Price selectedPrice = ApplicablePriceSelector
                .select(overlappingPricesWithSamePriority(), LocalDateTime.parse("2020-06-14T17:30:00"))
                .orElseThrow();

        assertThat(selectedPrice.priceList()).isEqualTo(4);
    }

    @ParameterizedTest(name = "[{index}] boundary {0} -> price list {1}")
    @CsvSource({
            "2020-06-14T00:00:00,1",
            "2020-06-14T18:30:00,1"
    })
    void shouldTreatStartAndEndDatesAsInclusive(String applicationDate, int expectedPriceList) {
        Price selectedPrice = ApplicablePriceSelector.select(singlePrice(), LocalDateTime.parse(applicationDate)).orElseThrow();

        assertThat(selectedPrice.priceList()).isEqualTo(expectedPriceList);
    }

    @ParameterizedTest(name = "[{index}] no match at {0}")
    @ValueSource(strings = {
            "2020-06-13T23:59:59",
            "2020-06-14T18:30:01"
    })
    void shouldReturnEmptyWhenNoPriceMatchesApplicationDate(String applicationDate) {
        assertThat(ApplicablePriceSelector
                .select(singlePrice(), LocalDateTime.parse(applicationDate)))
                .isEmpty();
    }

    private List<Price> singlePrice() {
        return List.of(
                price(1, 0, "35.50", "2020-06-14T00:00:00", "2020-06-14T18:30:00")
        );
    }

    private List<Price> overlappingPrices() {
        return List.of(
                price(1, 0, "35.50", "2020-06-14T00:00:00", "2020-12-31T23:59:59"),
                price(2, 1, "25.45", "2020-06-14T15:00:00", "2020-06-14T23:59:59"),
                price(3, 2, "20.00", "2020-06-14T16:00:00", "2020-06-14T18:00:00")
        );
    }

    private List<Price> overlappingPricesWithSamePriority() {
        return List.of(
                price(3, 2, "20.00", "2020-06-14T16:00:00", "2020-06-14T18:00:00"),
                price(4, 2, "18.00", "2020-06-14T17:00:00", "2020-06-14T18:00:00")
        );
    }

    private Price price(
            int priceList,
            int priority,
            String amount,
            String startDate,
            String endDate
    ) {
        return new Price(
                1L,
                35455L,
                priceList,
                priority,
                new BigDecimal(amount),
                "EUR",
                LocalDateTime.parse(startDate),
                LocalDateTime.parse(endDate)
        );
    }
}

