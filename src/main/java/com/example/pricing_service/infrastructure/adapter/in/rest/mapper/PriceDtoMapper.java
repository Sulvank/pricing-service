package com.example.pricing_service.infrastructure.adapter.in.rest.mapper;

import com.example.pricing_service.domain.model.Price;
import com.example.pricing_service.infrastructure.adapter.in.rest.dto.PriceResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PriceDtoMapper {

    PriceResponse toResponse(Price price);
}