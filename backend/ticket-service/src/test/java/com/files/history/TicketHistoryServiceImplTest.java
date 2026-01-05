package com.files.history;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketHistoryServiceImplTest {

    @Mock
    private TicketHistoryRepository repository;

    @InjectMocks
    private TicketHistoryServiceImpl service;

    @Test
    void record_success() {
        when(repository.save(any()))
                .thenAnswer(i -> Mono.just(i.getArgument(0)));

        StepVerifier.create(
                        service.record(
                                "t1",
                                TicketHistoryAction.CREATED,
                                "user1",
                                "created"
                        )
                )
                .verifyComplete();
    }

    @Test
    void getHistory_success() {
        when(repository.findByTicketIdOrderByCreatedAtAsc("t1"))
                .thenReturn(Flux.just(new TicketHistory()));

        StepVerifier.create(service.getHistory("t1"))
                .expectNextCount(1)
                .verifyComplete();
    }
}
