package com.files.reports;

import com.files.dto.*;
import com.files.model.Ticket;
import com.files.model.TicketPriority;
import com.files.model.TicketStatus;
import com.files.repository.TicketRepository;
import com.files.reports.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final TicketRepository ticketRepository;

    @Override
    public Flux<TicketsByStatusReport> ticketsByStatus() {
        return Flux.fromArray(TicketStatus.values())
                .flatMap(status ->
                        ticketRepository.countByStatus(status)
                                .map(count -> new TicketsByStatusReport(status, count))
                );
    }
    @Override
    public Mono<ReportSummaryDto> summary() {
        return Mono.zip(
                ticketRepository.count().defaultIfEmpty(0L),
                ticketRepository.countByStatus(TicketStatus.RESOLVED).defaultIfEmpty(0L),
                ticketRepository.countByStatus(TicketStatus.IN_PROGRESS).defaultIfEmpty(0L)
        ).map(tuple -> new ReportSummaryDto(
                tuple.getT1(),
                tuple.getT2(),
                tuple.getT3()
        ));
    }
    @Override
    public Flux<TicketsByPriorityReport> ticketsByPriority() {
        return Flux.fromArray(TicketPriority.values())
                .flatMap(priority ->
                        ticketRepository.countByPriority(priority)
                                .map(count -> new TicketsByPriorityReport(priority, count))
                );
    }

    @Override
    public Mono<AvgResolutionTimeReport> avgResolutionTime() {
        return ticketRepository.findByStatus(TicketStatus.RESOLVED)
                .filter(t -> t.getResolvedAt() != null)
                .map(this::resolutionMinutes)
                .collectList()
                .map(times -> {
                    if (times.isEmpty()) return 0.0;
                    return times.stream().mapToLong(Long::longValue).average().orElse(0);
                })
                .map(AvgResolutionTimeReport::new);
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


    private long resolutionMinutes(Ticket ticket) {
        return Duration.between(ticket.getCreatedAt(), ticket.getResolvedAt())
                .toMinutes();
    }
}
