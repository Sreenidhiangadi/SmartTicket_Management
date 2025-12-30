package com.files.history;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TicketHistoryServiceImpl implements TicketHistoryService {

    private final TicketHistoryRepository repository;

    @Override
    public Mono<Void> record(
            String ticketId,
            TicketHistoryAction action,
            String performedBy,
            String description
    ) {
        TicketHistory history = TicketHistory.builder()
                .ticketId(ticketId)
                .action(action)
                .performedBy(performedBy)
                .description(description)
                .createdAt(Instant.now())
                .build();

        return repository.save(history).then();
    }

    @Override
    public Flux<TicketHistory> getHistory(String ticketId) {
        return repository.findByTicketIdOrderByCreatedAtAsc(ticketId);
    }
}
