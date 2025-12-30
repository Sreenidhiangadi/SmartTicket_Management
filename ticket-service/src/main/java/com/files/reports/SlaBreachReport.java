package com.files.reports;

public record SlaBreachReport(
        String ticketId,
        long resolutionTimeMinutes
) {}
