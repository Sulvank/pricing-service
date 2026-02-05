package com.example.pricing_service.application.service;

import com.example.pricing_service.domain.exception.PriceNotFoundException;
import com.example.pricing_service.domain.model.Price;
import com.example.pricing_service.domain.model.PriceQuery;
import com.example.pricing_service.domain.port.out.PriceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindApplicablePriceServiceTest {

    @Mock
    private PriceRepository priceRepository;

    @InjectMocks
    private FindApplicablePriceService service;

    @Test
    void shouldReturnPriceWhenFound() {
        // Given
        PriceQuery query = PriceQuery.builder()
                .applicationDate(LocalDateTime.of(2020, 6, 14, 10, 0))
                .productId(35455L)
                .brandId(1L)
                .build();

        Price expectedPrice = Price.builder()
                .id(1L)
                .brandId(1L)
                .productId(35455L)
                .priceList(1L)
                .priority(0)
                .startDate(LocalDateTime.of(2020, 6, 14, 0, 0))
                .endDate(LocalDateTime.of(2020, 12, 31, 23, 59))
                .finalPrice(new BigDecimal("35.50"))
                .currency("EUR")
                .build();

        when(priceRepository.findByQuery(any()))
                .thenReturn(List.of(expectedPrice));

        // When
        Price result = service.findApplicablePrice(query);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(new BigDecimal("35.50"), result.getFinalPrice());
        verify(priceRepository, times(1)).findByQuery(query);
    }

    // ... mantener imports existentes ...
// Añadir esta aserción detallada en el test de excepción:

    @Test
    void shouldThrowExceptionWhenNoPriceFound() {
        // Given
        Long productId = 99999L;
        Long brandId = 1L;
        PriceQuery query = PriceQuery.builder()
                .applicationDate(LocalDateTime.now())
                .productId(productId)
                .brandId(brandId)
                .build();

        when(priceRepository.findByQuery(any())).thenReturn(List.of());

        // When & Then
        PriceNotFoundException exception = assertThrows(
                PriceNotFoundException.class,
                () -> service.findApplicablePrice(query)
        );

        // Verificamos que el mensaje de error sea el esperado (inyectado por PriceNotFoundException.forQuery)
        String expectedMessage = String.format("No applicable price found for productId=%d and brandId=%d", productId, brandId);
        assertEquals(expectedMessage, exception.getMessage());
    }
}