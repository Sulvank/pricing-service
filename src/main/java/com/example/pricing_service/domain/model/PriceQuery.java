package com.example.pricing_service.domain.model;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PriceQuery(LocalDateTime applicationDate, Long productId, Long brandId) {
}