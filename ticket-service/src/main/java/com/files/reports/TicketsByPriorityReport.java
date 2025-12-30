package com.files.reports;

import com.files.model.TicketPriority;

public record TicketsByPriorityReport(
        TicketPriority priority,
        long count
) {}