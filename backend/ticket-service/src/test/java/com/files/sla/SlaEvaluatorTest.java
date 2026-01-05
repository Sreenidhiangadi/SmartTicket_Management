package com.files.sla;

import com.files.model.Ticket;
import com.files.model.TicketStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class SlaEvaluatorTest {

    @Test
    void shouldNotBreach_whenSlaDueAtIsNull() {
        Ticket ticket = new Ticket();
        ticket.setSlaDueAt(null);
        ticket.setStatus(TicketStatus.CREATED);

        assertFalse(SlaEvaluator.isBreached(ticket));
    }

    @Test
    void shouldNotBreach_whenSlaDueAtIsInFuture() {
        Ticket ticket = new Ticket();
        ticket.setSlaDueAt(Instant.now().plusSeconds(3600));
        ticket.setStatus(TicketStatus.CREATED);

        assertFalse(SlaEvaluator.isBreached(ticket));
    }

    @Test
    void shouldNotBreach_whenTicketIsClosedEvenIfPastDue() {
        Ticket ticket = new Ticket();
        ticket.setSlaDueAt(Instant.now().minusSeconds(3600));
        ticket.setStatus(TicketStatus.CLOSED);

        assertFalse(SlaEvaluator.isBreached(ticket));
    }

    @Test
    void shouldBreach_whenPastDueAndNotClosed() {
        Ticket ticket = new Ticket();
        ticket.setSlaDueAt(Instant.now().minusSeconds(3600));
        ticket.setStatus(TicketStatus.IN_PROGRESS);

        assertTrue(SlaEvaluator.isBreached(ticket));
    }
}
