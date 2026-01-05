package com.files.service.impl;

import com.files.dto.AddCommentRequest;
import com.files.dto.TicketCommentResponse;
import com.files.model.TicketComment;
import com.files.repository.TicketCommentRepository;
import com.files.service.TicketCommentService;
import com.files.exception.TicketNotFoundException;
import com.files.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketCommentServiceImpl implements TicketCommentService {

    private final TicketCommentRepository commentRepository;
    private final TicketRepository ticketRepository;

    @Override
    public Mono<TicketCommentResponse> addComment(
            String ticketId,
            AddCommentRequest request
    ) {
        return ReactiveSecurityContextHolder.getContext()
            .flatMap(ctx -> {
                String userId = ctx.getAuthentication().getName();
                Set<String> roles = ctx.getAuthentication().getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .map(r -> r.replace("ROLE_", ""))
                        .collect(Collectors.toSet());

                return ticketRepository.findById(ticketId)
                    .switchIfEmpty(Mono.error(new TicketNotFoundException(ticketId)))
                    .flatMap(ticket -> {

                        if (roles.contains("USER") &&
                            !roles.contains("ADMIN") &&
                            !userId.equals(ticket.getCreatedBy())) {
                            return Mono.error(
                                new IllegalStateException(
                                    "User can comment only on own ticket"
                                )
                            );
                        }

                        TicketComment comment = TicketComment.builder()
                                .ticketId(ticketId)
                                .comment(request.comment())
                                .commentedBy(userId)
                                .role(roles.iterator().next())
                                .commentedAt(Instant.now())
                                .build();

                        return commentRepository.save(comment);
                    })
                    .map(this::toResponse);
            });
    }

    @Override
    public Flux<TicketCommentResponse> getComments(String ticketId) {
        return commentRepository
                .findByTicketIdOrderByCommentedAtAsc(ticketId)
                .map(this::toResponse);
    }

    private TicketCommentResponse toResponse(TicketComment c) {
        return new TicketCommentResponse(
                c.getId(),
                c.getComment(),
                c.getCommentedBy(),
                c.getRole(),
                c.getCommentedAt()
        );
    }
}
