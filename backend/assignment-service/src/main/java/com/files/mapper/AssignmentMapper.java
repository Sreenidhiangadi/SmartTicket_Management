package com.files.mapper;

import com.files.dto.response.AssignmentResponse;
import com.files.model.Assignment;

public final class AssignmentMapper {

    private AssignmentMapper() {}

    public static AssignmentResponse toResponse(Assignment assignment) {
        return AssignmentResponse.builder()
                .ticketId(assignment.getTicketId())
                .agentId(assignment.getAgentId())
                .priority(assignment.getPriority())
                .assignedAt(assignment.getAssignedAt())
                .slaDueAt(assignment.getSlaDueAt())
                .escalated(assignment.isEscalated())
                .build();
    }
}
