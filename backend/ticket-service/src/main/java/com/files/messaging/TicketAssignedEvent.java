package com.files.messaging;
import lombok.Data;
import java.time.Instant;

@Data
public class TicketAssignedEvent {
    private String ticketId;
    private String agentId;
    private Instant assignedAt;
}
