package com.files.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.reactive.function.client.WebClient;

import com.files.dto.AutoAssignmentResponse;
import com.files.dto.CreateTicketRequest;
import com.files.dto.TicketCommentResponse;
import com.files.exception.TicketNotFoundException;
import com.files.history.TicketHistory;
import com.files.history.TicketHistoryAction;
import com.files.history.TicketHistoryService;
import com.files.model.Ticket;
import com.files.model.TicketCategory;
import com.files.model.TicketPriority;
import com.files.model.TicketStatus;
import com.files.repository.TicketRepository;
import com.files.service.TicketCommentService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

	@Mock
	private TicketRepository ticketRepository;

	@Mock
	private TicketHistoryService ticketHistoryService;

	@Mock
	private TicketCommentService commentService;

	@Mock
	private WebClient assignmentWebClient;

	@Mock
	private KafkaTemplate<String, Object> kafkaTemplate;

	@InjectMocks
	private TicketServiceImpl service;

	private Ticket ticket;

	@BeforeEach
	void setup() {
		ticket = Ticket.builder().id("t1").title("Test").priority(TicketPriority.MEDIUM).status(TicketStatus.CREATED)
				.createdBy("user1").assignedTo("agent1").createdAt(Instant.now()).updatedAt(Instant.now()).build();
	}

	@Test
	void getTicketById_success() {
		when(ticketRepository.findById("t1")).thenReturn(Mono.just(ticket));
		StepVerifier.create(service.getTicketById("t1")).expectNextCount(1).verifyComplete();
	}

	@Test
	void getTicketById_notFound() {
		when(ticketRepository.findById("x")).thenReturn(Mono.empty());
		StepVerifier.create(service.getTicketById("x")).expectError(TicketNotFoundException.class).verify();
	}

	@Test
	void assignTicket_success() {
		ticket.setStatus(TicketStatus.CREATED);
		when(ticketRepository.findById("t1")).thenReturn(Mono.just(ticket));
		when(ticketRepository.save(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));
		when(ticketHistoryService.record(any(), any(), any(), any())).thenReturn(Mono.empty());
		StepVerifier.create(service.assignTicket("t1", "agent2")).expectNextCount(1).verifyComplete();
	}

	@Test
	void assignTicket_closedTicket() {
		ticket.setStatus(TicketStatus.CLOSED);
		when(ticketRepository.findById("t1")).thenReturn(Mono.just(ticket));
		StepVerifier.create(service.assignTicket("t1", "agent2")).expectError(IllegalStateException.class).verify();
	}

	@Test
	void updateStatus_success() {
		ticket.setStatus(TicketStatus.ASSIGNED);
		ticket.setAssignedTo("agent1");
		when(ticketRepository.findById("t1")).thenReturn(Mono.just(ticket));
		when(ticketRepository.save(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));
		when(ticketHistoryService.record(any(), any(), any(), any())).thenReturn(Mono.empty());

		Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").subject("agent1").issuedAt(Instant.now())
				.expiresAt(Instant.now().plusSeconds(3600)).claim("email", "test@test.com").build();
		JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt, Set.of(() -> "ROLE_AGENT"));

		StepVerifier
				.create(service.updateStatus("t1", TicketStatus.IN_PROGRESS)
						.contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
				.expectNextCount(1).verifyComplete();
	}

	@Test
	void getTickets_userBranch() {
		when(ticketRepository.findByCreatedBy("user1")).thenReturn(Flux.just(ticket));
		TestingAuthenticationToken auth = new TestingAuthenticationToken("user1", null, "ROLE_USER");
		StepVerifier
				.create(service.getTickets(null, null, 0, 10)
						.contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
				.expectNextCount(1).verifyComplete();
	}

	@Test
	void getTickets_agentBranch() {
		when(ticketRepository.findByAssignedTo("agent1")).thenReturn(Flux.just(ticket));
		TestingAuthenticationToken auth = new TestingAuthenticationToken("agent1", null, "ROLE_AGENT");
		StepVerifier
				.create(service.getTickets(null, null, 0, 10)
						.contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
				.expectNextCount(1).verifyComplete();
	}

	@Test
	void slaBreaches_success() {
		ticket.setSlaBreached(true);
		when(ticketRepository.findBySlaBreachedTrue()).thenReturn(Flux.just(ticket));
		StepVerifier.create(service.slaBreaches()).expectNextCount(1).verifyComplete();
	}

	@Test
	void getTimeline_success() {
		when(ticketHistoryService.getHistory("t1")).thenReturn(Flux.empty());
		when(commentService.getComments("t1")).thenReturn(Flux.empty());
		StepVerifier.create(service.getTimeline("t1")).verifyComplete();
	}

	@Test
	void createTicket_autoAssignFails_shouldStillCreateTicket() {
		when(ticketRepository.save(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));
		when(ticketHistoryService.record(any(), any(), any(), any())).thenReturn(Mono.empty());

		WebClient.RequestBodyUriSpec post = mock(WebClient.RequestBodyUriSpec.class);
		WebClient.RequestHeadersSpec<?> headers = mock(WebClient.RequestHeadersSpec.class);
		WebClient.ResponseSpec response = mock(WebClient.ResponseSpec.class);

		when(assignmentWebClient.post()).thenReturn(post);
		when(post.uri(any(java.util.function.Function.class))).thenReturn(post);
		when(post.header(any(), any())).thenReturn(post);
		when(post.retrieve()).thenReturn(response);
		when(response.bodyToMono(AutoAssignmentResponse.class))
				.thenReturn(Mono.error(new RuntimeException("assign fail")));

		Jwt jwt = Jwt.withTokenValue("t").header("alg", "none").subject("user1").claim("email", "u@test.com")
				.issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(3600)).build();
		JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt, Set.of(() -> "ROLE_USER"));

		StepVerifier
				.create(service
						.createTicket(new CreateTicketRequest("Test title", "Test description", TicketCategory.OTHER,
								TicketPriority.HIGH))
						.contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
				.expectNextCount(1).verifyComplete();
	}

	@Test
	void autoAssignTicket_alreadyAssigned_shouldReturnTicket() {
		ticket.setStatus(TicketStatus.CREATED);
		ticket.setAssignedTo("agent1");
		when(ticketRepository.findById("t1")).thenReturn(Mono.just(ticket));

		Jwt jwt = Jwt.withTokenValue("t").header("alg", "none").subject("user1").issuedAt(Instant.now())
				.expiresAt(Instant.now().plusSeconds(3600)).build();
		JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt, Set.of(() -> "ROLE_USER"));

		StepVerifier
				.create(service.autoAssignTicket("t1")
						.contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
				.expectNext(ticket).verifyComplete();
	}

	@Test
	void autoAssignTicket_notCreatedStatus_shouldFail() {
		ticket.setStatus(TicketStatus.IN_PROGRESS);
		when(ticketRepository.findById("t1")).thenReturn(Mono.just(ticket));

		Jwt jwt = Jwt.withTokenValue("t").header("alg", "none").subject("user1").issuedAt(Instant.now())
				.expiresAt(Instant.now().plusSeconds(3600)).build();
		JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt, Set.of(() -> "ROLE_USER"));

		StepVerifier
				.create(service.autoAssignTicket("t1")
						.contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
				.expectError(IllegalStateException.class).verify();
	}

	@Test
	void getTicketsByUser_success() {
		when(ticketRepository.findByCreatedBy("user1")).thenReturn(Flux.just(ticket));
		StepVerifier.create(service.getTicketsByUser("user1")).expectNextCount(1).verifyComplete();
	}

	@Test
	void getTicketsByAgent_success() {
		when(ticketRepository.findByAssignedTo("agent1")).thenReturn(Flux.just(ticket));
		StepVerifier.create(service.getTicketsByAgent("agent1")).expectNextCount(1).verifyComplete();
	}

	@Test
	void getTicketsByStatus_success() {
		when(ticketRepository.findByStatus(TicketStatus.CREATED)).thenReturn(Flux.just(ticket));
		StepVerifier.create(service.getTicketsByStatus(TicketStatus.CREATED)).expectNextCount(1).verifyComplete();
	}

	@Test
	void getTimeline_withHistoryItem() {
		TicketHistory history = new TicketHistory();
		history.setAction(TicketHistoryAction.CREATED);
		history.setPerformedBy("user1");
		history.setDescription("created");
		history.setCreatedAt(Instant.now());

		when(ticketHistoryService.getHistory("t1")).thenReturn(Flux.just(history));
		when(commentService.getComments("t1")).thenReturn(Flux.empty());
		StepVerifier.create(service.getTimeline("t1")).expectNextCount(1).verifyComplete();
	}

	@Test
	void updateStatus_resolvedAfterSla_marksBreached() {
		ticket.setStatus(TicketStatus.IN_PROGRESS);
		ticket.setAssignedTo("agent1");
		ticket.setSlaDueAt(Instant.now().minusSeconds(60));
		when(ticketRepository.findById("t1")).thenReturn(Mono.just(ticket));
		when(ticketRepository.save(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));
		when(ticketHistoryService.record(any(), any(), any(), any())).thenReturn(Mono.empty());

		Jwt jwt = Jwt.withTokenValue("t").header("alg", "none").subject("agent1").claim("email", "a@b.com")
				.issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(3600)).build();
		JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt, Set.of(() -> "ROLE_AGENT"));

		StepVerifier
				.create(service.updateStatus("t1", TicketStatus.RESOLVED)
						.contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
				.assertNext(r -> assertTrue(r.slaBreached())).verifyComplete();
	}

	@Test
	void createTicket_autoAssignSuccess() {
		when(ticketRepository.save(any())).thenAnswer(i -> {
			Ticket t = i.getArgument(0);
			t.setId("t1");
			return Mono.just(t);
		});
		when(ticketHistoryService.record(any(), any(), any(), any())).thenReturn(Mono.empty());

		AutoAssignmentResponse resp = new AutoAssignmentResponse();
		resp.setAgentId("agent1");
		resp.setAgentEmail("agent@test.com");

		WebClient.RequestBodyUriSpec post = mock(WebClient.RequestBodyUriSpec.class);
		WebClient.RequestHeadersSpec<?> headers = mock(WebClient.RequestHeadersSpec.class);
		WebClient.ResponseSpec response = mock(WebClient.ResponseSpec.class);

		when(assignmentWebClient.post()).thenReturn(post);
		when(post.uri(any(java.util.function.Function.class))).thenReturn(post);
		when(post.header(any(), any())).thenReturn(post);
		when(post.retrieve()).thenReturn(response);
		when(response.bodyToMono(AutoAssignmentResponse.class)).thenReturn(Mono.just(resp));

		Jwt jwt = Jwt.withTokenValue("t").header("alg", "none").subject("user1").claim("email", "u@test.com")
				.issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(3600)).build();
		JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt, Set.of(() -> "ROLE_USER"));

		StepVerifier
				.create(service
						.createTicket(
								new CreateTicketRequest("title", "desc", TicketCategory.OTHER, TicketPriority.HIGH))
						.contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
				.expectNextCount(1).verifyComplete();
	}

	private boolean invokeTransition(TicketStatus from, TicketStatus to) {
		try {
			var m = TicketServiceImpl.class.getDeclaredMethod("isValidTransition", TicketStatus.class,
					TicketStatus.class);
			m.setAccessible(true);
			return (boolean) m.invoke(service, from, to);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	void updateStatus_invalidTransition() {
		ticket.setStatus(TicketStatus.CREATED);
		when(ticketRepository.findById("t1")).thenReturn(Mono.just(ticket));

		Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").subject("user1").issuedAt(Instant.now())
				.expiresAt(Instant.now().plusSeconds(3600)).claim("email", "test@test.com").build();
		JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt, Set.of(() -> "ROLE_USER"));

		StepVerifier
				.create(service.updateStatus("t1", TicketStatus.RESOLVED)
						.contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
				.expectError(IllegalStateException.class).verify();
	}

	@Test
	void cancelTicket_success() {
		ticket.setStatus(TicketStatus.CREATED);
		ticket.setCreatedBy("user1");
		when(ticketRepository.findById("t1")).thenReturn(Mono.just(ticket));
		when(ticketRepository.save(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));
		when(ticketHistoryService.record(any(), any(), any(), any())).thenReturn(Mono.empty());

		TestingAuthenticationToken auth = new TestingAuthenticationToken("user1", null, "ROLE_USER");

		StepVerifier
				.create(service.cancelTicket("t1").contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
				.expectNextCount(1).verifyComplete();
	}

	@Test
	void reopenTicket_success() {
		ticket.setStatus(TicketStatus.RESOLVED);
		when(ticketRepository.findById("t1")).thenReturn(Mono.just(ticket));
		when(ticketRepository.save(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));
		when(ticketHistoryService.record(any(), any(), any(), any())).thenReturn(Mono.empty());

		TestingAuthenticationToken auth = new TestingAuthenticationToken("user1", null, "ROLE_USER");

		StepVerifier
				.create(service.reopenTicket("t1").contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
				.expectNextCount(1).verifyComplete();
	}

	@Test
	void markSlaBreached_success() {
		when(ticketRepository.findById("t1")).thenReturn(Mono.just(ticket));
		when(ticketRepository.save(any())).thenReturn(Mono.just(ticket));
		StepVerifier.create(service.markSlaBreached("t1")).verifyComplete();
	}

	@Test
	void assignTicket_cancelledTicket_shouldFail() {
		ticket.setStatus(TicketStatus.CANCELLED);
		when(ticketRepository.findById("t1")).thenReturn(Mono.just(ticket));
		StepVerifier.create(service.assignTicket("t1", "agent2")).expectError(IllegalStateException.class).verify();
	}

	@Test
	void updateStatus_agentUpdatingOtherTicket_shouldFail() {
		ticket.setStatus(TicketStatus.ASSIGNED);
		ticket.setAssignedTo("agent2");
		when(ticketRepository.findById("t1")).thenReturn(Mono.just(ticket));

		Jwt jwt = Jwt.withTokenValue("t").header("alg", "none").subject("agent1").issuedAt(Instant.now())
				.expiresAt(Instant.now().plusSeconds(3600)).build();
		JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt, Set.of(() -> "ROLE_AGENT"));

		StepVerifier
				.create(service.updateStatus("t1", TicketStatus.IN_PROGRESS)
						.contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
				.expectError(IllegalStateException.class).verify();
	}

	@Test
	void updateStatus_closed_setsClosedAt() {
		ticket.setStatus(TicketStatus.RESOLVED);
		ticket.setAssignedTo("agent1");
		when(ticketRepository.findById("t1")).thenReturn(Mono.just(ticket));
		when(ticketRepository.save(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));
		when(ticketHistoryService.record(any(), any(), any(), any())).thenReturn(Mono.empty());

		Jwt jwt = Jwt.withTokenValue("t").header("alg", "none").subject("agent1").issuedAt(Instant.now())
				.expiresAt(Instant.now().plusSeconds(3600)).build();
		JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt, Set.of(() -> "ROLE_AGENT"));

		StepVerifier
				.create(service.updateStatus("t1", TicketStatus.CLOSED)
						.contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
				.assertNext(r -> assertNotNull(r.closedAt())).verifyComplete();
	}

	@Test
	void reopenTicket_invalidStatus_shouldFail() {
		ticket.setStatus(TicketStatus.CREATED);
		when(ticketRepository.findById("t1")).thenReturn(Mono.just(ticket));
		TestingAuthenticationToken auth = new TestingAuthenticationToken("user1", null, "ROLE_USER");
		StepVerifier
				.create(service.reopenTicket("t1").contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
				.expectError(IllegalStateException.class).verify();
	}

	@Test
	void cancelTicket_userCancellingOthersTicket_shouldFail() {
		ticket.setStatus(TicketStatus.CREATED);
		ticket.setCreatedBy("otherUser");
		when(ticketRepository.findById("t1")).thenReturn(Mono.just(ticket));
		TestingAuthenticationToken auth = new TestingAuthenticationToken("user1", null, "ROLE_USER");
		StepVerifier
				.create(service.cancelTicket("t1").contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
				.expectError(IllegalStateException.class).verify();
	}

	@Test
	void closeTicket_success() {
		ticket.setStatus(TicketStatus.RESOLVED);
		ticket.setAssignedTo("agent1");
		when(ticketRepository.findById("t1")).thenReturn(Mono.just(ticket));
		when(ticketRepository.save(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));
		when(ticketHistoryService.record(any(), any(), any(), any())).thenReturn(Mono.empty());

		Jwt jwt = Jwt.withTokenValue("t").header("alg", "none").subject("agent1").issuedAt(Instant.now())
				.expiresAt(Instant.now().plusSeconds(3600)).build();
		JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt, Set.of(() -> "ROLE_AGENT"));

		StepVerifier
				.create(service.closeTicket("t1").contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
				.expectNextCount(1).verifyComplete();
	}

	@Test
	void updateStatus_adminCanUpdateAnyTicket() {
		ticket.setStatus(TicketStatus.ASSIGNED);
		ticket.setAssignedTo("otherAgent");
		when(ticketRepository.findById("t1")).thenReturn(Mono.just(ticket));
		when(ticketRepository.save(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));
		when(ticketHistoryService.record(any(), any(), any(), any())).thenReturn(Mono.empty());

		Jwt jwt = Jwt.withTokenValue("t").header("alg", "none").subject("admin").issuedAt(Instant.now())
				.expiresAt(Instant.now().plusSeconds(3600)).build();
		JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt, Set.of(() -> "ROLE_ADMIN"));

		StepVerifier
				.create(service.updateStatus("t1", TicketStatus.IN_PROGRESS)
						.contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
				.expectNextCount(1).verifyComplete();
	}

	@Test
	void getTickets_withStatusAndPriorityFilters() {
		when(ticketRepository.findByAssignedTo("agent1")).thenReturn(Flux.just(ticket));
		TestingAuthenticationToken auth = new TestingAuthenticationToken("agent1", null, "ROLE_AGENT");
		StepVerifier
				.create(service.getTickets(TicketStatus.CREATED, TicketPriority.MEDIUM, 0, 10)
						.contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
				.expectNextCount(1).verifyComplete();
	}

	@Test
	void getTimeline_withComment() {
		TicketCommentResponse response = new TicketCommentResponse("c1", "hello", "user1", "USER", Instant.now());
		when(ticketHistoryService.getHistory("t1")).thenReturn(Flux.empty());
		when(commentService.getComments("t1")).thenReturn(Flux.just(response));
		StepVerifier.create(service.getTimeline("t1")).expectNextCount(1).verifyComplete();
	}

	@Test
	void updateStatus_cancelledTicket_shouldFail() {
		ticket.setStatus(TicketStatus.CANCELLED);
		when(ticketRepository.findById("t1")).thenReturn(Mono.just(ticket));
		Jwt jwt = Jwt.withTokenValue("t").header("alg", "none").subject("agent1").issuedAt(Instant.now())
				.expiresAt(Instant.now().plusSeconds(3600)).build();
		JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt, Set.of(() -> "ROLE_AGENT"));
		StepVerifier
				.create(service.updateStatus("t1", TicketStatus.IN_PROGRESS)
						.contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
				.expectError(IllegalStateException.class).verify();
	}

	@Test
	void updateStatus_agentUpdatingOwnTicket_allowed() {
		ticket.setStatus(TicketStatus.ASSIGNED);
		ticket.setAssignedTo("agent1");
		when(ticketRepository.findById("t1")).thenReturn(Mono.just(ticket));
		when(ticketRepository.save(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));
		when(ticketHistoryService.record(any(), any(), any(), any())).thenReturn(Mono.empty());
		Jwt jwt = Jwt.withTokenValue("t").header("alg", "none").subject("agent1").issuedAt(Instant.now())
				.expiresAt(Instant.now().plusSeconds(3600)).build();
		JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt, Set.of(() -> "ROLE_AGENT"));
		StepVerifier
				.create(service.updateStatus("t1", TicketStatus.IN_PROGRESS)
						.contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
				.expectNextCount(1).verifyComplete();
	}

	@Test
	void reopenTicket_closedTicket_success() {
		ticket.setStatus(TicketStatus.CLOSED);
		when(ticketRepository.findById("t1")).thenReturn(Mono.just(ticket));
		when(ticketRepository.save(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));
		when(ticketHistoryService.record(any(), any(), any(), any())).thenReturn(Mono.empty());
		TestingAuthenticationToken auth = new TestingAuthenticationToken("user1", null, "ROLE_USER");
		StepVerifier
				.create(service.reopenTicket("t1").contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
				.expectNextCount(1).verifyComplete();
	}

	@Test
	void cancelTicket_resolvedTicket_shouldFail() {
		ticket.setStatus(TicketStatus.RESOLVED);
		when(ticketRepository.findById("t1")).thenReturn(Mono.just(ticket));
		TestingAuthenticationToken auth = new TestingAuthenticationToken("user1", null, "ROLE_USER");
		StepVerifier
				.create(service.cancelTicket("t1").contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
				.expectError(IllegalStateException.class).verify();
	}

	@Test
	void cancelTicket_userCancellingOthersTicket_shouldFail1() {
		ticket.setStatus(TicketStatus.CREATED);
		ticket.setCreatedBy("someoneElse");
		when(ticketRepository.findById("t1")).thenReturn(Mono.just(ticket));
		TestingAuthenticationToken auth = new TestingAuthenticationToken("user1", null, "ROLE_USER");
		StepVerifier
				.create(service.cancelTicket("t1").contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
				.expectError(IllegalStateException.class).verify();
	}
}