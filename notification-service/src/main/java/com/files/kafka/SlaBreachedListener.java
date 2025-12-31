//package com.files.kafka;
//
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//import com.files.messaging.SlaBreachedEvent;
//import com.files.model.NotificationType;
//import com.files.service.NotificationService;
//
//import lombok.RequiredArgsConstructor;
//
//@Component
//@RequiredArgsConstructor
//public class SlaBreachedListener {
//
//    private final NotificationService notificationService;
//
//    @KafkaListener(
//            topics = "sla-breached-events",
//            groupId = "notification-service",
//            containerFactory = "kafkaListenerContainerFactory"
//    )
//    public void onMessage(SlaBreachedEvent event) {
//        notificationService.notifyUser(
//                event.getManagerId(),
//                NotificationType.SLA_BREACH,
//                "SLA breached",
//                "SLA breached for ticket " + event.getTicketId()
//        ).subscribe();
//    }
//}
