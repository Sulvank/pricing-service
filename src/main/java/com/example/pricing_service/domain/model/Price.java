package com.example.pricing_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class Price {
    private Long id;
    private Long brandId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long priceList;
    private Long productId;
    private Integer priority;
    private BigDecimal finalPrice;
    private String currency;

    public boolean isApplicableFor(LocalDateTime applicationDate) {
        return !applicationDate.isBefore(startDate)
                && !applicationDate.isAfter(endDate);
    }
}