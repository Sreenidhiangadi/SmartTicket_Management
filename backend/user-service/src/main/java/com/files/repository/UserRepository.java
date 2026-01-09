package com.files.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.files.model.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {

    Mono<User> findByEmail(String email);

    Mono<Boolean> existsByEmail(String email);
    
    Flux<User> findByRolesContainingAndActive(String role, boolean active);
    
    Mono<User> findByResetToken(String resetToken);

}
