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
        long SLA_MINUTES = 48 * 60;

        return ticketRepository.findByStatusIn(
                        List.of(TicketStatus.RESOLVED, TicketStatus.CLOSED)
                )
                .filter(t -> t.getResolvedAt() != null)
                .map(t -> {
                    long minutes = resolutionMinutes(t);
                    return new Object[]{t, minutes};
                })
                .filter(arr -> (long) arr[1] > SLA_MINUTES)
                .map(arr -> {
                    Ticket t = (Ticket) arr[0];
                    long minutes = (long) arr[1];
                    return new SlaBreachReport(t.getId(), minutes);
                });
    }

    private long resolutionMinutes(Ticket ticket) {
        return Duration.between(ticket.getCreatedAt(), ticket.getResolvedAt())
                .toMinutes();
    }
}
