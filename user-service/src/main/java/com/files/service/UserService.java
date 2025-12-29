package com.files.service;
import com.files.dto.UserResponse;
import com.files.model.Role;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface UserService {

    Flux<UserResponse> getAllUsers();

    Mono<UserResponse> getById(String id);

    Mono<UserResponse> updateRoles(String id, Set<Role> roles);

    Mono<UserResponse> updateStatus(String id, boolean active);
}
