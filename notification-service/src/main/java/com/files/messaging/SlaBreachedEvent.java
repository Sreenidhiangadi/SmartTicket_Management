package com.files.messaging;

import lombok.Data;

@Data
public class SlaBreachedEvent {
    private String ticketId;
    private String agentId;
    private String managerId;
}
