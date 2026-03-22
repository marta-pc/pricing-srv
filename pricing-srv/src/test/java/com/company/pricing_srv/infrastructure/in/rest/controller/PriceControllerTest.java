package com.company.pricing_srv.infrastructure.in.rest.controller;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest(name = "[{index}] {0} -> price list {1} / price {2}")
    @CsvSource({
            "2020-06-14T10:00:00,1,35.50",
            "2020-06-14T16:00:00,2,25.45",
            "2020-06-14T21:00:00,1,35.50",
            "2020-06-15T10:00:00,3,30.50",
            "2020-06-16T21:00:00,4,38.95"
    })
    void shouldReturnExpectedApplicablePrice(String applicationDate, int expectedPriceList, double expectedPrice) throws Exception {
        mockMvc.perform(get("/api/v1/prices")
                        .param("applicationDate", applicationDate)
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(expectedPriceList))
                .andExpect(jsonPath("$.price").value(expectedPrice))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }
}
