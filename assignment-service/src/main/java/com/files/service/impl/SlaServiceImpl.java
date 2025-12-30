package com.files.service.impl;

import com.files.dto.response.SlaCheckResult;
import com.files.model.AgentWorkload;
import com.files.model.Assignment;
import com.files.repository.AssignmentRepository;
import com.files.service.AssignmentService;
import com.files.service.SlaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SlaServiceImpl implements SlaService {

    private final AssignmentRepository assignmentRepository;
    private final AssignmentService assignmentService;

    @Override
    public Flux<SlaCheckResult> checkSla(boolean dryRun) {

        Instant now = Instant.now();

        return assignmentRepository.findByEscalated(false)
            .filter(a -> a.getSlaDueAt().isBefore(now))
            .flatMap(assignment -> {

                if (dryRun) {
                    return Flux.just(
                        new SlaCheckResult(
                            assignment.getTicketId(),
                            true,                      
                            assignment.getSlaDueAt(),
                            false                      
                        )
                    );
                }

                return assignmentService
                    .escalate(assignment, "SLA_BREACH")
                    .map(a ->
                        new SlaCheckResult(
                            a.getTicketId(),
                            true,                     
                            a.getSlaDueAt(),
                            true                       
                        )
                    );
            });
    }

    @Override
    public Flux<AgentWorkload> calculateWorkload() {

        return assignmentRepository.findAll()
            .groupBy(Assignment::getAgentId)
            .flatMap(group ->
                group.count()
                    .map(count ->
                        new AgentWorkload(group.key(), count)
                    )
            );
    }
}
