package com.files.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class UserServiceClientConfig {

    @Bean
    public WebClient userServiceWebClient() {
        return WebClient.builder()
            .baseUrl("lb://USER-SERVICE")
            .build();
    }
}
