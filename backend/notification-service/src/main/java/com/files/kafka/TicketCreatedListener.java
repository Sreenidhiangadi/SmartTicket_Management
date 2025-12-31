package com.files.kafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.files.messaging.TicketCreatedEvent;
import com.files.model.NotificationType;
import com.files.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TicketCreatedListener {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "ticket-created-events",
            groupId = "notification-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onMessage(TicketCreatedEvent event) {
    	String subject = "Ticket Created Successfully";

        String body =
                "Your ticket \"" + event.getTitle() + "\" was created successfully.\n\n" +
                "Ticket ID: " + event.getTicketId() + "\n" +
                "We’ll notify you when the status changes.";
        notificationService.notifyUser(
                event.getCreatedByUserId(),
                event.getCreatedByEmail(),
                NotificationType.TICKET_CREATED,
                subject,
                body

        ).subscribe();
    }
}
