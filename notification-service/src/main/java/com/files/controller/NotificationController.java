package com.files.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.files.model.Notification;
import com.files.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;

    @GetMapping
    public Flux<Notification> getMyNotifications(
            @AuthenticationPrincipal Jwt jwt) {

        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(jwt.getSubject());
    }
}
