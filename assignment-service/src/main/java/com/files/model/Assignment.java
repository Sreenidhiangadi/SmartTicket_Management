package com.files.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "assignments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {

    @Id
    private String id;

    private String ticketId;
    private String agentId;

    private String priority; 

    private Instant assignedAt;
    private Instant slaDueAt;

    private boolean escalated;
}
