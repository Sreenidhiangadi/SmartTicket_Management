package com.files.dto;

import java.time.Instant;

import lombok.Data;

@Data
public class AutoAssignmentResponse {
    private String ticketId;
    private String agentId;
    private String priority;
    private Instant slaDueAt;
    private String agentEmail;

}
