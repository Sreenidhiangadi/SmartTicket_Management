package com.files.service;

import com.files.dto.AgentDto;
import reactor.core.publisher.Flux;

public interface AgentClientService {
    Flux<AgentDto> fetchActiveAgents();
}
