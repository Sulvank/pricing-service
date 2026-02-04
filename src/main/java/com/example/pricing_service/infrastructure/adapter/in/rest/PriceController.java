package com.example.pricing_service.infrastructure.adapter.in.rest;

import com.example.pricing_service.domain.model.PriceQuery;
import com.example.pricing_service.application.usecase.FindApplicablePriceUseCase;
import com.example.pricing_service.infrastructure.adapter.in.rest.dto.PriceResponse;
import com.example.pricing_service.infrastructure.adapter.in.rest.mapper.PriceDtoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/prices")
@RequiredArgsConstructor
@Validated
public class PriceController {

    private final FindApplicablePriceUseCase findApplicablePriceUseCase;
    private final PriceDtoMapper mapper;

    @GetMapping
    public ResponseEntity<PriceResponse> getApplicablePrice(
            @RequestParam @NotNull
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime applicationDate,

            @RequestParam @NotNull
            Long productId,

            @RequestParam @NotNull
            Long brandId) {

        PriceQuery query = PriceQuery.builder()
                .applicationDate(applicationDate)
                .productId(productId)
                .brandId(brandId)
                .build();

        return findApplicablePriceUseCase.findApplicablePrice(query)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
