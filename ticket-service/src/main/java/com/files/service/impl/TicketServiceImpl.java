package com.files.service.impl;

import com.files.dto.CreateTicketRequest;
import com.files.dto.TicketResponse;
import com.files.exception.TicketNotFoundException;
import com.files.history.TicketHistoryService;
import com.files.model.Ticket;
import com.files.model.TicketStatus;
import com.files.repository.TicketRepository;
import com.files.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketHistoryService ticketHistoryService;


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

                return ticketRepository.save(ticket);
            })
            .map(this::toResponse);
    }

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

                    return ticketRepository.save(ticket);
                })
                .map(this::toResponse);
    }

   
    @Override
    public Mono<TicketResponse> updateStatus(String ticketId, TicketStatus newStatus) {
        return withAuthContext((userId, roles) ->
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
                        !roles.contains("ADMIN")) {

                        if (!userId.equals(ticket.getAssignedTo())) {
                            return Mono.error(new IllegalStateException(
                                    "Agent can update only assigned tickets"
                            ));
                        }
                    }

                    if (!isValidTransition(ticket.getStatus(), newStatus)) {
                        return Mono.error(new IllegalStateException(
                                "Invalid status transition: " +
                                ticket.getStatus() + " → " + newStatus
                        ));
                    }

                    ticket.setStatus(newStatus);
                    ticket.setUpdatedAt(Instant.now());

                    if (newStatus == TicketStatus.RESOLVED) {
                        ticket.setResolvedAt(Instant.now());
                    }

                    if (newStatus == TicketStatus.CLOSED) {
                        ticket.setClosedAt(Instant.now());
                    }

                    return ticketRepository.save(ticket);
                })
                .map(this::toResponse)
        );
    }

    @Override
    public Mono<TicketResponse> closeTicket(String ticketId) {
        return updateStatus(ticketId, TicketStatus.CLOSED);
    }


    @Override
    public Mono<TicketResponse> reopenTicket(String ticketId) {
        return ticketRepository.findById(ticketId)
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

                    return ticketRepository.save(ticket);
                })
                .map(this::toResponse);
    }

    @Override
    public Mono<TicketResponse> cancelTicket(String ticketId) {
        return withAuthContext((userId, roles) ->
            ticketRepository.findById(ticketId)
                .switchIfEmpty(Mono.error(new TicketNotFoundException(ticketId)))
                .flatMap(ticket -> {

                    if (ticket.getStatus() == TicketStatus.RESOLVED ||
                        ticket.getStatus() == TicketStatus.CLOSED) {
                        return Mono.error(new IllegalStateException(
                                "Resolved or Closed tickets cannot be cancelled"
                        ));
                    }

                    // USER ownership rule
                    if (roles.contains("USER") && !roles.contains("ADMIN")) {
                        if (!userId.equals(ticket.getCreatedBy())) {
                            return Mono.error(new IllegalStateException(
                                    "User can cancel only their own ticket"
                            ));
                        }
                    }

                    ticket.setStatus(TicketStatus.CANCELLED);
                    ticket.setUpdatedAt(Instant.now());

                    return ticketRepository.save(ticket);
                })
                .map(this::toResponse)
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

    private Mono<TicketResponse> withAuthContext(AuthHandler handler) {
        return ReactiveSecurityContextHolder.getContext()
            .map(ctx -> ctx.getAuthentication())
            .switchIfEmpty(Mono.error(new IllegalStateException("No security context")))
            .flatMap(auth -> {

                String userId = auth.getName();

                Set<String> roles =
                    auth.getAuthorities() == null
                        ? Set.of()
                        : auth.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .map(r -> r.replace("ROLE_", ""))
                            .collect(Collectors.toSet());

                return handler.handle(userId, roles);
            });
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

    @FunctionalInterface
    interface AuthHandler {
        Mono<TicketResponse> handle(String userId, Set<String> roles);
    }
}
