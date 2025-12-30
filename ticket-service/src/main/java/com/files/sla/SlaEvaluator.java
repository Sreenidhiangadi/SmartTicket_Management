package com.files.sla;

import com.files.model.Ticket;
import com.files.model.TicketStatus;

import java.time.Instant;
import java.util.Set;

public class SlaEvaluator {

    private static final Set<TicketStatus> CLOSED_STATUSES =
            Set.of(TicketStatus.RESOLVED, TicketStatus.CLOSED);

    public static boolean isBreached(Ticket ticket) {
        return ticket.getSlaDueAt() != null
            && Instant.now().isAfter(ticket.getSlaDueAt())
            && !CLOSED_STATUSES.contains(ticket.getStatus());
    }
}
