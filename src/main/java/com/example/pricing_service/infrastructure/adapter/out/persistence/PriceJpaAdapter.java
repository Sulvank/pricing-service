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

@Component
@RequiredArgsConstructor
public class PriceJpaAdapter implements PriceRepository {

    private final PriceJpaRepository jpaRepository;
    private final PriceEntityMapper mapper;

    @Override
    public List<Price> findByQuery(PriceQuery query) {
        List<PriceEntity> entities = jpaRepository.findApplicablePrices(
                query.getProductId(),
                query.getBrandId(),
                query.getApplicationDate()
        );
        return mapper.toDomainList(entities);
    }
}
