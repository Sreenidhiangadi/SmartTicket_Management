package com.files.service.impl;

import com.files.dto.AddCommentRequest;
import com.files.dto.TicketCommentResponse;
import com.files.exception.TicketNotFoundException;
import com.files.model.Ticket;
import com.files.model.TicketComment;
import com.files.repository.TicketCommentRepository;
import com.files.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketCommentServiceImplTest {

    @Mock
    TicketCommentRepository commentRepository;

    @Mock
    TicketRepository ticketRepository;

    @InjectMocks
    TicketCommentServiceImpl service;

    AddCommentRequest request;

    @BeforeEach
    void setup() {
        request = new AddCommentRequest("test comment");
    }

    private Mono<TicketCommentResponse> withUser(
            String user,
            String role,
            Mono<TicketCommentResponse> mono
    ) {
        var auth = new UsernamePasswordAuthenticationToken(
                user,
                "pwd",
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );

        return mono.contextWrite(
                ReactiveSecurityContextHolder.withAuthentication(auth)
        );
    }

    // ---------- addComment success (USER owns ticket) ----------
    @Test
    void addComment_success_userOwnTicket() {
        Ticket ticket = new Ticket();
        ticket.setId("T1");
        ticket.setCreatedBy("user1");

        TicketComment saved = TicketComment.builder()
                .id("C1")
                .ticketId("T1")
                .comment("test comment")
                .commentedBy("user1")
                .role("USER")
                .commentedAt(Instant.now())
                .build();

        when(ticketRepository.findById("T1"))
                .thenReturn(Mono.just(ticket));

        when(commentRepository.save(any()))
                .thenReturn(Mono.just(saved));

        StepVerifier.create(
                withUser(
                        "user1",
                        "USER",
                        service.addComment("T1", request)
                )
        )
        .expectNextMatches(r ->
                r.comment().equals("test comment") &&
                r.commentedBy().equals("user1")
        )
        .verifyComplete();
    }

    // ---------- ticket not found ----------
    @Test
    void addComment_ticketNotFound() {
        when(ticketRepository.findById("T1"))
                .thenReturn(Mono.empty());

        StepVerifier.create(
                withUser(
                        "user1",
                        "USER",
                        service.addComment("T1", request)
                )
        )
        .expectError(TicketNotFoundException.class)
        .verify();
    }

    // ---------- USER not owner ----------
    @Test
    void addComment_userNotOwner_shouldFail() {
        Ticket ticket = new Ticket();
        ticket.setId("T1");
        ticket.setCreatedBy("otherUser");

        when(ticketRepository.findById("T1"))
                .thenReturn(Mono.just(ticket));

        StepVerifier.create(
                withUser(
                        "user1",
                        "USER",
                        service.addComment("T1", request)
                )
        )
        .expectError(IllegalStateException.class)
        .verify();
    }

    // ---------- ADMIN can comment ----------
    @Test
    void addComment_adminAllowed() {
        Ticket ticket = new Ticket();
        ticket.setId("T1");
        ticket.setCreatedBy("someone");

        TicketComment saved = TicketComment.builder()
                .id("C1")
                .ticketId("T1")
                .comment("admin comment")
                .commentedBy("admin")
                .role("ADMIN")
                .commentedAt(Instant.now())
                .build();

        when(ticketRepository.findById("T1"))
                .thenReturn(Mono.just(ticket));

        when(commentRepository.save(any()))
                .thenReturn(Mono.just(saved));

        StepVerifier.create(
                withUser(
                        "admin",
                        "ADMIN",
                        service.addComment("T1", request)
                )
        )
        .expectNextCount(1)
        .verifyComplete();
    }

    // ---------- getComments ----------
    @Test
    void getComments_success() {
        TicketComment c1 = TicketComment.builder()
                .id("1")
                .comment("c1")
                .commentedBy("u1")
                .role("USER")
                .commentedAt(Instant.now())
                .build();

        TicketComment c2 = TicketComment.builder()
                .id("2")
                .comment("c2")
                .commentedBy("u2")
                .role("ADMIN")
                .commentedAt(Instant.now())
                .build();

        when(commentRepository.findByTicketIdOrderByCommentedAtAsc("T1"))
                .thenReturn(Flux.just(c1, c2));

        StepVerifier.create(service.getComments("T1"))
                .expectNextCount(2)
                .verifyComplete();
    }
}
