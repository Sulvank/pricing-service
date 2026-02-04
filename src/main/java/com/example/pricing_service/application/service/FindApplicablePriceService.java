package com.example.pricing_service.application.service;

import com.example.pricing_service.domain.model.Price;
import com.example.pricing_service.domain.model.PriceQuery;
import com.example.pricing_service.application.usecase.FindApplicablePriceUseCase;
import com.example.pricing_service.domain.port.out.PriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FindApplicablePriceService implements FindApplicablePriceUseCase {

    private final PriceRepository priceRepository;

    @Override
    public Optional<Price> findApplicablePrice(PriceQuery query) {
        List<Price> prices = priceRepository.findByQuery(query).orElse(List.of());
        return prices.stream()
                .filter(price -> price.isApplicableFor(query.applicationDate()))
                .max(Comparator
                        .comparing(Price::getPriority)
                        .thenComparing(Price::getStartDate));
    }
}
