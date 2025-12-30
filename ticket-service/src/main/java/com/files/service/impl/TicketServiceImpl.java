package com.files.service.impl;

import com.files.dto.CreateTicketRequest;
import com.files.dto.TicketResponse;
import com.files.dto.TimelineItemResponse;
import com.files.exception.TicketNotFoundException;
import com.files.history.TicketHistory;
import com.files.history.TicketHistoryAction;
import com.files.history.TicketHistoryService;
import com.files.model.Ticket;
import com.files.model.TicketPriority;
import com.files.model.TicketStatus;
import com.files.repository.TicketRepository;
import com.files.service.TicketCommentService;
import com.files.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketHistoryService ticketHistoryService;
    private final TicketCommentService commentService;

    // ================= CREATE =================

    @Override
    public Mono<TicketResponse> createTicket(CreateTicketRequest request) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMap(userId -> {

                    Ticket ticket = Ticket.builder()
                            .title(request.title())
                            .description(request.description())
                            .category(request.category())
                            .priority(request.priority())
                            .status(TicketStatus.CREATED)
                            .createdBy(userId)
                            .createdAt(Instant.now())
                            .updatedAt(Instant.now())
                            .build();

                    return ticketRepository.save(ticket)
                            .flatMap(saved ->
                                    ticketHistoryService.record(
                                            saved.getId(),
                                            TicketHistoryAction.CREATED,
                                            userId,
                                            "Ticket created"
                                    ).thenReturn(saved)
                            );
                })
                .map(this::toResponse);
    }

    // ================= READ =================

    @Override
    public Mono<TicketResponse> getTicketById(String id) {
        return ticketRepository.findById(id)
                .switchIfEmpty(Mono.error(new TicketNotFoundException(id)))
                .map(this::toResponse);
    }

    @Override
    public Flux<TicketResponse> getTicketsByUser(String userId) {
        return ticketRepository.findByCreatedBy(userId)
                .map(this::toResponse);
    }

    @Override
    public Flux<TicketResponse> getTicketsByStatus(TicketStatus status) {
        return ticketRepository.findByStatus(status)
                .map(this::toResponse);
    }

    // ================= ASSIGN =================

    @Override
    public Mono<TicketResponse> assignTicket(String ticketId, String agentId) {
        return ticketRepository.findById(ticketId)
                .switchIfEmpty(Mono.error(new TicketNotFoundException(ticketId)))
                .flatMap(ticket -> {

                    if (ticket.getStatus() != TicketStatus.CREATED) {
                        return Mono.error(new IllegalStateException(
                                "Only CREATED tickets can be assigned"
                        ));
                    }

                    ticket.setAssignedTo(agentId);
                    ticket.setStatus(TicketStatus.ASSIGNED);
                    ticket.setUpdatedAt(Instant.now());

                    return ticketRepository.save(ticket)
                            .flatMap(saved ->
                                    ticketHistoryService.record(
                                            saved.getId(),
                                            TicketHistoryAction.ASSIGNED,
                                            agentId,
                                            "Ticket assigned to agent " + agentId
                                    ).thenReturn(saved)
                            );
                })
                .map(this::toResponse);
    }

    // ================= STATUS =================

    @Override
    public Mono<TicketResponse> updateStatus(String ticketId, TicketStatus newStatus) {
        return withAuthContextMono((userId, roles) ->
                ticketRepository.findById(ticketId)
                        .switchIfEmpty(Mono.error(new TicketNotFoundException(ticketId)))
                        .flatMap(ticket -> {

                            if (ticket.getStatus() == TicketStatus.CLOSED ||
                                ticket.getStatus() == TicketStatus.CANCELLED) {
                                return Mono.error(new IllegalStateException(
                                        "Closed or Cancelled tickets cannot be updated"
                                ));
                            }

                            if (roles.contains("AGENT") &&
                                !roles.contains("MANAGER") &&
                                !roles.contains("ADMIN") &&
                                !userId.equals(ticket.getAssignedTo())) {
                                return Mono.error(new IllegalStateException(
                                        "Agent can update only assigned tickets"
                                ));
                            }

                            if (!isValidTransition(ticket.getStatus(), newStatus)) {
                                return Mono.error(new IllegalStateException(
                                        "Invalid status transition: " +
                                                ticket.getStatus() + " → " + newStatus
                                ));
                            }

                            TicketStatus oldStatus = ticket.getStatus();

                            ticket.setStatus(newStatus);
                            ticket.setUpdatedAt(Instant.now());

                            if (newStatus == TicketStatus.RESOLVED) {
                                ticket.setResolvedAt(Instant.now());
                            }
                            if (newStatus == TicketStatus.CLOSED) {
                                ticket.setClosedAt(Instant.now());
                            }

                            return ticketRepository.save(ticket)
                                    .flatMap(saved ->
                                            ticketHistoryService.record(
                                                    saved.getId(),
                                                    TicketHistoryAction.STATUS_CHANGED,
                                                    userId,
                                                    oldStatus + " → " + newStatus
                                            ).thenReturn(saved)
                                    );
                        })
                        .map(this::toResponse)
        );
    }

    @Override
    public Mono<TicketResponse> closeTicket(String ticketId) {
        return updateStatus(ticketId, TicketStatus.CLOSED);
    }

    // ================= REOPEN =================

    @Override
    public Mono<TicketResponse> reopenTicket(String ticketId) {
        return withAuthContextMono((userId, roles) ->
                ticketRepository.findById(ticketId)
                        .switchIfEmpty(Mono.error(new TicketNotFoundException(ticketId)))
                        .flatMap(ticket -> {

                            if (ticket.getStatus() != TicketStatus.RESOLVED &&
                                ticket.getStatus() != TicketStatus.CLOSED) {
                                return Mono.error(new IllegalStateException(
                                        "Only RESOLVED or CLOSED tickets can be reopened"
                                ));
                            }

                            ticket.setStatus(TicketStatus.IN_PROGRESS);
                            ticket.setUpdatedAt(Instant.now());

                            return ticketRepository.save(ticket)
                                    .flatMap(saved ->
                                            ticketHistoryService.record(
                                                    saved.getId(),
                                                    TicketHistoryAction.REOPENED,
                                                    userId,
                                                    "Ticket reopened"
                                            ).thenReturn(saved)
                                    );
                        })
                        .map(this::toResponse)
        );
    }

    // ================= CANCEL =================

    @Override
    public Mono<TicketResponse> cancelTicket(String ticketId) {
        return withAuthContextMono((userId, roles) ->
                ticketRepository.findById(ticketId)
                        .switchIfEmpty(Mono.error(new TicketNotFoundException(ticketId)))
                        .flatMap(ticket -> {

                            if (ticket.getStatus() == TicketStatus.RESOLVED ||
                                ticket.getStatus() == TicketStatus.CLOSED) {
                                return Mono.error(new IllegalStateException(
                                        "Resolved or Closed tickets cannot be cancelled"
                                ));
                            }

                            if (roles.contains("USER") &&
                                !roles.contains("ADMIN") &&
                                !userId.equals(ticket.getCreatedBy())) {
                                return Mono.error(new IllegalStateException(
                                        "User can cancel only their own ticket"
                                ));
                            }

                            ticket.setStatus(TicketStatus.CANCELLED);
                            ticket.setUpdatedAt(Instant.now());

                            return ticketRepository.save(ticket)
                                    .flatMap(saved ->
                                            ticketHistoryService.record(
                                                    saved.getId(),
                                                    TicketHistoryAction.CANCELLED,
                                                    userId,
                                                    "Ticket cancelled"
                                            ).thenReturn(saved)
                                    );
                        })
                        .map(this::toResponse)
        );
    }

    // ================= LIST (FILTER + PAGINATION) =================

    @Override
    public Flux<TicketResponse> getTickets(
            TicketStatus status,
            TicketPriority priority,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return withAuthContextFlux((userId, roles) -> {

            Flux<Ticket> flux;

            if (roles.contains("USER")) {
                flux = ticketRepository.findByCreatedBy(userId);
            } else if (roles.contains("AGENT")) {
                flux = ticketRepository.findByAssignedTo(userId);
            } else {
                flux = ticketRepository.findAll();
            }

            return flux
                    .filter(t -> status == null || t.getStatus() == status)
                    .filter(t -> priority == null || t.getPriority() == priority)
                    .skip((long) page * size)
                    .take(size)
                    .map(this::toResponse);
        });
    }
    @Override
    public Flux<TimelineItemResponse> getTimeline(String ticketId) {

        Flux<TimelineItemResponse> historyFlux =
                ticketHistoryService.getHistory(ticketId)
                        .map(this::fromHistory);

        Flux<TimelineItemResponse> commentFlux =
                commentService.getComments(ticketId)
                        .map(comment ->
                                new TimelineItemResponse(
                                        "COMMENT",
                                        "COMMENT",
                                        comment.commentedBy(),
                                        comment.comment(),
                                        comment.commentedAt()
                                )
                        );

        return Flux.merge(historyFlux, commentFlux)
                .sort(Comparator.comparing(TimelineItemResponse::timestamp));
    }
    // ================= HELPERS =================
    private TimelineItemResponse fromHistory(TicketHistory history) {
        return new TimelineItemResponse(
                "HISTORY",
                history.getAction().name(),
                history.getPerformedBy(),
                history.getDescription(),
                history.getCreatedAt()
        );
    }
    private boolean isValidTransition(TicketStatus from, TicketStatus to) {
        return switch (from) {
            case CREATED -> to == TicketStatus.ASSIGNED || to == TicketStatus.CANCELLED;
            case ASSIGNED -> to == TicketStatus.IN_PROGRESS || to == TicketStatus.CANCELLED;
            case IN_PROGRESS -> to == TicketStatus.RESOLVED;
            case RESOLVED -> to == TicketStatus.CLOSED;
            case CLOSED, CANCELLED -> false;
        };
    }

    private Mono<TicketResponse> withAuthContextMono(AuthMonoHandler handler) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .flatMap(auth -> {
                    String userId = auth.getName();
                    Set<String> roles = auth.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .map(r -> r.replace("ROLE_", ""))
                            .collect(Collectors.toSet());
                    return handler.handle(userId, roles);
                });
    }

    private Flux<TicketResponse> withAuthContextFlux(AuthFluxHandler handler) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .flatMapMany(auth -> {
                    String userId = auth.getName();
                    Set<String> roles = auth.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .map(r -> r.replace("ROLE_", ""))
                            .collect(Collectors.toSet());
                    return handler.handle(userId, roles);
                });
    }

    @FunctionalInterface
    interface AuthMonoHandler {
        Mono<TicketResponse> handle(String userId, Set<String> roles);
    }

    @FunctionalInterface
    interface AuthFluxHandler {
        Flux<TicketResponse> handle(String userId, Set<String> roles);
    }

    private TicketResponse toResponse(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getCategory(),
                ticket.getPriority(),
                ticket.getStatus(),
                ticket.getCreatedBy(),
                ticket.getAssignedTo(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt(),
                ticket.getResolvedAt(),
                ticket.getClosedAt()
        );
    }
}
