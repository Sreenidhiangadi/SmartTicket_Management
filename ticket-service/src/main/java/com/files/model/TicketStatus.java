package com.files.model;

import java.util.Set;

public enum TicketStatus {

    CREATED,
    ASSIGNED,
    IN_PROGRESS,
    RESOLVED,
    CLOSED,
    CANCELLED;

    public static boolean canTransition(TicketStatus from, TicketStatus to) {

        return switch (from) {

            case CREATED -> Set.of(ASSIGNED, CANCELLED).contains(to);

            case ASSIGNED -> Set.of(IN_PROGRESS, CANCELLED).contains(to);

            case IN_PROGRESS -> Set.of(RESOLVED).contains(to);

            case RESOLVED -> Set.of(CLOSED).contains(to);

            case CLOSED -> Set.of(CREATED).contains(to); // reopen

            case CANCELLED -> false;
        };
    }

    public static boolean canReopen(TicketStatus status) {
        return status == RESOLVED || status == CLOSED;
    }
}
