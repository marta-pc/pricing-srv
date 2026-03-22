package com.company.pricing_srv.domain.port.in;

import com.company.pricing_srv.domain.model.ApplicablePrice;
import com.company.pricing_srv.domain.model.PriceQuery;

public interface GetApplicablePriceUseCase {

    ApplicablePrice getApplicablePrice(PriceQuery query);
}
