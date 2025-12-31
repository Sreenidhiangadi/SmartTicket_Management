package com.files.controller;


import com.files.dto.request.AssignTicketRequest;
import com.files.dto.response.AutoAssignmentResponse;
import com.files.mapper.AssignmentMapper;
import com.files.mapper.EscalationMapper;
import com.files.repository.EscalationLogRepository;
import com.files.service.AssignmentService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;
@RestController
@RequestMapping("/api/assign")
@RequiredArgsConstructor
@Slf4j
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final EscalationLogRepository escalationLogRepository;

    @PostMapping("/{ticketId}")
    public Mono<?> assign(
            @PathVariable String ticketId,
            @RequestBody AssignTicketRequest request
    ) {
        return assignmentService
                .assignTicket(
                        ticketId,
                        request.getAgentId(),
                        request.getPriority()
                )
                .map(AssignmentMapper::toResponse);
    }

    @GetMapping("/escalations")
    public Flux<?> escalationLogs() {
        return escalationLogRepository.findAll()
                .map(EscalationMapper::toResponse);
    }

    @GetMapping("/escalations/manager/{managerId}")
    public Flux<?> escalationsByManager(
            @PathVariable String managerId
    ) {
        return escalationLogRepository
                .findByEscalatedToManagerId(managerId)
                .map(EscalationMapper::toResponse);
    }
    @PostMapping("/auto/{ticketId}")
    public Mono<AutoAssignmentResponse> autoAssign(
            @PathVariable String ticketId,
            @RequestParam(required = false) String priority
    ) {
    	log.info("AUTO-ASSIGN hit for ticket {}", ticketId);
        return assignmentService.autoAssign(ticketId, priority);
    }

}
