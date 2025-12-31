//package com.files.kafka;
//
//
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//import com.files.messaging.TicketEscalatedEvent;
//import com.files.model.NotificationType;
//import com.files.service.NotificationService;
//
//import lombok.RequiredArgsConstructor;
//
//@Component
//@RequiredArgsConstructor
//public class TicketEscalatedListener {
//
//    private final NotificationService notificationService;
//
//    @KafkaListener(
//            topics = "ticket-escalated-events",
//            groupId = "notification-service",
//            containerFactory = "kafkaListenerContainerFactory"
//    )
//    public void onMessage(TicketEscalatedEvent event) {
//        notificationService.notifyUser(
//                event.getManagerId(),
//                NotificationType.ESCALATION,
//                "Ticket escalated",
//                "Ticket " + event.getTicketId() + " has been escalated"
//        ).subscribe();
//    }
//}
