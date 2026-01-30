package com.example.pricing_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class PriceQuery {
    private LocalDateTime applicationDate;
    private Long productId;
    private Long brandId;
}