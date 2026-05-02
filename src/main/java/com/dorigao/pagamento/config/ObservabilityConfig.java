package com.dorigao.pagamento.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObservabilityConfig {

    @Bean
    public HealthIndicator customHealthIndicator() {
        return () -> Health.up()
                .withDetail("service", "pagamento")
                .withDetail("version", "1.0.0")
                .build();
    }

    @Bean
    public MeterRegistryCustomizer meterRegistryCustomizer(MeterRegistry meterRegistry) {
        return registry -> registry.config()
                .commonTags(
                    "application", "pagamento",
                    "service.name", "pagamento",
                    "service.namespace", "app"
                );
    }
}
