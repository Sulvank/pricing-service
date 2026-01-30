package com.example.pricing_service.domain.port.in;

import com.example.pricing_service.domain.model.Price;
import com.example.pricing_service.domain.model.PriceQuery;

import java.util.Optional;

public interface FindApplicablePriceUseCase {
    Optional<Price> findApplicablePrice(PriceQuery query);
}