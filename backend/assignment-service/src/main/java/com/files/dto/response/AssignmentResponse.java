package com.files.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AssignmentResponse {

    private String ticketId;
    private String agentId;
    private String priority;

    private Instant assignedAt;
    private Instant slaDueAt;

    private boolean escalated;
}
