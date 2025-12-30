package com.files.service;

import com.files.dto.CreateTicketRequest;
import com.files.dto.TicketResponse;
import com.files.dto.TimelineItemResponse;
import com.files.model.TicketPriority;
import com.files.model.TicketStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TicketService {

    Mono<TicketResponse> createTicket(CreateTicketRequest request);

    Mono<TicketResponse> getTicketById(String id);

    Flux<TicketResponse> getTicketsByUser(String userId);

    Flux<TicketResponse> getTicketsByStatus(TicketStatus status);

    Mono<TicketResponse> assignTicket(String ticketId, String agentId);

    Mono<TicketResponse> updateStatus(String ticketId, TicketStatus status);

    Mono<TicketResponse> closeTicket(String ticketId);

    Mono<TicketResponse> reopenTicket(String ticketId);

    Mono<TicketResponse> cancelTicket(String ticketId);
    
    Flux<TicketResponse> getTickets(TicketStatus status,TicketPriority priority,int page,int size);
    
    Flux<TimelineItemResponse> getTimeline(String ticketId);
}
