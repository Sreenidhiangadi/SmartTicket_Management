package com.files.messaging;
import java.time.Instant;

import lombok.Data;

@Data
public class TicketAssignedEvent {
    private String ticketId;
    private String agentId;
    private Instant assignedAt;
}
