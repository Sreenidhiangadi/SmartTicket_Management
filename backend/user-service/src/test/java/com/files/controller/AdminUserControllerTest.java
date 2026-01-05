package com.files.controller;

import com.files.dto.AdminUserUpdateRequest;
import com.files.dto.UserResponse;
import com.files.model.Role;
import com.files.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@WebFluxTest(AdminUserController.class)
@Import(AdminUserControllerTest.TestSecurityConfig.class)
class AdminUserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    // ✅ TEST-ONLY SECURITY OVERRIDE
    @TestConfiguration
    static class TestSecurityConfig {

        @Bean
        SecurityWebFilterChain testSecurityFilterChain(ServerHttpSecurity http) {
            return http
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authorizeExchange(ex -> ex.anyExchange().permitAll())
                    .build();
        }
    }

    @Test
    void getAllUsers_success() {
        Mockito.when(userService.getAllUsers())
                .thenReturn(Flux.just(Mockito.mock(UserResponse.class)));

        webTestClient.get()
                .uri("/users/admin")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserResponse.class)
                .hasSize(1);
    }

    @Test
    void getAllUsers_empty() {
        Mockito.when(userService.getAllUsers())
                .thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/users/admin")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserResponse.class)
                .hasSize(0);
    }

    @Test
    void updateUser_roles() {
        AdminUserUpdateRequest request = new AdminUserUpdateRequest();
        request.setRoles(Set.of(Role.ADMIN));

        Mockito.when(userService.updateRoles(Mockito.eq("1"), Mockito.any()))
                .thenReturn(Mono.just(Mockito.mock(UserResponse.class)));

        webTestClient.patch()
                .uri("/users/admin/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void updateUser_activeStatus() {
        AdminUserUpdateRequest request = new AdminUserUpdateRequest();
        request.setActive(true);

        Mockito.when(userService.updateStatus("1", true))
                .thenReturn(Mono.just(Mockito.mock(UserResponse.class)));

        webTestClient.patch()
                .uri("/users/admin/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void updateUser_noFieldsProvided() {
        AdminUserUpdateRequest request = new AdminUserUpdateRequest();

        webTestClient.patch()
                .uri("/users/admin/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getUserById_success() {
        Mockito.when(userService.getById("1"))
                .thenReturn(Mono.just(Mockito.mock(UserResponse.class)));

        webTestClient.get()
                .uri("/users/internal/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getActiveAgents_success() {
        Mockito.when(userService.getActiveAgents())
                .thenReturn(Flux.just(Mockito.mock(UserResponse.class)));

        webTestClient.get()
                .uri("/users/agents")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserResponse.class)
                .hasSize(1);
    }
}
