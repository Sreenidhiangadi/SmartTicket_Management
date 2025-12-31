package com.files.history;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TicketHistoryService {

    Mono<Void> record(
            String ticketId,
            TicketHistoryAction action,
            String performedBy,
            String description
    );

    Flux<TicketHistory> getHistory(String ticketId);
}
