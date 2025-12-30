package com.files.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "tickets")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {

    @Id
    private String id;

    private String title;
    private String description;

    private TicketCategory category;
    private TicketPriority priority;
    private TicketStatus status;

    private String createdBy; 
    private String assignedTo; 

    private Instant createdAt;
    private Instant updatedAt;
    private Instant resolvedAt;
    private Instant closedAt;
    private Instant slaDueAt;
    private Boolean slaBreached;

}
