package com.files.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                .pathMatchers("/actuator/**").permitAll()

                
                .pathMatchers(HttpMethod.POST, "/tickets/**").hasRole("USER")
                .pathMatchers("/tickets/user/**").hasRole("USER")

                
                .pathMatchers(HttpMethod.GET, "/tickets/*/history")
                .hasAnyRole("USER", "AGENT", "MANAGER", "ADMIN")
                
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

                
                .pathMatchers("/reports/**", "/dashboard/**")
                    .hasAnyRole("MANAGER", "ADMIN")
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
