package com.files.controller;

import com.files.dashboard.DashboardService;
import com.files.dashboard.DashboardSummaryResponse;
import com.files.dto.AddCommentRequest;
import com.files.dto.CreateTicketRequest;
import com.files.dto.TicketCommentResponse;
import com.files.dto.TicketResponse;
import com.files.dto.TimelineItemResponse;
import com.files.dto.UpdateTicketStatusRequest;
import com.files.history.TicketHistory;
import com.files.history.TicketHistoryService;
import com.files.model.Ticket;
import com.files.model.TicketPriority;
import com.files.model.TicketStatus;
import com.files.service.TicketCommentService;
import com.files.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final TicketHistoryService ticketHistoryService;
    private final TicketCommentService commentService;
    private final DashboardService dashboardService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TicketResponse> createTicket(
            @Valid @RequestBody CreateTicketRequest request
    ) {
        return ticketService.createTicket(request);
    }


    @GetMapping("/{id}")
    public Mono<TicketResponse> getTicketById(@PathVariable String id) {
        return ticketService.getTicketById(id);
    }

    @GetMapping("/user/{userId}")
    public Flux<TicketResponse> getTicketsByUser(@PathVariable String userId) {
        return ticketService.getTicketsByUser(userId);
    }
    @GetMapping("/agent/{agentId}")
    public Flux<TicketResponse> getTicketsByAgent(
            @PathVariable String agentId
    ) {
        return ticketService.getTicketsByAgent(agentId);
    }

    @GetMapping("/status/{status}")
    public Flux<TicketResponse> getTicketsByStatus(
            @PathVariable TicketStatus status
    ) {
        return ticketService.getTicketsByStatus(status);
    }

    @PutMapping("/{id}/assign")
    public Mono<TicketResponse> assignTicket(
            @PathVariable String id,
            @RequestParam String agentId
    ) {
        return ticketService.assignTicket(id, agentId);
    }


    @PutMapping("/{id}/status")
    public Mono<TicketResponse> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateTicketStatusRequest request
    ) {
        return ticketService.updateStatus(id, request.status());
    }

    @PutMapping("/{id}/close")
    public Mono<TicketResponse> closeTicket(@PathVariable String id) {
        return ticketService.closeTicket(id);
    }

    @PutMapping("/{id}/reopen")
    public Mono<TicketResponse> reopenTicket(@PathVariable String id) {
        return ticketService.reopenTicket(id);
    }

    @PutMapping("/{id}/cancel")
    public Mono<TicketResponse> cancelTicket(@PathVariable String id) {
        return ticketService.cancelTicket(id);
    }
    @GetMapping("/{id}/history")
    public Flux<TicketHistory> getTicketHistory(@PathVariable String id) {
        return ticketHistoryService.getHistory(id);
    }
    @PostMapping("/{ticketId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TicketCommentResponse> addComment(
            @PathVariable String ticketId,
            @Valid @RequestBody AddCommentRequest request
    ) {
        return commentService.addComment(ticketId, request);
    }
    @GetMapping("/{ticketId}/comments")
    public Flux<TicketCommentResponse> getComments(
            @PathVariable String ticketId
    ) {
        return commentService.getComments(ticketId);
    }
    @GetMapping
    public Flux<TicketResponse> getTickets(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketPriority priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ticketService.getTickets(status, priority, page, size);
    }
    @GetMapping("/{id}/timeline")
    public Flux<TimelineItemResponse> getTimeline(@PathVariable String id) {
        return ticketService.getTimeline(id);
    }
    @PostMapping("/{ticketId}/auto-assign")
    public Mono<Ticket> autoAssign(
            @PathVariable String ticketId
    ) {
        return ticketService.autoAssignTicket(ticketId);
    }
    @PutMapping("/{ticketId}/sla-breached")
    public Mono<Void> markSlaBreached(@PathVariable String ticketId) {
        return ticketService.markSlaBreached(ticketId);
    }


}
