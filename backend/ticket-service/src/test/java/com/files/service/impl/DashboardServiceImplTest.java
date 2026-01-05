package com.files.service.impl;

import com.files.dashboard.DashboardServiceImpl;
import com.files.model.TicketPriority;
import com.files.model.TicketStatus;
import com.files.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private DashboardServiceImpl service;

    @Test
    void getSummary_success() {
        when(ticketRepository.count()).thenReturn(Mono.just(100L));
        when(ticketRepository.countByStatus(TicketStatus.CLOSED)).thenReturn(Mono.just(40L));
        when(ticketRepository.countByAssignedToIsNull()).thenReturn(Mono.just(10L));

        when(ticketRepository.findBySlaDueAtBeforeAndStatusNotIn(
                any(Instant.class),
                eq(List.of(TicketStatus.RESOLVED, TicketStatus.CLOSED))
        )).thenReturn(Flux.empty());

        when(ticketRepository.countByStatus(TicketStatus.CREATED)).thenReturn(Mono.just(10L));
        when(ticketRepository.countByStatus(TicketStatus.ASSIGNED)).thenReturn(Mono.just(20L));
        when(ticketRepository.countByStatus(TicketStatus.IN_PROGRESS)).thenReturn(Mono.just(15L));
        when(ticketRepository.countByStatus(TicketStatus.RESOLVED)).thenReturn(Mono.just(5L));
        when(ticketRepository.countByStatus(TicketStatus.CANCELLED)).thenReturn(Mono.just(10L));

        when(ticketRepository.countByPriority(TicketPriority.LOW)).thenReturn(Mono.just(25L));
        when(ticketRepository.countByPriority(TicketPriority.MEDIUM)).thenReturn(Mono.just(25L));
        when(ticketRepository.countByPriority(TicketPriority.HIGH)).thenReturn(Mono.just(30L));
        when(ticketRepository.countByPriority(TicketPriority.CRITICAL)).thenReturn(Mono.just(20L));

        StepVerifier.create(service.getSummary())
                .expectNextCount(1)
                .verifyComplete();
    }
}
