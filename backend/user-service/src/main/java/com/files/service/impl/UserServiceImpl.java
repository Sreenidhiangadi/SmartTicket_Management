package com.files.service.impl;

import com.files.dto.UserResponse;
import com.files.exception.NotFoundException;
import com.files.model.Role;
import com.files.repository.UserRepository;
import com.files.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public Flux<UserResponse> getAllUsers() {
        return repository.findAll().map(UserResponse::from);
    }

    @Override
    public Mono<UserResponse> getById(String id) {
        return repository.findById(id)
            .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
            .map(UserResponse::from);
    }

    @Override
    public Mono<UserResponse> updateRoles(String id, Set<Role> roles) {
        return repository.findById(id)
            .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
            .flatMap(user -> {
                user.setRoles(roles);
                return repository.save(user);
            })
            .map(UserResponse::from);
    }

    @Override
    public Mono<UserResponse> updateStatus(String id, boolean active) {
        return repository.findById(id)
            .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
            .flatMap(user -> {
                user.setActive(active);
                return repository.save(user);
            })
            .map(UserResponse::from);
    }
    
    @Override
    public Flux<UserResponse> getActiveAgents() {
        return repository
                .findByRolesContainingAndActive("AGENT", true)
                .map(UserResponse::from);
    }

}
