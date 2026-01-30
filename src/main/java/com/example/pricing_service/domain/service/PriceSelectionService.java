package com.example.pricing_service.domain.service;

import com.example.pricing_service.domain.model.Price;
import com.example.pricing_service.domain.model.PriceQuery;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class PriceSelectionService {

    public Optional<Price> selectApplicablePrice(List<Price> prices, PriceQuery query) {
        return prices.stream()
                .filter(price -> price.isApplicableFor(query.getApplicationDate()))
                .max(Comparator
                        .comparing(Price::getPriority)
                        .thenComparing(Price::getStartDate));
    }
}
