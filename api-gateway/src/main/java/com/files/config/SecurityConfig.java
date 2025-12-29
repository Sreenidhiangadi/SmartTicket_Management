package com.files.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthConverter jwtAuthConverter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(ex -> ex
                .pathMatchers("/user-service/auth/**").permitAll()
                .pathMatchers("/user-service/internal/auth/**").permitAll()
                .pathMatchers("/user-service/users/admin/**").hasRole("ADMIN")
                .pathMatchers("/user-service/users/**").authenticated()
                .pathMatchers("/tickets/**")
                    .hasAnyRole("USER","AGENT","MANAGER","ADMIN")
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth ->
            oauth.jwt(jwt ->
                jwt.jwtAuthenticationConverter(jwtAuthConverter)
            )
        )
        .build();
//            .oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::jwt)
            
    }
}
