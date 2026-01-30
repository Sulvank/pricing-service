package com.example.pricing_service.application.service;

import com.example.pricing_service.domain.model.Price;
import com.example.pricing_service.domain.model.PriceQuery;
import com.example.pricing_service.domain.port.in.FindApplicablePriceUseCase;
import com.example.pricing_service.domain.port.out.PriceRepository;
import com.example.pricing_service.domain.service.PriceSelectionService;

import java.util.List;
import java.util.Optional;

public class FindApplicablePriceService implements FindApplicablePriceUseCase {

    private final PriceRepository priceRepository;
    private final PriceSelectionService priceSelectionService;

    public FindApplicablePriceService(PriceRepository priceRepository,
                                      PriceSelectionService priceSelectionService) {
        this.priceRepository = priceRepository;
        this.priceSelectionService = priceSelectionService;
    }

    @Override
    public Optional<Price> findApplicablePrice(PriceQuery query) {
        List<Price> prices = priceRepository.findByQuery(query);
        return priceSelectionService.selectApplicablePrice(prices, query);
    }
}
