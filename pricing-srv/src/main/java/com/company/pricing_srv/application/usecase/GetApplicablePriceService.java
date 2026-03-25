package com.company.pricing_srv.application.usecase;

import com.company.pricing_srv.domain.exception.PriceNotFoundException;
import com.company.pricing_srv.domain.model.ApplicablePrice;
import com.company.pricing_srv.domain.model.Price;
import com.company.pricing_srv.domain.model.PriceQuery;
import com.company.pricing_srv.domain.port.in.GetApplicablePriceUseCase;
import com.company.pricing_srv.domain.port.out.LoadPricesPort;
import com.company.pricing_srv.domain.service.ApplicablePriceSelector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetApplicablePriceService implements GetApplicablePriceUseCase {

    private final LoadPricesPort loadPricesPort;

    @Override
    public ApplicablePrice getApplicablePrice(PriceQuery query) {
        Price selectedPrice = ApplicablePriceSelector.select(
                        loadPricesPort.findApplicablePrices(query.brandId(), query.productId(), query.applicationDate())
                )
                .orElseThrow(() -> new PriceNotFoundException(query.brandId(), query.productId()));

        return new ApplicablePrice(
                selectedPrice.brandId(),
                selectedPrice.productId(),
                selectedPrice.priceList(),
                selectedPrice.startDate(),
                selectedPrice.endDate(),
                selectedPrice.price(),
                selectedPrice.currency()
        );
    }
}
