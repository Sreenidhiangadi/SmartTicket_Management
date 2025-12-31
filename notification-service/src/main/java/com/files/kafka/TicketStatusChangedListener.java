package com.files.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.files.messaging.TicketStatusChangedEvent;
import com.files.model.NotificationType;
import com.files.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TicketStatusChangedListener {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "ticket-status-events",
            groupId = "notification-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onMessage(TicketStatusChangedEvent event) {
    	String subject = "Ticket Status Updated";

        String body =
                "Your ticket status was updated.\n\n" +
                "Previous status: " + event.getOldStatus() + "\n" +
                "Current status: " + event.getNewStatus() + "\n\n" +
                "Ticket ID: " + event.getTicketId();
        notificationService.notifyUser(
                event.getUserId(),
                event.getUserEmail(),
                NotificationType.STATUS_CHANGED,
                subject,
                body
        ).subscribe();
    }
}
