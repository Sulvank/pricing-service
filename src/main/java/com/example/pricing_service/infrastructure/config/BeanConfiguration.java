package com.example.pricing_service.infrastructure.config;

import com.example.pricing_service.application.service.FindApplicablePriceService;
import com.example.pricing_service.domain.port.in.FindApplicablePriceUseCase;
import com.example.pricing_service.domain.port.out.PriceRepository;
import com.example.pricing_service.domain.service.PriceSelectionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public PriceSelectionService priceSelectionService() {
        return new PriceSelectionService();
    }

    @Bean
    public FindApplicablePriceUseCase findApplicablePriceUseCase(
            PriceRepository priceRepository,
            PriceSelectionService priceSelectionService) {
        return new FindApplicablePriceService(priceRepository, priceSelectionService);
    }
}
