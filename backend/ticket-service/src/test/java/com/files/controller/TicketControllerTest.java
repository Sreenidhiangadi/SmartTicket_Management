package com.files.controller;

import com.files.config.TestSecurityConfig;
import com.files.dashboard.DashboardService;
import com.files.dto.*;
import com.files.history.TicketHistory;
import com.files.history.TicketHistoryService;
import com.files.model.*;
import com.files.service.TicketCommentService;
import com.files.service.TicketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static org.mockito.Mockito.*;

@WebFluxTest(TicketController.class)
@Import(TestSecurityConfig.class)
class TicketControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private TicketService ticketService;

    @MockBean
    private TicketHistoryService ticketHistoryService;

    @MockBean
    private TicketCommentService commentService;

    @MockBean
    private DashboardService dashboardService;

    private TicketResponse ticket() {
        return new TicketResponse(
                "1", "title", "desc",
                TicketCategory.SOFTWARE,
                TicketPriority.MEDIUM,
                TicketStatus.CREATED,
                "user1", null,
                Instant.now(), Instant.now(),
                null, null, null, false
        );
    }

    @Test
    void createTicket() {
        when(ticketService.createTicket(any()))
                .thenReturn(Mono.just(ticket()));

        webTestClient
                .mutateWith(
                        SecurityMockServerConfigurers.mockJwt()
                                .authorities(() -> "SCOPE_user")
                )
                .post()
                .uri("/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CreateTicketRequest(
                        "t", "d",
                        TicketCategory.SOFTWARE,
                        TicketPriority.MEDIUM
                ))
                .exchange()
                .expectStatus().isCreated();
    }


    @Test
    void getTicketById() {
        when(ticketService.getTicketById("1"))
                .thenReturn(Mono.just(ticket()));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt())
                .get()
                .uri("/tickets/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getTicketsByUser() {
        when(ticketService.getTicketsByUser("u"))
                .thenReturn(Flux.just(ticket()));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt())
                .get()
                .uri("/tickets/user/u")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getTicketsByAgent() {
        when(ticketService.getTicketsByAgent("a"))
                .thenReturn(Flux.just(ticket()));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt())
                .get()
                .uri("/tickets/agent/a")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getTicketsByStatus() {
        when(ticketService.getTicketsByStatus(TicketStatus.CREATED))
                .thenReturn(Flux.just(ticket()));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt())
                .get()
                .uri("/tickets/status/CREATED")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void assignTicket() {
        when(ticketService.assignTicket("1", "a"))
                .thenReturn(Mono.just(ticket()));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(() -> "SCOPE_agent"))
                .put()
                .uri("/tickets/1/assign?agentId=a")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void updateStatus() {
        when(ticketService.updateStatus("1", TicketStatus.IN_PROGRESS))
                .thenReturn(Mono.just(ticket()));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(() -> "SCOPE_agent"))
                .put()
                .uri("/tickets/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateTicketStatusRequest(TicketStatus.IN_PROGRESS))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void closeTicket() {
        when(ticketService.closeTicket("1"))
                .thenReturn(Mono.just(ticket()));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(() -> "SCOPE_admin"))
                .put()
                .uri("/tickets/1/close")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void reopenTicket() {
        when(ticketService.reopenTicket("1"))
                .thenReturn(Mono.just(ticket()));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(() -> "SCOPE_admin"))
                .put()
                .uri("/tickets/1/reopen")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void cancelTicket() {
        when(ticketService.cancelTicket("1"))
                .thenReturn(Mono.just(ticket()));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(() -> "SCOPE_user"))
                .put()
                .uri("/tickets/1/cancel")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void history() {
        when(ticketHistoryService.getHistory("1"))
                .thenReturn(Flux.just(new TicketHistory()));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt())
                .get()
                .uri("/tickets/1/history")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void comments() {
        when(commentService.getComments("1"))
                .thenReturn(Flux.just(
                        new TicketCommentResponse(
                                "c", "msg", "u", "USER", Instant.now()
                        )
                ));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt())
                .get()
                .uri("/tickets/1/comments")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void timeline() {
        when(ticketService.getTimeline("1"))
                .thenReturn(Flux.just(
                        new TimelineItemResponse(
                                "HISTORY", "CREATED", "u", "msg", Instant.now()
                        )
                ));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt())
                .get()
                .uri("/tickets/1/timeline")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void autoAssign() {
        when(ticketService.autoAssignTicket("1"))
                .thenReturn(Mono.just(new Ticket()));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(() -> "SCOPE_manager"))
                .post()
                .uri("/tickets/1/auto-assign")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void markSlaBreached() {
        when(ticketService.markSlaBreached("1"))
                .thenReturn(Mono.empty());

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt())
                .put()
                .uri("/tickets/1/sla-breached")
                .exchange()
                .expectStatus().isOk();
    }
}
