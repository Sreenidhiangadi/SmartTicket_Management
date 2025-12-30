package com.files.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Flux;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchange -> exchange
                    .pathMatchers("/actuator/**").permitAll()
                    
                    .pathMatchers(HttpMethod.GET, "/tickets/*/history")
                    .hasAnyRole("USER", "AGENT", "MANAGER", "ADMIN")
                    
                    
                    .pathMatchers(HttpMethod.POST, "/tickets").hasRole("USER")
                    .pathMatchers("/tickets/user/**").hasRole("USER")
                    
                    .pathMatchers(HttpMethod.POST, "/tickets/*/comments")
                    .hasAnyRole("USER", "AGENT", "MANAGER", "ADMIN")

                .pathMatchers(HttpMethod.GET, "/tickets/*/comments")
                    .authenticated()

                   
                    .pathMatchers("/tickets/agent/**").hasAnyRole("AGENT", "MANAGER")
                    .pathMatchers("/tickets/*/status").hasRole("AGENT")
                    .pathMatchers("/tickets/*/close").hasRole("AGENT")

                   
                    .pathMatchers("/tickets/*/assign").hasRole("MANAGER")
                    .pathMatchers("/tickets/*/reopen").hasAnyRole("MANAGER", "ADMIN")

                    
                    .pathMatchers("/tickets/*/cancel").hasAnyRole("USER", "ADMIN")
                    .pathMatchers(HttpMethod.GET, "/tickets/*/timeline").authenticated()

                    .pathMatchers("/reports/**", "/dashboard/**")
                        .hasAnyRole("MANAGER", "ADMIN")

                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            )
            .build();
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

    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        ReactiveJwtAuthenticationConverter converter =
                new ReactiveJwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<String> roles = jwt.getClaimAsStringList("roles");
            if (roles == null || roles.isEmpty()) {
                return Flux.empty();
            }

            return Flux.fromIterable(
                roles.stream()
                     .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                     .toList()
            );
        });

        return converter;
    }
}
