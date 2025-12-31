package com.files.service;
import com.files.model.NotificationType;

import reactor.core.publisher.Mono;

public interface NotificationService {

    Mono<Void> notifyUser(
            String userId,
            String email,
            NotificationType type,
            String title,
            String message
    );

}
