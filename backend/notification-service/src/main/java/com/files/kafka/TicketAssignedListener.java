package com.files.kafka;

import com.files.messaging.TicketAssignedEvent;
import com.files.model.NotificationType;
import com.files.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TicketAssignedListener {

    private final NotificationService notificationService;

    @KafkaListener(
        topics = "ticket-assigned-events",
        groupId = "notification-service",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void onMessage(TicketAssignedEvent event) {

        String subject = "New Ticket Assigned to You";

        String body =
            "A new ticket has been assigned to you.\n\n" +
            "Ticket ID: " + event.getTicketId() + "\n" +
            "Assigned at: " + event.getAssignedAt();

        notificationService.notifyUser(
            event.getAgentId(),
            event.getAgentEmail(),
            NotificationType.TICKET_ASSIGNED,
            subject,
            body
        ).subscribe();
    }
}

