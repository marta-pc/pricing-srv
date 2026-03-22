package com.company.pricing_srv.application.usecase;

import com.company.pricing_srv.domain.exception.PriceNotFoundException;
import com.company.pricing_srv.domain.model.ApplicablePrice;
import com.company.pricing_srv.domain.model.Price;
import com.company.pricing_srv.domain.model.PriceQuery;
import com.company.pricing_srv.domain.port.out.LoadPricesPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetApplicablePriceServiceTest {

    @ParameterizedTest(name = "[{index}] {0} -> price list {1} / price {2}")
    @CsvSource({
            "2020-06-14T10:00:00,1,35.50",
            "2020-06-14T16:00:00,2,25.45",
            "2020-06-14T21:00:00,1,35.50",
            "2020-06-15T10:00:00,3,30.50",
            "2020-06-16T21:00:00,4,38.95"
    })
    void shouldReturnApplicablePriceForQuery(String applicationDate, int expectedPriceList, String expectedPrice) {
        GetApplicablePriceService service = new GetApplicablePriceService((brandId, productId) -> samplePrices());

        ApplicablePrice result = service.getApplicablePrice(new PriceQuery(
                LocalDateTime.parse(applicationDate),
                35455L,
                1L
        ));

        assertThat(result.priceList()).isEqualTo(expectedPriceList);
        assertThat(result.price()).isEqualByComparingTo(expectedPrice);
        assertThat(result.currency()).isEqualTo("EUR");
    }

    @Test
    void shouldThrowWhenNoApplicablePriceExists() {
        LoadPricesPort loadPricesPort = (brandId, productId) -> List.of(
                price(1, 0, "35.50", "2020-06-14T00:00:00", "2020-06-14T10:00:00")
        );
        GetApplicablePriceService service = new GetApplicablePriceService(loadPricesPort);

        PriceQuery query = new PriceQuery(
                LocalDateTime.parse("2020-06-14T10:00:01"),
                35455L,
                1L
        );

        assertThatThrownBy(() -> service.getApplicablePrice(query))
                .isInstanceOf(PriceNotFoundException.class)
                .hasMessageContaining("brandId=1")
                .hasMessageContaining("productId=35455");
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