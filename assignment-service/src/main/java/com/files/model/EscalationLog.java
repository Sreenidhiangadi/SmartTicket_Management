package com.files.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "escalation_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EscalationLog {

    @Id
    private String id;

    private String ticketId;
    private String agentId;
    private String escalatedToManagerId;

    private String reason; // SLA_BREACH, MANUAL, PRIORITY_CHANGE

    private Instant escalatedAt;
}
