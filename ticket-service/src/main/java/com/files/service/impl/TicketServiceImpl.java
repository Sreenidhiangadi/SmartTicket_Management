package com.files.service.impl;

import com.files.dto.AutoAssignmentResponse;
import com.files.dto.CreateTicketRequest;
import com.files.dto.SlaBreachReport;
import com.files.dto.TicketResponse;
import com.files.dto.TimelineItemResponse;
import com.files.exception.TicketNotFoundException;
import com.files.history.TicketHistory;
import com.files.history.TicketHistoryAction;
import com.files.history.TicketHistoryService;
import com.files.messaging.TicketAssignedEvent;
import com.files.messaging.TicketCreatedEvent;
import com.files.messaging.TicketStatusChangedEvent;
import com.files.model.Ticket;
import com.files.model.TicketPriority;
import com.files.model.TicketStatus;
import com.files.repository.TicketRepository;
import com.files.service.TicketCommentService;
import com.files.service.TicketService;
import com.files.sla.SlaPolicy;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.security.oauth2.jwt.Jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketHistoryService ticketHistoryService;
    private final TicketCommentService commentService;
    private final WebClient assignmentWebClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

//    @Override
//    public Mono<TicketResponse> createTicket(CreateTicketRequest request) {
//
//        return ReactiveSecurityContextHolder.getContext()
//            .map(ctx -> ctx.getAuthentication().getName())
//            .flatMap(userId -> {
//
//                Instant now = Instant.now();
//                Instant slaDueAt = now.plus(
//                    SlaPolicy.getDuration(request.priority())
//                );
//
//                Ticket ticket = Ticket.builder()
//                    .title(request.title())
//                    .description(request.description())
//                    .category(request.category())
//                    .priority(request.priority())
//                    .status(TicketStatus.CREATED)
//                    .createdBy(userId)
//                    .createdAt(now)
//                    .updatedAt(now)
//                    .slaDueAt(slaDueAt)
//                    .slaBreached(false)
//                    .build();
//
//                return ticketRepository.save(ticket)
//                    .flatMap(saved ->
//                        ticketHistoryService.record(
//                            saved.getId(),
//                            TicketHistoryAction.CREATED,
//                            userId,
//                            "Ticket created (SLA due at " + slaDueAt + ")"
//                        ).thenReturn(saved)
//                    )
//                 
//                    .flatMap(saved ->
//                        autoAssignTicket(saved.getId())
//                            .onErrorResume(ex -> {
//                                log.error(
//                                    "Auto-assign failed for ticket {}",
//                                    saved.getId(),
//                                    ex
//                                );
//                                return Mono.just(saved);
//                            })
//                    );
//            })
//            .map(this::toResponse);
//    }

    @Override
    public Mono<TicketResponse> createTicket(CreateTicketRequest request) {

        return ReactiveSecurityContextHolder.getContext()
            .map(ctx -> (JwtAuthenticationToken) ctx.getAuthentication())
            .flatMap(jwtAuth -> {
            	Jwt jwt = jwtAuth.getToken();
                String userId = jwtAuth.getName();
                String email = jwt.getClaimAsString("email");
                String bearerToken = "Bearer " + jwtAuth.getToken().getTokenValue();

                Instant now = Instant.now();
                Instant slaDueAt = now.plus(
                    SlaPolicy.getDuration(request.priority())
                );

                Ticket ticket = Ticket.builder()
                    .title(request.title())
                    .description(request.description())
                    .category(request.category())
                    .priority(request.priority())
                    .status(TicketStatus.CREATED)
                    .createdBy(userId)
                    .createdByEmail(email) 
                    .createdAt(now)
                    .updatedAt(now)
                    .slaDueAt(slaDueAt)
                    .slaBreached(false)
                    .build();

                return ticketRepository.save(ticket)
                		.doOnSuccess(saved -> {
                	        TicketCreatedEvent event = new TicketCreatedEvent();
                	        event.setTicketId(saved.getId());
                	        event.setCreatedByUserId(saved.getCreatedBy());
                	        event.setCreatedByEmail(email); 
                	        event.setTitle(saved.getTitle());

                	        kafkaTemplate.send(
                	            "ticket-created-events",
                	            saved.getId(),
                	            event
                	        );
                	    })
                    .flatMap(saved ->
                        ticketHistoryService.record(
                            saved.getId(),
                            TicketHistoryAction.CREATED,
                            userId,
                            "Ticket created (SLA due at " + slaDueAt + ")"
                        ).thenReturn(saved)
                    )
                  
                    .flatMap(saved ->
                        assignmentWebClient
                            .post()
                            .uri("/api/assign/auto/{ticketId}", saved.getId())
                            .header("Authorization", bearerToken)
                            .retrieve()
                            .bodyToMono(AutoAssignmentResponse.class)
                            .flatMap(resp -> {
                                saved.setAssignedTo(resp.getAgentId());
                                saved.setStatus(TicketStatus.ASSIGNED);
                                saved.setUpdatedAt(Instant.now());

                                return ticketRepository.save(saved)
                                		 .doOnSuccess(updated -> {

                                	            TicketAssignedEvent event = new TicketAssignedEvent();
                                	            event.setTicketId(updated.getId());
                                	            event.setAgentId(updated.getAssignedTo());
                                	            event.setAgentEmail(resp.getAgentEmail()); 
                                	            event.setAssignedAt(Instant.now());

                                	            kafkaTemplate.send(
                                	                "ticket-assigned-events",
                                	                updated.getId(),
                                	                event
                                	            );
                                	        })
                                    .flatMap(updated ->
                                        ticketHistoryService.record(
                                            updated.getId(),
                                            TicketHistoryAction.ASSIGNED,
                                            resp.getAgentId(),
                                            "Ticket auto-assigned to agent " + resp.getAgentId()
                                        ).thenReturn(updated)
                                    );
                            })
                         
                            .onErrorResume(ex -> {
                                log.error("Auto-assign failed for ticket {}", saved.getId(), ex);
                                return Mono.just(saved);
                            })
                    );
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


//    @Override
//    public Mono<TicketResponse> updateStatus(String ticketId, TicketStatus newStatus) {
//        return withAuthContextMono((userId, roles) ->
//                ticketRepository.findById(ticketId)
//                        .switchIfEmpty(Mono.error(new TicketNotFoundException(ticketId)))
//                        .flatMap(ticket -> {
//
//                            if (ticket.getStatus() == TicketStatus.CLOSED ||
//                                ticket.getStatus() == TicketStatus.CANCELLED) {
//                                return Mono.error(new IllegalStateException(
//                                        "Closed or Cancelled tickets cannot be updated"
//                                ));
//                            }
//
//                            if (roles.contains("AGENT") &&
//                                !roles.contains("MANAGER") &&
//                                !roles.contains("ADMIN") &&
//                                !userId.equals(ticket.getAssignedTo())) {
//                                return Mono.error(new IllegalStateException(
//                                        "Agent can update only assigned tickets"
//                                ));
//                            }
//
//                            if (!isValidTransition(ticket.getStatus(), newStatus)) {
//                                return Mono.error(new IllegalStateException(
//                                        "Invalid status transition: " +
//                                                ticket.getStatus() + " → " + newStatus
//                                ));
//                            }
//
//                            TicketStatus oldStatus = ticket.getStatus();
//
//                            ticket.setStatus(newStatus);
//                            ticket.setUpdatedAt(Instant.now());
//
//                            if (newStatus == TicketStatus.RESOLVED) {
//                                ticket.setResolvedAt(Instant.now());
//                            }
//                            if (newStatus == TicketStatus.CLOSED) {
//                                ticket.setClosedAt(Instant.now());
//                            }
//
//                            return ticketRepository.save(ticket)
//                            		 .doOnSuccess(saved -> {
//                            		        TicketStatusChangedEvent event =
//                            		                new TicketStatusChangedEvent();
//
//                            		        event.setTicketId(saved.getId());
//                            		        event.setUserId(userId);
//                            		        event.setUserEmail(email); 
//                            		        event.setOldStatus(oldStatus.name());
//                            		        event.setNewStatus(saved.getStatus().name());
//
//                            		        kafkaTemplate.send(
//                            		            "ticket-status-events",
//                            		            saved.getId(),
//                            		            event
//                            		        );
//                            		    })
//                                    .flatMap(saved ->
//                                            ticketHistoryService.record(
//                                                    saved.getId(),
//                                                    TicketHistoryAction.STATUS_CHANGED,
//                                                    userId,
//                                                    oldStatus + " → " + newStatus
//                                            ).thenReturn(saved)
//                                    );
//                        })
//                        .map(this::toResponse)
//        );
//    }
    @Override
    public Mono<TicketResponse> updateStatus(String ticketId, TicketStatus newStatus) {

        return ReactiveSecurityContextHolder.getContext()
            .map(ctx -> (JwtAuthenticationToken) ctx.getAuthentication())
            .flatMap(jwtAuth -> {

                String userId = jwtAuth.getName();

                Set<String> roles = jwtAuth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .map(r -> r.replace("ROLE_", ""))
                        .collect(Collectors.toSet());

                return ticketRepository.findById(ticketId)
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
                            .doOnSuccess(saved -> {

                                TicketStatusChangedEvent event =
                                        new TicketStatusChangedEvent();

                                event.setTicketId(saved.getId());
                                event.setUserId(userId);
                                event.setUserEmail(saved.getCreatedByEmail()); 
                                event.setOldStatus(oldStatus.name());
                                event.setNewStatus(saved.getStatus().name());

                                kafkaTemplate.send(
                                    "ticket-status-events",
                                    saved.getId(),
                                    event
                                );
                            })
                            .flatMap(saved ->
                                ticketHistoryService.record(
                                    saved.getId(),
                                    TicketHistoryAction.STATUS_CHANGED,
                                    userId,
                                    oldStatus + " → " + newStatus
                                ).thenReturn(saved)
                            );
                    })
                    .map(this::toResponse);
            });
    }

    @Override
    public Mono<TicketResponse> closeTicket(String ticketId) {
        return updateStatus(ticketId, TicketStatus.CLOSED);
    }

  

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
    @Override
    public Flux<SlaBreachReport> slaBreaches() {

        return ticketRepository
            .findBySlaDueAtBeforeAndStatusNotIn(
                Instant.now(),
                List.of(TicketStatus.RESOLVED, TicketStatus.CLOSED)
            )
            .map(ticket ->
                new SlaBreachReport(
                    ticket.getId(),
                    ticket.getSlaDueAt(),
                    ticket.getStatus().name()
                )
            );
    }
//    @Override
//    public Mono<Ticket> autoAssignTicket(String ticketId) {
//
//        return ReactiveSecurityContextHolder.getContext()
//            .map(ctx -> (JwtAuthenticationToken) ctx.getAuthentication())
//            .flatMap(jwtAuth -> {
//
//                String bearerToken =
//                        "Bearer " + jwtAuth.getToken().getTokenValue();
//
//                return ticketRepository.findById(ticketId)
//                    .switchIfEmpty(
//                        Mono.error(new IllegalStateException("Ticket not found"))
//                    )
//                    .flatMap(ticket -> {
//
//                        if (ticket.getStatus() == TicketStatus.CLOSED ||
//                            ticket.getStatus() == TicketStatus.CANCELLED) {
//                            return Mono.error(
//                                new IllegalStateException("Ticket cannot be auto-assigned")
//                            );
//                        }
//                        log.info("Calling Assignment Service auto-assign for ticket {}", ticketId);
//                        return assignmentWebClient
//                            .post()
//                            .uri("/api/assign/auto/{ticketId}", ticketId)
//                            .header("Authorization", bearerToken)
//                            .retrieve()
//                            .bodyToMono(AutoAssignmentResponse.class)
//                            .flatMap(response -> {
//
//                                ticket.setAssignedTo(response.getAgentId());
//                                ticket.setStatus(TicketStatus.ASSIGNED);
//                                ticket.setUpdatedAt(Instant.now());
//
//                                return ticketRepository.save(ticket)
//                                    .flatMap(saved ->
//                                        ticketHistoryService.record(
//                                            saved.getId(),
//                                            TicketHistoryAction.ASSIGNED,
//                                            response.getAgentId(),
//                                            "Ticket auto-assigned to agent " + response.getAgentId()
//                                        ).thenReturn(saved)
//                                    );
//                            });
//                    });
//            });
//    }
//
    @Override
    public Mono<Ticket> autoAssignTicket(String ticketId) {

        return ReactiveSecurityContextHolder.getContext()
            .map(ctx -> (JwtAuthenticationToken) ctx.getAuthentication())
            .flatMap(jwtAuth -> {

                String bearerToken =
                        "Bearer " + jwtAuth.getToken().getTokenValue();

                return ticketRepository.findById(ticketId)
                    .switchIfEmpty(
                        Mono.error(new IllegalStateException("Ticket not found"))
                    )
                    .flatMap(ticket -> {

                        if (ticket.getStatus() != TicketStatus.CREATED) {
                            return Mono.error(
                                new IllegalStateException(
                                    "Only CREATED tickets can be auto-assigned"
                                )
                            );
                        }

                        if (ticket.getAssignedTo() != null) {
                            log.info(
                                "Ticket {} already assigned to {}, skipping auto-assign",
                                ticketId,
                                ticket.getAssignedTo()
                            );
                            return Mono.just(ticket);
                        }

                        log.info(
                            "Calling Assignment Service auto-assign for ticket {}",
                            ticketId
                        );

                        return assignmentWebClient
                            .post()
                            .uri("/api/assign/auto/{ticketId}", ticketId)
                            .header("Authorization", bearerToken)
                            .retrieve()
                            .onStatus(
                                status -> status.is4xxClientError() || status.is5xxServerError(),
                                response ->
                                    response.bodyToMono(String.class)
                                        .flatMap(body -> {
                                            log.error(
                                                "Auto-assign failed for ticket {}: {}",
                                                ticketId,
                                                body
                                            );
                                            return Mono.error(
                                                new IllegalStateException(
                                                    "Auto-assign service failed"
                                                )
                                            );
                                        })
                            )
                            .bodyToMono(AutoAssignmentResponse.class)
                            .flatMap(response -> {

                                ticket.setAssignedTo(response.getAgentId());
                                ticket.setStatus(TicketStatus.ASSIGNED);
                                ticket.setUpdatedAt(Instant.now());

                                return ticketRepository.save(ticket)
                                    .flatMap(saved ->
                                        ticketHistoryService.record(
                                            saved.getId(),
                                            TicketHistoryAction.ASSIGNED,
                                            response.getAgentId(),
                                            "Ticket auto-assigned to agent " +
                                                response.getAgentId()
                                        ).thenReturn(saved)
                                    );
                            });
                    });
            });
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
