package com.files.repository;

import com.files.model.Assignment;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AssignmentRepository
        extends ReactiveMongoRepository<Assignment, String> {

    Mono<Assignment> findByTicketId(String ticketId);

    Flux<Assignment> findByAgentId(String agentId);

    Flux<Assignment> findByEscalated(boolean escalated);
}
