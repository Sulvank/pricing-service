package com.example.pricing_service.domain.exception;

public class PriceNotFoundException extends RuntimeException {

    public PriceNotFoundException(String message) {
        super(message);
    }

    public static PriceNotFoundException forQuery(Long productId, Long brandId) {
        return new PriceNotFoundException(
                String.format("No applicable price found for productId=%d and brandId=%d", productId, brandId)
        );
    }
}
