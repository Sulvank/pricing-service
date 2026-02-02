package com.example.pricing_service.infrastructure.adapter.out.persistence.mapper;

import com.example.pricing_service.domain.model.Price;
import com.example.pricing_service.infrastructure.adapter.out.persistence.entity.PriceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PriceEntityMapper {

    @Mapping(source = "price", target = "finalPrice")
    Price toDomain(PriceEntity entity);

    List<Price> toDomainList(List<PriceEntity> entities);
}