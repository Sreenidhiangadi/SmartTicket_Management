package com.files.messaging;

import lombok.Data;

@Data
public class TicketCreatedEvent {
    private String ticketId;
    private String createdByUserId;
    private String createdByEmail;
    private String title;
}
