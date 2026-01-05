package com.files.messaging;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TicketMessagingEventsTest {

    @Test
    void ticketAssignedEvent_fullCoverage() {
        Instant now = Instant.now();

        TicketAssignedEvent e1 = new TicketAssignedEvent();
        e1.setTicketId("t1");
        e1.setAgentId("a1");
        e1.setAgentEmail("agent@test.com");
        e1.setAssignedAt(now);

        TicketAssignedEvent e2 = new TicketAssignedEvent();
        e2.setTicketId("t1");
        e2.setAgentId("a1");
        e2.setAgentEmail("agent@test.com");
        e2.setAssignedAt(now);

        assertEquals(e1, e2);               
        assertEquals(e1.hashCode(), e2.hashCode());
        assertNotEquals(e1, null);            
        assertNotEquals(e1, "string");          
        assertNotEquals(e1, new TicketAssignedEvent()); 

        assertNotNull(e1.toString());
    }

    @Test
    void ticketCreatedEvent_fullCoverage() {
        TicketCreatedEvent e1 = new TicketCreatedEvent();
        e1.setTicketId("t1");
        e1.setCreatedByUserId("u1");
        e1.setCreatedByEmail("user@test.com");
        e1.setTitle("Test");

        TicketCreatedEvent e2 = new TicketCreatedEvent();
        e2.setTicketId("t1");
        e2.setCreatedByUserId("u1");
        e2.setCreatedByEmail("user@test.com");
        e2.setTitle("Test");

        assertEquals(e1, e2);
        assertEquals(e1.hashCode(), e2.hashCode());
        assertNotEquals(e1, null);
        assertNotEquals(e1, "string");
        assertNotEquals(e1, new TicketCreatedEvent());

        assertNotNull(e1.toString());
    }

    @Test
    void ticketStatusChangedEvent_fullCoverage() {
        TicketStatusChangedEvent e1 = new TicketStatusChangedEvent();
        e1.setTicketId("t1");
        e1.setUserId("u1");
        e1.setUserEmail("user@test.com");
        e1.setOldStatus("ASSIGNED");
        e1.setNewStatus("IN_PROGRESS");

        TicketStatusChangedEvent e2 = new TicketStatusChangedEvent();
        e2.setTicketId("t1");
        e2.setUserId("u1");
        e2.setUserEmail("user@test.com");
        e2.setOldStatus("ASSIGNED");
        e2.setNewStatus("IN_PROGRESS");

        assertEquals(e1, e2);
        assertEquals(e1.hashCode(), e2.hashCode());
        assertNotEquals(e1, null);
        assertNotEquals(e1, "string");
        assertNotEquals(e1, new TicketStatusChangedEvent());

        assertNotNull(e1.toString());
    }
}
