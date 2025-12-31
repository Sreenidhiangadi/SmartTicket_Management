package com.files.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AgentWorkloadResponse {

    private String agentId;
    private long activeTickets;
}
