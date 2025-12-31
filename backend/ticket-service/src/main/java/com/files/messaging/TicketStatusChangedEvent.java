package com.files.messaging;
import lombok.Data;

@Data
public class TicketStatusChangedEvent {
    private String ticketId;
    private String userId;
    private String userEmail;
    private String oldStatus;
    private String newStatus;
}
