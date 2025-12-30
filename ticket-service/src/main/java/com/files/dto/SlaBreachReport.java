package com.files.dto;

import java.time.Instant;

public record SlaBreachReport(
        String ticketId,
        Instant slaDueAt,
        String status
) {}
