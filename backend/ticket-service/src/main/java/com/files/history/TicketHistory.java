package com.files.history;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "ticket_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketHistory {

    @Id
    private String id;

    private String ticketId;

    private TicketHistoryAction action;

    private String performedBy;

    private String description;

    private Instant createdAt;
}
