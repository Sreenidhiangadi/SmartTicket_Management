package com.files.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.files.model.Notification;

import reactor.core.publisher.Flux;

public interface NotificationRepository
        extends ReactiveMongoRepository<Notification, String> {

    Flux<Notification> findByUserIdOrderByCreatedAtDesc(String userId);
}
