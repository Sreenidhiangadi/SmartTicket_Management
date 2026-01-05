package com.files.controller;

import com.files.dto.request.SlaCheckRequest;
import com.files.dto.response.SlaCheckResult;
import com.files.model.AgentWorkload;
import com.files.service.SlaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.time.Instant;

import static org.mockito.Mockito.when;

@WebFluxTest(
    controllers = SlaController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        ReactiveSecurityAutoConfiguration.class
    }
)
class SlaControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private SlaService slaService;

    @Test
    void checkSla() {
        when(slaService.checkSla(true))
                .thenReturn(Flux.just(
                        new SlaCheckResult(
                                "t1",
                                true,
                                Instant.now(),
                                false
                        )
                ));

        SlaCheckRequest req = new SlaCheckRequest();
        req.setDryRun(true);

        webTestClient
                .post()
                .uri("/api/sla/check")
                .bodyValue(req)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void workload() {
        when(slaService.calculateWorkload())
                .thenReturn(Flux.just(
                        new AgentWorkload("a1", 3)
                ));

        webTestClient
                .get()
                .uri("/api/sla/workload")
                .exchange()
                .expectStatus().isOk();
    }
}
