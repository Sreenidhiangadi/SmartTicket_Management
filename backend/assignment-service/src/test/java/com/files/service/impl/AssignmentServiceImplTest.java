package com.files.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.reactive.function.client.WebClient;

import com.files.dto.AgentDto;
import com.files.exception.AssignmentAlreadyExistsException;
import com.files.model.Assignment;
import com.files.repository.AssignmentRepository;
import com.files.repository.EscalationLogRepository;
import com.files.service.AgentClientService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceImplTest {

	@Mock
	AssignmentRepository assignmentRepository;

	@Mock
	EscalationLogRepository escalationLogRepository;

	@Mock
	AgentClientService agentClientService;

	@Mock
	WebClient ticketWebClient;

	@InjectMocks
	AssignmentServiceImpl service;

	Assignment assignment;

	@BeforeEach
	void setup() {
		assignment = Assignment.builder().ticketId("t1").agentId("a1").priority("MEDIUM")
				.slaDueAt(Instant.now().minusSeconds(60)).escalated(false).build();
	}

	@Test
	void assignTicket_success() {
		when(assignmentRepository.findByTicketId("t1")).thenReturn(Mono.empty());
		when(assignmentRepository.save(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));

		StepVerifier.create(service.assignTicket("t1", "a1", "HIGH")).expectNextCount(1).verifyComplete();
	}

	@Test
	void assignTicket_alreadyExists() {
		when(assignmentRepository.findByTicketId("t1")).thenReturn(Mono.just(assignment));

		StepVerifier.create(service.assignTicket("t1", "a1", "HIGH"))
				.expectError(AssignmentAlreadyExistsException.class).verify();
	}

	@Test
	void autoAssign_success() {
		when(assignmentRepository.findByTicketId("t1")).thenReturn(Mono.empty());

		AgentDto a1 = new AgentDto();
		a1.setId("a1");
		a1.setActive(true);

		AgentDto a2 = new AgentDto();
		a2.setId("a2");
		a2.setActive(true);

		when(agentClientService.fetchActiveAgents()).thenReturn(Flux.just(a1, a2));

		when(assignmentRepository.findAll()).thenReturn(Flux.empty());

		when(assignmentRepository.save(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));

		StepVerifier.create(service.autoAssign("t1", null)).expectNextMatches(resp -> resp.getTicketId().equals("t1"))
				.verifyComplete();
	}

	@Test
	void autoAssign_alreadyExists() {
		when(assignmentRepository.findByTicketId("t1")).thenReturn(Mono.just(assignment));

		StepVerifier.create(service.autoAssign("t1", "LOW")).expectError(AssignmentAlreadyExistsException.class)
				.verify();
	}

	@Test
	void escalate_success() {
		Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").subject("admin").build();

		JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt, Set.of());

		assignment.setEscalated(false);

		when(assignmentRepository.save(any())).thenReturn(Mono.just(assignment));

		when(escalationLogRepository.save(any())).thenReturn(Mono.empty());

		WebClient.RequestBodyUriSpec bodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
		WebClient.RequestBodySpec bodySpec = mock(WebClient.RequestBodySpec.class);
		WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
		doReturn(bodyUriSpec).when(ticketWebClient).put();

		doReturn(bodySpec).when(bodyUriSpec).uri(anyString(), any(Object[].class));

		doReturn(bodySpec).when(bodySpec).header(anyString(), anyString());

		doReturn(responseSpec).when(bodySpec).retrieve();

		doReturn(Mono.empty()).when(responseSpec).bodyToMono(Void.class);

		StepVerifier
				.create(service.escalate(assignment, "SLA")
						.contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
				.assertNext(a -> org.junit.jupiter.api.Assertions.assertTrue(a.isEscalated())).verifyComplete();
	}

}
