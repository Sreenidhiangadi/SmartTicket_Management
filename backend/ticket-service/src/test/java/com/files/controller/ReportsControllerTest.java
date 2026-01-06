package com.files.controller;

import com.files.dto.SlaBreachReport;
import com.files.model.TicketPriority;
import com.files.model.TicketStatus;
import com.files.reports.AvgResolutionTimeReport;
import com.files.reports.ReportService;
import com.files.reports.ReportSummaryDto;
import com.files.reports.ReportsController;
import com.files.reports.TicketsByPriorityReport;
import com.files.reports.TicketsByStatusReport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@WebFluxTest(ReportsController.class)
@WithMockUser
class ReportsControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ReportService reportService;

    @Test
    void ticketsByStatus() {
        when(reportService.ticketsByStatus())
                .thenReturn(Flux.just(
                        new TicketsByStatusReport(TicketStatus.CREATED, 5)
                ));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt())
                .get()
                .uri("/reports/tickets-by-status")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void ticketsByPriority() {
        when(reportService.ticketsByPriority())
                .thenReturn(Flux.just(
                        new TicketsByPriorityReport(TicketPriority.MEDIUM, 3)
                ));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt())
                .get()
                .uri("/reports/tickets-by-priority")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void avgResolutionTime() {
        when(reportService.avgResolutionTime())
                .thenReturn(Mono.just(new AvgResolutionTimeReport(30)));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt())
                .get()
                .uri("/reports/avg-resolution-time")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void slaBreaches() {
        when(reportService.slaBreaches())
                .thenReturn(Flux.just(
                        new SlaBreachReport("1", null, "OPEN")
                ));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt())
                .get()
                .uri("/reports/sla-breaches")
                .exchange()
                .expectStatus().isOk();
    }
    @Test
    void summary_endpoint() {
        when(reportService.summary())
                .thenReturn(Mono.just(new ReportSummaryDto(10, 4, 3)));

        webTestClient.get()
                .uri("/reports/summary")
                .exchange()
                .expectStatus().isOk();
    }
}
