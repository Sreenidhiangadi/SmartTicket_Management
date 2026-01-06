package com.files.service.impl;

import java.time.Instant;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.files.model.Notification;
import com.files.model.NotificationType;
import com.files.repository.NotificationRepository;
import com.files.service.NotificationService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    @Override
    @CircuitBreaker(
        name = "notificationServiceCB",
        fallbackMethod = "notifyUserFallback"
    )
    public Mono<Void> notifyUser(
            String userId,
            String email,
            NotificationType type,
            String title,
            String message) {

        Notification notification = new Notification(
                null,
                userId,
                type,
                title,
                message,
                false,
                Instant.now()
        );

        return notificationRepository.save(notification)
                .then(sendEmail(email, title, message));
    }

    /**
     * Fallback method when circuit breaker is OPEN or email fails.
     * Signature MUST match original method + Throwable at the end.
     */
    private Mono<Void> notifyUserFallback(
            String userId,
            String email,
            NotificationType type,
            String title,
            String message,
            Throwable ex) {

        log.error("Notification service fallback triggered. Email skipped.", ex);

        Notification fallbackNotification = new Notification(
                null,
                userId,
                type,
                title,
                message + " (email delivery pending)",
                false,
                Instant.now()
        );

        return notificationRepository.save(fallbackNotification).then();
    }

    private Mono<Void> sendEmail(String email, String subject, String body) {
        return Mono.fromRunnable(() -> {
            if (email == null || email.isBlank()) {
                log.warn("Skipping email notification: recipient email is null");
                return;
            }

            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(email);
            mail.setSubject(subject);
            mail.setText(body);

            mailSender.send(mail);
        });
    }
}
