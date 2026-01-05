package com.files.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TicketStatusTest {

    @Test
    void canTransition_validTransitions() {
        assertTrue(TicketStatus.canTransition(TicketStatus.CREATED, TicketStatus.ASSIGNED));
        assertTrue(TicketStatus.canTransition(TicketStatus.CREATED, TicketStatus.CANCELLED));

        assertTrue(TicketStatus.canTransition(TicketStatus.ASSIGNED, TicketStatus.IN_PROGRESS));
        assertTrue(TicketStatus.canTransition(TicketStatus.ASSIGNED, TicketStatus.CANCELLED));

        assertTrue(TicketStatus.canTransition(TicketStatus.IN_PROGRESS, TicketStatus.RESOLVED));

        assertTrue(TicketStatus.canTransition(TicketStatus.RESOLVED, TicketStatus.CLOSED));

        assertTrue(TicketStatus.canTransition(TicketStatus.CLOSED, TicketStatus.CREATED));
    }

    @Test
    void canTransition_invalidTransitions() {
        assertFalse(TicketStatus.canTransition(TicketStatus.CREATED, TicketStatus.RESOLVED));
        assertFalse(TicketStatus.canTransition(TicketStatus.IN_PROGRESS, TicketStatus.CANCELLED));
        assertFalse(TicketStatus.canTransition(TicketStatus.CANCELLED, TicketStatus.CREATED));
    }

    @Test
    void canReopen_onlyResolvedOrClosed() {
        assertTrue(TicketStatus.canReopen(TicketStatus.RESOLVED));
        assertTrue(TicketStatus.canReopen(TicketStatus.CLOSED));

        assertFalse(TicketStatus.canReopen(TicketStatus.CREATED));
        assertFalse(TicketStatus.canReopen(TicketStatus.ASSIGNED));
        assertFalse(TicketStatus.canReopen(TicketStatus.IN_PROGRESS));
        assertFalse(TicketStatus.canReopen(TicketStatus.CANCELLED));
    }
}
