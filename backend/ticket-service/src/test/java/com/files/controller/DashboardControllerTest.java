package com.files.controller;

import com.files.dashboard.DashboardController;
import com.files.dashboard.DashboardService;
import com.files.dashboard.DashboardSummaryResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@WebFluxTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private DashboardService dashboardService;

    @Test
    void getSummary() {
        when(dashboardService.getSummary())
                .thenReturn(Mono.just(
                        new DashboardSummaryResponse(
                                10L, 5L, 5L, 2L, 1L, null, null
                        )
                ));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt())
                .get()
                .uri("/dashboard/summary")
                .exchange()
                .expectStatus().isOk();
    }
}
