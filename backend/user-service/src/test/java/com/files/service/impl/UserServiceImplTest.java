package com.files.service.impl;

import com.files.exception.NotFoundException;
import com.files.model.Role;
import com.files.model.User;
import com.files.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void getAllUsers_success() {
        when(repository.findAll()).thenReturn(Flux.just(new User()));

        StepVerifier.create(service.getAllUsers())
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getById_success() {
        when(repository.findById("1")).thenReturn(Mono.just(new User()));

        StepVerifier.create(service.getById("1"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getById_notFound() {
        when(repository.findById("1")).thenReturn(Mono.empty());

        StepVerifier.create(service.getById("1"))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void updateRoles_success() {
        User user = new User();
        when(repository.findById("1")).thenReturn(Mono.just(user));
        when(repository.save(any())).thenReturn(Mono.just(user));

        StepVerifier.create(service.updateRoles("1", Set.of(Role.ADMIN)))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void updateRoles_userNotFound() {
        when(repository.findById("1")).thenReturn(Mono.empty());

        StepVerifier.create(service.updateRoles("1", Set.of(Role.ADMIN)))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void updateStatus_success() {
        User user = new User();
        when(repository.findById("1")).thenReturn(Mono.just(user));
        when(repository.save(any())).thenReturn(Mono.just(user));

        StepVerifier.create(service.updateStatus("1", true))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void updateStatus_userNotFound() {
        when(repository.findById("1")).thenReturn(Mono.empty());

        StepVerifier.create(service.updateStatus("1", true))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void getActiveAgents_success() {
        when(repository.findByRolesContainingAndActive("AGENT", true))
                .thenReturn(Flux.just(new User()));

        StepVerifier.create(service.getActiveAgents())
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getActiveAgents_empty() {
        when(repository.findByRolesContainingAndActive("AGENT", true))
                .thenReturn(Flux.empty());

        StepVerifier.create(service.getActiveAgents())
                .verifyComplete();
    }
}
