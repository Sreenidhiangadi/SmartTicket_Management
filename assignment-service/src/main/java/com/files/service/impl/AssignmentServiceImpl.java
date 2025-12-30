package com.files.service.impl;

import com.files.exception.AssignmentAlreadyExistsException;
import com.files.model.Assignment;
import com.files.model.EscalationLog;
import com.files.repository.AssignmentRepository;
import com.files.repository.EscalationLogRepository;
import com.files.service.AssignmentService;
import com.files.util.SlaPolicyCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl
        implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final EscalationLogRepository escalationLogRepository;

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
