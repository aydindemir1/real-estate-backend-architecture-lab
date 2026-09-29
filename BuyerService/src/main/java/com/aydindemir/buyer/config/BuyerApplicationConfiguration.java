package com.aydindemir.buyer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class BuyerApplicationConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
