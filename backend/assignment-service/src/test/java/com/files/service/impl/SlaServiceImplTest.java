package com.files.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.files.model.Assignment;
import com.files.repository.AssignmentRepository;
import com.files.service.AssignmentService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class SlaServiceImplTest {

	@Mock
	AssignmentRepository assignmentRepository;

	@Mock
	AssignmentService assignmentService;

	@InjectMocks
	SlaServiceImpl service;

	@Test
	void checkSla_dryRun() {
		Assignment a = Assignment.builder().ticketId("t1").slaDueAt(Instant.now().minusSeconds(60)).escalated(false)
				.build();

		when(assignmentRepository.findByEscalated(false)).thenReturn(Flux.just(a));

		StepVerifier.create(service.checkSla(true)).assertNext(result -> {
			assert result.isBreached();
			assert !result.isEscalated();
		}).verifyComplete();
	}

	@Test
	void checkSla_realEscalation() {
		Assignment a = Assignment.builder().ticketId("t1").slaDueAt(Instant.now().minusSeconds(60)).escalated(false)
				.build();

		when(assignmentRepository.findByEscalated(false)).thenReturn(Flux.just(a));

		when(assignmentService.escalate(any(), any())).thenReturn(Mono.just(a));

		StepVerifier.create(service.checkSla(false)).assertNext(result -> {
			assert result.isBreached();
			assert result.isEscalated();
		}).verifyComplete();
	}

	@Test
	void calculateWorkload() {
		when(assignmentRepository.findAll()).thenReturn(
				Flux.just(Assignment.builder().agentId("a1").build(), Assignment.builder().agentId("a1").build()));

		StepVerifier.create(service.calculateWorkload()).assertNext(w -> assertEquals(2, w.getActiveTickets()))
				.verifyComplete();
	}
}
