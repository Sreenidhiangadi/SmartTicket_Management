package com.files.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentWorkload {

    private String agentId;
    private long activeTickets;
}
