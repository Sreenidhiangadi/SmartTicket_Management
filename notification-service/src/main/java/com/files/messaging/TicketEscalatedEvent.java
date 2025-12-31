package com.files.messaging;

import lombok.Data;

@Data
public class TicketEscalatedEvent {
    private String ticketId;
    private String managerId;
}
