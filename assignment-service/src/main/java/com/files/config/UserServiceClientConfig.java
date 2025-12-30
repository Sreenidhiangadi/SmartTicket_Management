package com.files.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class UserServiceClientConfig {

    @Bean
    public WebClient userServiceClient(WebClient.Builder builder) {
        return builder
                .baseUrl("lb://user-service")
                .build();
    }
}

