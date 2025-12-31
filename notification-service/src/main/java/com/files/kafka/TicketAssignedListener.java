//package com.files.kafka;
//
//
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//import com.files.messaging.TicketAssignedEvent;
//import com.files.model.NotificationType;
//import com.files.service.NotificationService;
//
//import lombok.RequiredArgsConstructor;
//
//@Component
//@RequiredArgsConstructor
//public class TicketAssignedListener {
//
//    private final NotificationService notificationService;
//
//    @KafkaListener(
//            topics = "ticket-assigned-events",
//            groupId = "notification-service",
//            containerFactory = "kafkaListenerContainerFactory"
//    )
//    public void onMessage(TicketAssignedEvent event) {
//        notificationService.notifyUser(
//                event.getAgentId(),
//                NotificationType.TICKET_ASSIGNED,
//                "New ticket assigned",
//                "Ticket " + event.getTicketId() + " has been assigned to you"
//        ).subscribe();
//    }
//}
