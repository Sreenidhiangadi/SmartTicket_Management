package com.files.mapper;
import com.files.dto.response.EscalationResponse;
import com.files.model.EscalationLog;

public final class EscalationMapper {

    private EscalationMapper() {}

    public static EscalationResponse toResponse(EscalationLog log) {
        return EscalationResponse.builder()
                .ticketId(log.getTicketId())
                .agentId(log.getAgentId())
                .escalatedToManagerId(log.getEscalatedToManagerId())
                .reason(log.getReason())
                .escalatedAt(log.getEscalatedAt())
                .build();
    }
}
