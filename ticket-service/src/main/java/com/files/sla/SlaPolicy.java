package com.files.sla;

import com.files.model.TicketPriority;

import java.time.Duration;
import java.util.Map;

public class SlaPolicy {

    private static final Map<TicketPriority, Duration> SLA_MAP = Map.of(
        TicketPriority.HIGH, Duration.ofHours(4),
        TicketPriority.MEDIUM, Duration.ofHours(8),
        TicketPriority.LOW, Duration.ofHours(24)
    );

    public static Duration getDuration(TicketPriority priority) {
        return SLA_MAP.get(priority);
    }
}
