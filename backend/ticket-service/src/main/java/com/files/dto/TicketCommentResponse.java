package com.files.dto;

import java.time.Instant;

public record TicketCommentResponse(
        String id,
        String comment,
        String commentedBy,
        String role,
        Instant commentedAt
) {}
