package com.files.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.files.model.Ticket;
import com.files.model.TicketPriority;
import com.files.model.TicketStatus;
import com.files.reports.ReportServiceImpl;
import com.files.repository.TicketRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

	@Mock
	private TicketRepository ticketRepository;

	@InjectMocks
	private ReportServiceImpl service;

	@Test
	void ticketsByStatus_success() {
		for (TicketStatus status : TicketStatus.values()) {
			when(ticketRepository.countByStatus(status)).thenReturn(Mono.just(5L));
		}

		StepVerifier.create(service.ticketsByStatus()).expectNextCount(TicketStatus.values().length).verifyComplete();
	}

	@Test
	void ticketsByPriority_success() {
		for (TicketPriority priority : TicketPriority.values()) {
			when(ticketRepository.countByPriority(priority)).thenReturn(Mono.just(3L));
		}

		StepVerifier.create(service.ticketsByPriority()).expectNextCount(TicketPriority.values().length)
				.verifyComplete();
	}

	@Test
	void avgResolutionTime_success() {
		Ticket t1 = Ticket.builder().createdAt(Instant.now().minusSeconds(3600)).resolvedAt(Instant.now()).build();

		when(ticketRepository.findByStatus(TicketStatus.RESOLVED)).thenReturn(Flux.just(t1));

		StepVerifier.create(service.avgResolutionTime()).expectNextCount(1).verifyComplete();
	}

	@Test
	void avgResolutionTime_empty() {
		when(ticketRepository.findByStatus(TicketStatus.RESOLVED)).thenReturn(Flux.empty());

		StepVerifier.create(service.avgResolutionTime()).assertNext(report -> {
			assertNotNull(report);
			assertTrue(report.toString().contains("0"));
		}).verifyComplete();
	}

	@Test
	void slaBreaches_success() {
		Ticket ticket = Ticket.builder().id("1").slaDueAt(Instant.now().minusSeconds(60))
				.status(TicketStatus.IN_PROGRESS).build();

		when(ticketRepository.findBySlaDueAtBeforeAndStatusNotIn(any(Instant.class),
				eq(List.of(TicketStatus.RESOLVED, TicketStatus.CLOSED)))).thenReturn(Flux.just(ticket));

		StepVerifier.create(service.slaBreaches()).expectNextCount(1).verifyComplete();
	}

	@Test
	void summary_success() {

		when(ticketRepository.count()).thenReturn(Mono.just(10L));
		when(ticketRepository.countByStatus(TicketStatus.RESOLVED)).thenReturn(Mono.just(4L));
		when(ticketRepository.countByStatus(TicketStatus.IN_PROGRESS)).thenReturn(Mono.just(3L));

		StepVerifier.create(service.summary()).assertNext(dto -> {
			assertEquals(10L, dto.getTotalTickets());
			assertEquals(4L, dto.getResolved());
			assertEquals(3L, dto.getInProgress());
		}).verifyComplete();
	}

	@Test
	void avgResolutionTime_ticketWithNullResolvedAt_isIgnored() {

		Ticket t1 = Ticket.builder().createdAt(Instant.now().minusSeconds(3600)).resolvedAt(null).build();

		when(ticketRepository.findByStatus(TicketStatus.RESOLVED)).thenReturn(Flux.just(t1));

		StepVerifier.create(service.avgResolutionTime())
				.assertNext(report -> assertEquals(0.0, report.averageResolutionMinutes())).verifyComplete();
	}

}
