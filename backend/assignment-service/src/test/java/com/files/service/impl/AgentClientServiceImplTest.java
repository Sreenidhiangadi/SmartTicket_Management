package com.files.service.impl;

import com.files.dto.AgentDto;
import com.files.service.impl.AgentClientServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgentClientServiceImplTest {

    @Mock
    private WebClient userServiceClient;

    @InjectMocks
    private AgentClientServiceImpl service;

    @Test
    void fetchActiveAgents_success() {

        WebClient.RequestHeadersUriSpec uriSpec =
                mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec =
                mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec =
                mock(WebClient.ResponseSpec.class);

        AgentDto agent = new AgentDto();
        agent.setId("a1");
        agent.setActive(true);

        when(userServiceClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri("/users/agents")).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(AgentDto.class))
                .thenReturn(Flux.just(agent));

        StepVerifier.create(service.fetchActiveAgents())
                .expectNextMatches(a -> a.getId().equals("a1") && a.isActive())
                .verifyComplete();
    }
}
