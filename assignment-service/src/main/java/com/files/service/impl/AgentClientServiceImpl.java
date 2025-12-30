package com.files.service.impl;

import com.files.dto.AgentDto;
import com.files.service.AgentClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class AgentClientServiceImpl implements AgentClientService {

    private final WebClient userServiceClient;

    @Override
    public Flux<AgentDto> fetchActiveAgents() {
        return userServiceClient
                .get()
                .uri("/users/agents")
                .retrieve()
                .bodyToFlux(AgentDto.class);
    }
}
