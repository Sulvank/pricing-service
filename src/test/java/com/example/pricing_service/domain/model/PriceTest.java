package com.example.pricing_service.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class PriceTest {

    @Test
    @DisplayName("Should return true when date is exactly the start date")
    void shouldReturnTrueWhenDateIsExactlyStartDate() {
        LocalDateTime start = LocalDateTime.of(2020, 6, 14, 10, 0);
        Price price = Price.builder()
                .startDate(start)
                .endDate(start.plusDays(1))
                .build();

        assertTrue(price.isApplicableFor(start));
    }

    @Test
    @DisplayName("Should return true when date is exactly the end date")
    void shouldReturnTrueWhenDateIsExactlyEndDate() {
        LocalDateTime end = LocalDateTime.of(2020, 6, 14, 10, 0);
        Price price = Price.builder()
                .startDate(end.minusDays(1))
                .endDate(end)
                .build();

        assertTrue(price.isApplicableFor(end));
    }

    @Test
    @DisplayName("Should return false when date is one second after end date")
    void shouldReturnFalseWhenDateIsAfterEndDate() {
        LocalDateTime end = LocalDateTime.of(2020, 6, 14, 10, 0);
        Price price = Price.builder()
                .startDate(end.minusDays(1))
                .endDate(end)
                .build();

        assertFalse(price.isApplicableFor(end.plusSeconds(1)));
    }

    @Test
    void shouldBeApplicableWhenDateIsWithinRange() {
        LocalDateTime start = LocalDateTime.of(2020, 6, 14, 0, 0);
        LocalDateTime end = LocalDateTime.of(2020, 6, 14, 23, 59);
        Price price = Price.builder().startDate(start).endDate(end).build();

        assertTrue(price.isApplicableFor(start), "Should be applicable at start date");
        assertTrue(price.isApplicableFor(end), "Should be applicable at end date");
        assertTrue(price.isApplicableFor(start.plusHours(10)), "Should be applicable within range");
    }

    @Test
    void shouldNotBeApplicableWhenDateIsOutsideRange() {
        LocalDateTime start = LocalDateTime.of(2020, 6, 14, 0, 0);
        LocalDateTime end = LocalDateTime.of(2020, 6, 14, 23, 59);
        Price price = Price.builder().startDate(start).endDate(end).build();

        assertFalse(price.isApplicableFor(start.minusSeconds(1)), "Should not be applicable before start");
        assertFalse(price.isApplicableFor(end.plusSeconds(1)), "Should not be applicable after end");
    }
}