package com.example.pricing_service.infrastructure.adapter.out.persistence.repository;

import com.example.pricing_service.infrastructure.adapter.out.persistence.entity.PriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PriceJpaRepository extends JpaRepository<PriceEntity, Long> {

    @Query("SELECT p FROM PriceEntity p WHERE " +
            "p.productId = :productId AND " +
            "p.brandId = :brandId AND " +
            "p.startDate <= :applicationDate AND " +
            "p.endDate >= :applicationDate")
    List<PriceEntity> findApplicablePrices(
            @Param("productId") Long productId,
            @Param("brandId") Long brandId,
            @Param("applicationDate") LocalDateTime applicationDate
    );
}