package com.example.pricing_service.application.service;

import com.example.pricing_service.domain.model.Price;
import com.example.pricing_service.domain.model.PriceQuery;
import com.example.pricing_service.domain.port.out.PriceRepository;
import com.example.pricing_service.domain.service.PriceSelectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindApplicablePriceServiceTest {

    @Mock
    private PriceRepository priceRepository;

    private PriceSelectionService priceSelectionService;
    private FindApplicablePriceService service;

    @BeforeEach
    void setUp() {
        priceSelectionService = new PriceSelectionService();
        service = new FindApplicablePriceService(priceRepository, priceSelectionService);
    }

    @Test
    void shouldReturnApplicablePriceWhenFound() {
        // Given
        PriceQuery query = PriceQuery.builder()
                .applicationDate(LocalDateTime.of(2020, 6, 14, 10, 0))
                .productId(35455L)
                .brandId(1L)
                .build();

        Price price1 = Price.builder()
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

        List<Price> prices = Collections.singletonList(price1);
        when(priceRepository.findByQuery(query)).thenReturn(prices);

        // When
        Optional<Price> result = service.findApplicablePrice(query);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals(new BigDecimal("35.50"), result.get().getFinalPrice());
        verify(priceRepository, times(1)).findByQuery(query);
    }

    @Test
    void shouldReturnEmptyWhenNoPricesFound() {
        // Given
        PriceQuery query = PriceQuery.builder()
                .applicationDate(LocalDateTime.of(2020, 6, 14, 10, 0))
                .productId(35455L)
                .brandId(1L)
                .build();

        when(priceRepository.findByQuery(query)).thenReturn(Collections.emptyList());

        // When
        Optional<Price> result = service.findApplicablePrice(query);

        // Then
        assertFalse(result.isPresent());
        verify(priceRepository, times(1)).findByQuery(query);
    }

    @Test
    void shouldSelectCorrectPriceWhenMultiplePricesApply() {
        // Given
        PriceQuery query = PriceQuery.builder()
                .applicationDate(LocalDateTime.of(2020, 6, 14, 16, 0))
                .productId(35455L)
                .brandId(1L)
                .build();

        Price lowPriority = Price.builder()
                .id(1L)
                .priority(0)
                .startDate(LocalDateTime.of(2020, 6, 14, 0, 0))
                .endDate(LocalDateTime.of(2020, 12, 31, 23, 59))
                .finalPrice(new BigDecimal("35.50"))
                .build();

        Price highPriority = Price.builder()
                .id(2L)
                .priority(1)
                .startDate(LocalDateTime.of(2020, 6, 14, 15, 0))
                .endDate(LocalDateTime.of(2020, 6, 14, 18, 30))
                .finalPrice(new BigDecimal("25.45"))
                .build();

        List<Price> prices = Arrays.asList(lowPriority, highPriority);
        when(priceRepository.findByQuery(query)).thenReturn(prices);

        // When
        Optional<Price> result = service.findApplicablePrice(query);

        // Then
        assertTrue(result.isPresent());
        assertEquals(2L, result.get().getId());
        assertEquals(1, result.get().getPriority());
        verify(priceRepository, times(1)).findByQuery(query);
    }
}