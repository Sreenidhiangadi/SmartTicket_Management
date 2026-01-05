package com.files.controller;

import com.files.dto.*;
import com.files.model.User;
import com.files.repository.UserRepository;
import com.files.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(AuthController.class)
@Import(AuthControllerTest.TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserRepository userRepository;

    @TestConfiguration
    static class TestSecurityConfig {

        @Bean
        SecurityWebFilterChain testSecurityFilterChain(ServerHttpSecurity http) {
            return http
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authorizeExchange(exchange -> exchange
                            .anyExchange().permitAll()
                    )
                    .build();
        }
    }
    private RegisterRequest validRegisterRequest() {
        RegisterRequest req = new RegisterRequest();
        req.setName("Test User");
        req.setEmail("test@test.com");
        req.setPassword("password123");
        return req;
    }

    private LoginRequest validLoginRequest() {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@test.com");
        req.setPassword("password123");
        return req;
    }

    @Test
    void register_success() {
        Mockito.when(authService.register(Mockito.any()))
                .thenReturn(Mono.just(Mockito.mock(UserResponse.class)));

        webTestClient.post()
                .uri("/auth/register")
                .bodyValue(validRegisterRequest())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").exists();
    }

    @Test
    void register_illegalArgument() {
        Mockito.when(authService.register(Mockito.any()))
                .thenReturn(Mono.error(new IllegalArgumentException("Invalid")));

        webTestClient.post()
                .uri("/auth/register")
                .bodyValue(validRegisterRequest())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid");
    }

    @Test
    void registerAdmin_success() {
        Mockito.when(authService.registerAdmin(Mockito.any()))
                .thenReturn(Mono.just(Mockito.mock(UserResponse.class)));

        webTestClient.post()
                .uri("/auth/register-admin")
                .bodyValue(validRegisterRequest())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void login_success() {
        Mockito.when(authService.login(Mockito.any()))
                .thenReturn(Mono.just(new AuthResponse("jwt-token")));

        webTestClient.post()
                .uri("/auth/login")
                .bodyValue(validLoginRequest())
                .exchange()
                .expectStatus().isOk();
    }


    @Test
    void me_success() {
        User user = User.builder()
                .id("user-id")
                .email("test@test.com")
                .active(true)
                .build();

        Mockito.when(userRepository.findById("user-id"))
                .thenReturn(Mono.just(user));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .jwt(jwt -> jwt.subject("user-id")))
                .get()
                .uri("/auth/me")
                .exchange()
                .expectStatus().isOk();
    }
    @Test
    void me_userNotFound() {
        Mockito.when(userRepository.findById("missing"))
                .thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/auth/me")
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
