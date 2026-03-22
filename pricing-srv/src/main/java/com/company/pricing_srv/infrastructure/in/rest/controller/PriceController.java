package com.company.pricing_srv.infrastructure.in.rest.controller;

import com.company.pricing_srv.domain.model.ApplicablePrice;
import com.company.pricing_srv.domain.model.PriceQuery;
import com.company.pricing_srv.domain.port.in.GetApplicablePriceUseCase;
import com.company.pricing_srv.infrastructure.in.rest.dto.ApplicablePriceResponse;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Validated
@RestController
@RequestMapping("/api/v1/prices")
@RequiredArgsConstructor
public class PriceController {

    private final GetApplicablePriceUseCase getApplicablePriceUseCase;

    @GetMapping
    public ApplicablePriceResponse getApplicablePrice(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime applicationDate,
            @RequestParam @NotNull @Positive Long productId,
            @RequestParam @NotNull @Positive Long brandId
    ) {
        ApplicablePrice applicablePrice = getApplicablePriceUseCase.getApplicablePrice(
                new PriceQuery(applicationDate, productId, brandId)
        );

        return new ApplicablePriceResponse(
                applicablePrice.brandId(),
                applicablePrice.productId(),
                applicablePrice.priceList(),
                applicablePrice.startDate(),
                applicablePrice.endDate(),
                applicablePrice.price(),
                applicablePrice.currency()
        );
    }
}
