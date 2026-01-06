package com.files.dashboard;

import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.files.model.TicketPriority;
import com.files.model.TicketStatus;
import com.files.repository.TicketRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

	private final TicketRepository ticketRepository;

	@Override
	public Mono<DashboardSummaryResponse> getSummary() {

		Mono<Long> total = ticketRepository.count();
		Mono<Long> closed = ticketRepository.countByStatus(TicketStatus.CLOSED);
		Mono<Long> unassigned = ticketRepository.countByAssignedToIsNull();

		Mono<Long> slaBreached = ticketRepository
				.findBySlaDueAtBeforeAndStatusNotIn(Instant.now(), List.of(TicketStatus.RESOLVED, TicketStatus.CLOSED))
				.count();

		Mono<Map<TicketStatus, Long>> byStatus = Mono.zip(ticketRepository.countByStatus(TicketStatus.CREATED),
				ticketRepository.countByStatus(TicketStatus.ASSIGNED),
				ticketRepository.countByStatus(TicketStatus.IN_PROGRESS),
				ticketRepository.countByStatus(TicketStatus.RESOLVED),
				ticketRepository.countByStatus(TicketStatus.CLOSED),
				ticketRepository.countByStatus(TicketStatus.CANCELLED)).map(t -> {
					Map<TicketStatus, Long> map = new EnumMap<>(TicketStatus.class);
					map.put(TicketStatus.CREATED, t.getT1());
					map.put(TicketStatus.ASSIGNED, t.getT2());
					map.put(TicketStatus.IN_PROGRESS, t.getT3());
					map.put(TicketStatus.RESOLVED, t.getT4());
					map.put(TicketStatus.CLOSED, t.getT5());
					map.put(TicketStatus.CANCELLED, t.getT6());
					return map;
				});

		Mono<Map<TicketPriority, Long>> byPriority = Mono.zip(ticketRepository.countByPriority(TicketPriority.LOW),
				ticketRepository.countByPriority(TicketPriority.MEDIUM),
				ticketRepository.countByPriority(TicketPriority.HIGH),
				ticketRepository.countByPriority(TicketPriority.CRITICAL)).map(t -> {
					Map<TicketPriority, Long> map = new EnumMap<>(TicketPriority.class);
					map.put(TicketPriority.LOW, t.getT1());
					map.put(TicketPriority.MEDIUM, t.getT2());
					map.put(TicketPriority.HIGH, t.getT3());
					map.put(TicketPriority.CRITICAL, t.getT4());
					return map;
				});

		return Mono.zip(total, closed, unassigned, slaBreached, byStatus, byPriority)
				.map(t -> new DashboardSummaryResponse(t.getT1(), t.getT1() - t.getT2(), t.getT2(), t.getT3(),
						t.getT4(), t.getT5(), t.getT6()));
	}

}
