package com.files.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "ticket_comments")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketComment {

    @Id
    private String id;

    private String ticketId;
    private String comment;

    private String commentedBy;     
    private String role;           

    private Instant commentedAt;
}
