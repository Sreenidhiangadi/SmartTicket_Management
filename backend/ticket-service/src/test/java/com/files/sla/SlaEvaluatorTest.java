package com.files.sla;

import com.files.model.Ticket;
import com.files.model.TicketPriority;
import com.files.model.TicketStatus;
import org.junit.jupiter.api.Test;

import java.time.Duration;
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
    void shouldNotBreach_whenTicketIsResolvedEvenIfPastDue() {
        Ticket ticket = new Ticket();
        ticket.setSlaDueAt(Instant.now().minusSeconds(3600));
        ticket.setStatus(TicketStatus.RESOLVED);

        assertFalse(SlaEvaluator.isBreached(ticket));
    }

    @Test
    void shouldReturnCorrectDurationForEachPriority() {
        assertEquals(Duration.ofHours(1), SlaPolicy.getDuration(TicketPriority.CRITICAL));
        assertEquals(Duration.ofHours(4), SlaPolicy.getDuration(TicketPriority.HIGH));
        assertEquals(Duration.ofHours(8), SlaPolicy.getDuration(TicketPriority.MEDIUM));
        assertEquals(Duration.ofHours(24), SlaPolicy.getDuration(TicketPriority.LOW));
    }
    @Test
    void shouldNotBreach_whenSlaDueAtIsInFuture() {
        Ticket ticket = new Ticket();
        ticket.setSlaDueAt(Instant.now().plusSeconds(3600));
        ticket.setStatus(TicketStatus.CREATED);

        assertFalse(SlaEvaluator.isBreached(ticket));
    }
    @Test
    void getDuration_shouldReturnNonNullForAllPriorities() {
        for (TicketPriority priority : TicketPriority.values()) {
            assertNotNull(SlaPolicy.getDuration(priority));
        }
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
