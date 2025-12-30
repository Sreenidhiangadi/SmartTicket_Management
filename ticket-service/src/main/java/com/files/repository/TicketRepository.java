package com.files.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.files.model.Ticket;
import com.files.model.TicketPriority;
import com.files.model.TicketStatus;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TicketRepository extends ReactiveMongoRepository<Ticket, String> {

    Flux<Ticket> findByCreatedBy(String createdBy);
    
    Flux<Ticket> findByStatus(TicketStatus status);
    
    Mono<Long> countByStatus(TicketStatus status);

    Mono<Long> countByPriority(TicketPriority priority);

    Mono<Long> countByAssignedToIsNull();
    
    Flux<Ticket> findByStatusIn(Iterable<TicketStatus> statuses);
}
