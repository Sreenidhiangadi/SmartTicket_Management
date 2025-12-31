package com.files.service.impl;

import com.files.dto.AgentDto;
import com.files.dto.response.AutoAssignmentResponse;
import com.files.exception.AssignmentAlreadyExistsException;
import com.files.model.AgentWorkload;
import com.files.model.Assignment;
import com.files.model.EscalationLog;
import com.files.repository.AssignmentRepository;
import com.files.repository.EscalationLogRepository;
import com.files.service.AgentClientService;
import com.files.service.AssignmentService;
import com.files.util.SlaPolicyCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final EscalationLogRepository escalationLogRepository;
    private final AgentClientService agentClientService;

    @Override
    public Mono<Assignment> assignTicket(
            String ticketId,
            String agentId,
            String priority
    ) {

        return assignmentRepository.findByTicketId(ticketId)
            .flatMap(existing ->
                Mono.<Assignment>error(
                    new AssignmentAlreadyExistsException(ticketId)
                )
            )
            .switchIfEmpty(Mono.defer(() -> {

                String finalPriority =
                        priority != null ? priority : "MEDIUM";

                Assignment assignment = Assignment.builder()
                        .ticketId(ticketId)
                        .agentId(agentId)
                        .priority(finalPriority)
                        .assignedAt(Instant.now())
                        .slaDueAt(
                                SlaPolicyCalculator.calculateDueAt(finalPriority)
                        )
                        .escalated(false)
                        .build();

                return assignmentRepository.save(assignment);
            }));
    }


    @Override
    public Mono<AutoAssignmentResponse> autoAssign(
            String ticketId,
            String priority
    ) {

        String finalPriority = priority != null ? priority : "MEDIUM";

        return assignmentRepository.findByTicketId(ticketId)
            .flatMap(existing ->
                Mono.<AutoAssignmentResponse>error(
                    new AssignmentAlreadyExistsException(ticketId)
                )
            )
            .switchIfEmpty(Mono.defer(() -> {

                Mono<List<AgentDto>> agentsMono =
                        agentClientService
                                .fetchActiveAgents()
                                .collectList();

                Mono<Map<String, Long>> workloadMono =
                        assignmentRepository.findAll()
                                .groupBy(Assignment::getAgentId)
                                .flatMap(g ->
                                        g.count()
                                         .map(c -> Map.entry(g.key(), c))
                                )
                                .collectMap(
                                        Map.Entry::getKey,
                                        Map.Entry::getValue
                                );

                return Mono.zip(agentsMono, workloadMono)
                        .flatMap(tuple -> {

                            List<AgentDto> agents = tuple.getT1();
                            Map<String, Long> workload = tuple.getT2();

                            AgentWorkload selected =
                                    agents.stream()
                                            .map(agent ->
                                                    new AgentWorkload(
                                                            agent.getId(),
                                                            workload.getOrDefault(
                                                                    agent.getId(),
                                                                    0L
                                                            )
                                                    )
                                            )
                                            .min(Comparator.comparingLong(
                                                    AgentWorkload::getActiveTickets
                                            ))
                                            .orElseThrow(() ->
                                                    new IllegalStateException(
                                                            "No active agents found"
                                                    )
                                            );

                            Assignment assignment = Assignment.builder()
                                    .ticketId(ticketId)
                                    .agentId(selected.getAgentId())
                                    .priority(finalPriority)
                                    .assignedAt(Instant.now())
                                    .slaDueAt(
                                            SlaPolicyCalculator
                                                    .calculateDueAt(finalPriority)
                                    )
                                    .escalated(false)
                                    .build();

                            return assignmentRepository.save(assignment)
                            	    .map(saved ->
                            	        AutoAssignmentResponse.builder()
                            	            .ticketId(ticketId)
                            	            .agentId(saved.getAgentId())
                            	            .priority(finalPriority)
                            	            .slaDueAt(saved.getSlaDueAt())
                            	            .build()
                            	    );

                        });
            }));
    }


    @Override
    public Mono<Assignment> escalate(
            Assignment assignment,
            String reason
    ) {

        assignment.setEscalated(true);

        EscalationLog log = EscalationLog.builder()
                .ticketId(assignment.getTicketId())
                .agentId(assignment.getAgentId())
                .escalatedToManagerId("AUTO_MANAGER")
                .reason(reason)
                .escalatedAt(Instant.now())
                .build();

        return assignmentRepository.save(assignment)
                .then(escalationLogRepository.save(log))
                .thenReturn(assignment);
    }


   

}
