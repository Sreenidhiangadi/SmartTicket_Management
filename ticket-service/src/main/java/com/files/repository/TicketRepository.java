package com.files.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.files.model.Ticket;
import com.files.model.TicketStatus;

import reactor.core.publisher.Flux;

public interface TicketRepository extends ReactiveMongoRepository<Ticket, String> {

    Flux<Ticket> findByCreatedBy(String createdBy);
    Flux<Ticket> findByStatus(TicketStatus status);
}
