package com.files.history;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface TicketHistoryRepository
        extends ReactiveMongoRepository<TicketHistory, String> {

    Flux<TicketHistory> findByTicketIdOrderByCreatedAtAsc(String ticketId);
}
