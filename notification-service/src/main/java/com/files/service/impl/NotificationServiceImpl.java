package com.files.service.impl;

import java.time.Instant;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.files.model.Notification;
import com.files.model.NotificationType;
import com.files.repository.NotificationRepository;
import com.files.service.NotificationService;

import jakarta.annotation.PostConstruct;
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

//    @PostConstruct
//    public void testMail() {
//        try {
//            SimpleMailMessage msg = new SimpleMailMessage();
//            msg.setTo("angadisreenidhi@gmail.com");
//            msg.setSubject("Test Mail");
//            msg.setText("If you see this, SMTP works");
//
//            mailSender.send(msg);
//
//            log.info("SMTP sanity check mail sent successfully");
//        } catch (Exception e) {
//            log.error("SMTP sanity check failed", e);
//        }
//    }

}
