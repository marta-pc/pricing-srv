package com.company.pricing_srv.domain.service;

import com.company.pricing_srv.domain.model.Price;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicablePriceSelectorTest {

    @ParameterizedTest(name = "[{index}] {0} -> price list {1} / price {2}")
    @CsvSource({
            "2020-06-14T10:00:00,1,35.50",
            "2020-06-14T16:00:00,2,25.45",
            "2020-06-14T21:00:00,1,35.50",
            "2020-06-15T10:00:00,3,30.50",
            "2020-06-16T21:00:00,4,38.95"
    })
    void shouldSelectExpectedApplicablePrice(String applicationDate, int expectedPriceList, String expectedPrice) {
        Price selectedPrice = ApplicablePriceSelector.select(samplePrices(), LocalDateTime.parse(applicationDate)).orElseThrow();

        assertThat(selectedPrice.priceList()).isEqualTo(expectedPriceList);
        assertThat(selectedPrice.price()).isEqualByComparingTo(expectedPrice);
    }

    @ParameterizedTest(name = "[{index}] boundary {0} -> price list {1}")
    @CsvSource({
            "2020-06-14T00:00:00,1",
            "2020-06-14T15:00:00,2",
            "2020-06-14T18:30:00,2",
            "2020-06-15T00:00:00,3",
            "2020-06-15T11:00:00,3",
            "2020-06-15T16:00:00,4",
            "2020-12-31T23:59:59,4"
    })
    void shouldTreatStartAndEndDatesAsInclusive(String applicationDate, int expectedPriceList) {
        Price selectedPrice = ApplicablePriceSelector.select(samplePrices(), LocalDateTime.parse(applicationDate)).orElseThrow();

        assertThat(selectedPrice.priceList()).isEqualTo(expectedPriceList);
    }

    @ParameterizedTest(name = "[{index}] no match at {0}")
    @ValueSource(strings = {
            "2020-06-13T23:59:59",
            "2021-01-01T00:00:00"
    })
    void shouldReturnEmptyWhenNoPriceMatchesApplicationDate(String applicationDate) {
        assertThat(ApplicablePriceSelector.select(samplePrices(), LocalDateTime.parse(applicationDate))).isEmpty();
    }

    private List<Price> samplePrices() {
        return List.of(
                price(1, 0, "35.50", "2020-06-14T00:00:00", "2020-12-31T23:59:59"),
                price(2, 1, "25.45", "2020-06-14T15:00:00", "2020-06-14T18:30:00"),
                price(3, 1, "30.50", "2020-06-15T00:00:00", "2020-06-15T11:00:00"),
                price(4, 1, "38.95", "2020-06-15T16:00:00", "2020-12-31T23:59:59")
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
