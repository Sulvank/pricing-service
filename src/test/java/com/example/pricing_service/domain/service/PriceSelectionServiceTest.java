package com.example.pricing_service.domain.service;

import com.example.pricing_service.domain.model.Price;
import com.example.pricing_service.domain.model.PriceQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PriceSelectionServiceTest {

    private PriceSelectionService service;

    @BeforeEach
    void setUp() {
        service = new PriceSelectionService();
    }

    @Test
    void shouldSelectPriceWithHighestPriority() {
        // Given
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 16, 0);

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
        PriceQuery query = PriceQuery.builder()
                .applicationDate(applicationDate)
                .productId(35455L)
                .brandId(1L)
                .build();

        // When
        Optional<Price> result = service.selectApplicablePrice(prices, query);

        // Then
        assertTrue(result.isPresent());
        assertEquals(2L, result.get().getId());
        assertEquals(1, result.get().getPriority());
    }

    @Test
    void shouldSelectPriceWithLatestStartDateWhenPrioritiesAreEqual() {
        // Given
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 15, 10, 0);

        Price earlierStart = Price.builder()
                .id(1L)
                .priority(1)
                .startDate(LocalDateTime.of(2020, 6, 14, 0, 0))
                .endDate(LocalDateTime.of(2020, 12, 31, 23, 59))
                .finalPrice(new BigDecimal("30.50"))
                .build();

        Price laterStart = Price.builder()
                .id(3L)
                .priority(1)
                .startDate(LocalDateTime.of(2020, 6, 15, 0, 0))
                .endDate(LocalDateTime.of(2020, 6, 15, 11, 0))
                .finalPrice(new BigDecimal("30.50"))
                .build();

        List<Price> prices = Arrays.asList(earlierStart, laterStart);
        PriceQuery query = PriceQuery.builder()
                .applicationDate(applicationDate)
                .productId(35455L)
                .brandId(1L)
                .build();

        // When
        Optional<Price> result = service.selectApplicablePrice(prices, query);

        // Then
        assertTrue(result.isPresent());
        assertEquals(3L, result.get().getId());
        assertEquals(LocalDateTime.of(2020, 6, 15, 0, 0), result.get().getStartDate());
    }

    @Test
    void shouldReturnEmptyWhenNoPricesApply() {
        // Given
        LocalDateTime applicationDate = LocalDateTime.of(2021, 1, 1, 10, 0);

        Price price = Price.builder()
                .id(1L)
                .priority(0)
                .startDate(LocalDateTime.of(2020, 6, 14, 0, 0))
                .endDate(LocalDateTime.of(2020, 12, 31, 23, 59))
                .finalPrice(new BigDecimal("35.50"))
                .build();

        List<Price> prices = Collections.singletonList(price);
        PriceQuery query = PriceQuery.builder()
                .applicationDate(applicationDate)
                .productId(35455L)
                .brandId(1L)
                .build();

        // When
        Optional<Price> result = service.selectApplicablePrice(prices, query);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenPriceListIsEmpty() {
        // Given
        PriceQuery query = PriceQuery.builder()
                .applicationDate(LocalDateTime.of(2020, 6, 14, 10, 0))
                .productId(35455L)
                .brandId(1L)
                .build();

        // When
        Optional<Price> result = service.selectApplicablePrice(Collections.emptyList(), query);

        // Then
        assertFalse(result.isPresent());
    }
}