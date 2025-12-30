package com.files.dashboard;

import com.files.model.TicketPriority;
import com.files.model.TicketStatus;

import java.util.Map;

public record DashboardSummaryResponse(
        long totalTickets,
        long openTickets,
        long closedTickets,
        long unassignedTickets,
        Map<TicketStatus, Long> ticketsByStatus,
        Map<TicketPriority, Long> ticketsByPriority
) {}