package com.files.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.reactive.CorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthConverter jwtAuthConverter;

    private final CorsConfigurationSource corsConfigurationSource;
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
        		.cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(ex -> ex
            		 .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            		.pathMatchers("/user-service/users/agents").permitAll()
            	    .pathMatchers("/user-service/auth/**").permitAll()
            	    .pathMatchers("/user-service/internal/auth/**").permitAll()
            	    .pathMatchers("/actuator/**").permitAll()

            	    .pathMatchers("/user-service/users/admin/**").hasRole("ADMIN")
            	    .pathMatchers("/user-service/users/**").authenticated()
            	    .pathMatchers("/user-service/internal/**").permitAll()

            	    .pathMatchers(HttpMethod.POST, "/ticket-service/tickets")
            	    .hasRole("USER")

            	.pathMatchers("/ticket-service/tickets/user/**")
            	    .hasRole("USER")

            	.pathMatchers(HttpMethod.GET, "/ticket-service/tickets/*/history")
            	    .hasAnyRole("USER", "AGENT", "MANAGER", "ADMIN")

            	.pathMatchers(HttpMethod.POST, "/ticket-service/tickets/*/comments")
            	    .hasAnyRole("USER", "AGENT", "MANAGER", "ADMIN")

            	.pathMatchers(HttpMethod.GET, "/ticket-service/tickets/*/comments")
            	    .authenticated()

            	.pathMatchers("/ticket-service/tickets/agent/**")
            	    .hasAnyRole("AGENT", "MANAGER")

            	.pathMatchers("/ticket-service/tickets/*/status")
            	    .hasRole("AGENT")

            	.pathMatchers("/ticket-service/tickets/*/close")
            	    .hasRole("AGENT")

            	.pathMatchers("/ticket-service/tickets/*/assign")
            	    .hasRole("MANAGER")

            	.pathMatchers("/ticket-service/tickets/*/reopen")
            	    .hasAnyRole("MANAGER", "ADMIN")

            	.pathMatchers("/ticket-service/tickets/*/cancel")
            	    .hasAnyRole("USER", "ADMIN")

                .pathMatchers(HttpMethod.POST, "/assignment-service/api/assign/auto/**")
                .permitAll()

            .pathMatchers(HttpMethod.POST, "/assignment-service/api/assign/**")
                .hasAnyRole("MANAGER", "ADMIN")

            .pathMatchers("/assignment-service/api/escalations/**")
                .hasAnyRole("MANAGER", "ADMIN")

            .pathMatchers("/assignment-service/api/agents/workload")
                .hasRole("ADMIN")

            .pathMatchers("/assignment-service/api/sla/**")
                .hasRole("ADMIN")
            .pathMatchers("/notification-service/notifications/**")
                .authenticated()

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
