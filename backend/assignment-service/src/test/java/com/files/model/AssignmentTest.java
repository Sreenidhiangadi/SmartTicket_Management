package com.files.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class AssignmentTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        Assignment a = new Assignment();

        Instant now = Instant.now();

        a.setId("1");
        a.setTicketId("t1");
        a.setAgentId("a1");
        a.setPriority("HIGH");
        a.setAssignedAt(now);
        a.setSlaDueAt(now.plusSeconds(3600));
        a.setEscalated(true);

        assertEquals("1", a.getId());
        assertEquals("t1", a.getTicketId());
        assertEquals("a1", a.getAgentId());
        assertEquals("HIGH", a.getPriority());
        assertTrue(a.isEscalated());
    }

    @Test
    void testAllArgsConstructor() {
        Instant now = Instant.now();

        Assignment a = new Assignment(
                "1",
                "t1",
                "a1",
                "MEDIUM",
                now,
                now.plusSeconds(3600),
                false
        );

        assertEquals("t1", a.getTicketId());
        assertFalse(a.isEscalated());
    }

    @Test
    void testBuilder() {
        Assignment a = Assignment.builder()
                .ticketId("t2")
                .agentId("a2")
                .priority("LOW")
                .escalated(false)
                .build();

        assertEquals("t2", a.getTicketId());
        assertEquals("a2", a.getAgentId());
        assertEquals("LOW", a.getPriority());
        assertFalse(a.isEscalated());
    }

    @Test
    void testEqualsHashCodeToString() {
        Assignment a1 = Assignment.builder().ticketId("t1").build();
        Assignment a2 = Assignment.builder().ticketId("t1").build();

        assertEquals(a1, a2);
        assertEquals(a1.hashCode(), a2.hashCode());
        assertNotNull(a1.toString());
    }
}