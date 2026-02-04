package com.example.pricing_service.infrastructure.adapter.out.persistence;

import com.example.pricing_service.domain.model.Price;
import com.example.pricing_service.domain.model.PriceQuery;
import com.example.pricing_service.domain.port.out.PriceRepository;
import com.example.pricing_service.infrastructure.adapter.out.persistence.entity.PriceEntity;
import com.example.pricing_service.infrastructure.adapter.out.persistence.mapper.PriceEntityMapper;
import com.example.pricing_service.infrastructure.adapter.out.persistence.repository.PriceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PriceJpaAdapter implements PriceRepository {

    private final PriceJpaRepository jpaRepository;
    private final PriceEntityMapper mapper;

    @Override
    public Optional<List<Price>> findByQuery(PriceQuery query) {
        List<PriceEntity> entities = jpaRepository.findApplicablePrices(
                query.productId(),
                query.brandId(),
                query.applicationDate()
        );
        return Optional.ofNullable(mapper.toDomainList(entities));
    }
}
