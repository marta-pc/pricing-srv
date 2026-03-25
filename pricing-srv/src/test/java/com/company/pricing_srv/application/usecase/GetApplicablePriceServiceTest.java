package com.company.pricing_srv.application.usecase;

import com.company.pricing_srv.domain.exception.PriceNotFoundException;
import com.company.pricing_srv.domain.model.ApplicablePrice;
import com.company.pricing_srv.domain.model.Price;
import com.company.pricing_srv.domain.model.PriceQuery;
import com.company.pricing_srv.domain.port.out.LoadPricesPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetApplicablePriceServiceTest {

    @Test
    void shouldReturnApplicablePriceFromSelectedPersistenceResult() {
        LoadPricesPort loadPricesPort = (brandId, productId, applicationDate) -> List.of(
                price(1, 0, "35.50", "2020-06-14T00:00:00", "2020-12-31T23:59:59"),
                price(2, 1, "25.45", "2020-06-14T15:00:00", "2020-06-14T18:30:00")
        );
        GetApplicablePriceService service = new GetApplicablePriceService(loadPricesPort);

        ApplicablePrice result = service.getApplicablePrice(new PriceQuery(
                LocalDateTime.parse("2020-06-14T16:00:00"),
                35455L,
                1L
        ));

        assertThat(result.brandId()).isEqualTo(1L);
        assertThat(result.productId()).isEqualTo(35455L);
        assertThat(result.priceList()).isEqualTo(2);
        assertThat(result.price()).isEqualByComparingTo("25.45");
        assertThat(result.currency()).isEqualTo("EUR");
    }

    @Test
    void shouldThrowWhenNoApplicablePriceExists() {
        LoadPricesPort loadPricesPort = (brandId, productId, applicationDate) -> List.of();
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
