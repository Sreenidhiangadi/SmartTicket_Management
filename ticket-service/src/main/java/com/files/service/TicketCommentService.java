package com.files.service;

import com.files.dto.AddCommentRequest;
import com.files.dto.TicketCommentResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TicketCommentService {

    Mono<TicketCommentResponse> addComment(
            String ticketId,
            AddCommentRequest request
    );

    Flux<TicketCommentResponse> getComments(String ticketId);
}
