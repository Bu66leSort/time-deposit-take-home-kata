package org.ikigaidigital.config;

import org.ikigaidigital.TimeDepositCalculator;
import org.ikigaidigital.domain.interest.BasicInterestStrategy;
import org.ikigaidigital.domain.interest.PremiumInterestStrategy;
import org.ikigaidigital.domain.interest.StudentInterestStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.List;

@Configuration
public class AppConfig {
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public TimeDepositCalculator timeDepositCalculator() {
        // A new plan is registered here; the calculator itself does not change.
        return new TimeDepositCalculator(List.of(
                new BasicInterestStrategy(), new StudentInterestStrategy(), new PremiumInterestStrategy()));
    }
}
