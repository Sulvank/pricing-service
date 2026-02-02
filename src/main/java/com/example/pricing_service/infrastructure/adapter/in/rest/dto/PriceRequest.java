package com.example.pricing_service.infrastructure.adapter.in.rest.dto;

import jakarta. validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PriceRequest {

    @NotNull(message = "Application date is required")
    private LocalDateTime applicationDate;

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Brand ID is required")
    private Long brandId;
}