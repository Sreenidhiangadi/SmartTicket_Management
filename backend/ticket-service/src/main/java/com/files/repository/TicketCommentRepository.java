package com.files.repository;

import com.files.model.TicketComment;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface TicketCommentRepository
        extends ReactiveMongoRepository<TicketComment, String> {

    Flux<TicketComment> findByTicketIdOrderByCommentedAtAsc(String ticketId);
}
