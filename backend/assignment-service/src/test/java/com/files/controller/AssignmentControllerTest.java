package com.files.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.files.config.TestSecurityConfig;
import com.files.dto.request.AssignTicketRequest;
import com.files.dto.response.AutoAssignmentResponse;
import com.files.model.Assignment;
import com.files.model.EscalationLog;
import com.files.repository.EscalationLogRepository;
import com.files.service.AssignmentService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(AssignmentController.class)
@Import(TestSecurityConfig.class)
class AssignmentControllerTest {

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private AssignmentService assignmentService;

	@MockBean
	private EscalationLogRepository escalationLogRepository;

	@Test
	void assignTicket() {
		Assignment assignment = Assignment.builder().ticketId("t1").agentId("a1").priority("HIGH").build();

		when(assignmentService.assignTicket(any(), any(), any())).thenReturn(Mono.just(assignment));

		AssignTicketRequest req = new AssignTicketRequest();
		req.setAgentId("a1");
		req.setPriority("HIGH");

		webTestClient.post().uri("/api/assign/t1").bodyValue(req).exchange().expectStatus().isOk();
	}

	@Test
	void autoAssign() {
		AutoAssignmentResponse resp = AutoAssignmentResponse.builder().ticketId("t1").agentId("a1").priority("MEDIUM")
				.slaDueAt(Instant.now()).build();

		when(assignmentService.autoAssign("t1", null)).thenReturn(Mono.just(resp));

		webTestClient.post().uri("/api/assign/auto/t1").exchange().expectStatus().isOk();
	}

	@Test
	void escalationLogs() {
		when(escalationLogRepository.findAll())
				.thenReturn(Flux.just(EscalationLog.builder().ticketId("t1").agentId("a1").build()));

		webTestClient.mutateWith(SecurityMockServerConfigurers.mockJwt()).get().uri("/api/assign/escalations")
				.exchange().expectStatus().isOk();
	}

	@Test
	void escalationsByManager() {
		when(escalationLogRepository.findByEscalatedToManagerId("m1"))
				.thenReturn(Flux.just(EscalationLog.builder().ticketId("t1").escalatedToManagerId("m1").build()));

		webTestClient.mutateWith(SecurityMockServerConfigurers.mockJwt()).get()
				.uri("/api/assign/escalations/manager/m1").exchange().expectStatus().isOk();
	}
}
