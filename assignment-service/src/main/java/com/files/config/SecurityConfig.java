package com.files.config;

import com.files.security.JwtAuthConverter;

import java.nio.charset.StandardCharsets;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
	 @Value("${security.jwt.secret}")
	    private String jwtSecret;


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex
                        .pathMatchers(HttpMethod.POST, "/api/assign/**")
                        .hasAnyRole("MANAGER", "ADMIN")

                        .pathMatchers("/api/escalations/**")
                        .hasAnyRole("MANAGER", "ADMIN")

                        .pathMatchers("/api/agents/workload")
                        .hasRole("ADMIN")

                        .pathMatchers("/api/sla/**")
                        .hasRole("ADMIN")

                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth ->
                        oauth.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(new JwtAuthConverter())
                        )
                )
                .build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        return NimbusReactiveJwtDecoder.withSecretKey(
                new SecretKeySpec(
                        jwtSecret.getBytes(StandardCharsets.UTF_8),
                        "HmacSHA256"
                )
        ).build();
    }
}
