package com.files.dto;

import com.files.model.*;
import java.time.Instant;

public record TicketResponse(
        String id,
        String title,
        String description,
        TicketCategory category,
        TicketPriority priority,
        TicketStatus status,
        String createdBy,
        String assignedTo,
        Instant createdAt,
        Instant updatedAt,
        Instant resolvedAt,
        Instant closedAt,
        Instant slaDueAt,
        boolean slaBreached
        
) {}
