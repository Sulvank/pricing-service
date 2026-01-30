package com.example.pricing_service.domain.port.out;

import com.example.pricing_service.domain.model.Price;
import com.example.pricing_service.domain.model.PriceQuery;

import java.util.List;

public interface PriceRepository {
    List<Price> findByQuery(PriceQuery query);
}