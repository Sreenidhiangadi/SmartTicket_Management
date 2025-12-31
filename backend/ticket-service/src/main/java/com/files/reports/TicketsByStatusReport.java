package com.files.reports;

import com.files.model.TicketStatus;

public record TicketsByStatusReport(
        TicketStatus status,
        long count
) {}