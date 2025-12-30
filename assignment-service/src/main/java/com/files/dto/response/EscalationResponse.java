package com.files.dto.response;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class EscalationResponse {

    private String ticketId;
    private String agentId;
    private String escalatedToManagerId;

    private String reason;
    private Instant escalatedAt;
}

