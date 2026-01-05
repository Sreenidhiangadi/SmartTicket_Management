package com.files.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class EscalationLogTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        EscalationLog log = new EscalationLog();

        Instant now = Instant.now();

        log.setId("1");
        log.setTicketId("t1");
        log.setAgentId("a1");
        log.setEscalatedToManagerId("m1");
        log.setReason("SLA_BREACH");
        log.setEscalatedAt(now);

        assertEquals("t1", log.getTicketId());
        assertEquals("a1", log.getAgentId());
        assertEquals("m1", log.getEscalatedToManagerId());
        assertEquals("SLA_BREACH", log.getReason());
        assertEquals(now, log.getEscalatedAt());
    }
    @Test
    void testBuilder_id_and_toString() {
        EscalationLog.EscalationLogBuilder builder =
                EscalationLog.builder()
                        .id("id1")
                        .ticketId("t1")
                        .agentId("a1");

        EscalationLog log = builder.build();

        assertEquals("id1", log.getId());
        assertNotNull(builder.toString()); 
    }

    @Test
    void testAllArgsConstructor() {
        Instant now = Instant.now();

        EscalationLog log = new EscalationLog(
                "1",
                "t1",
                "a1",
                "m1",
                "AUTO",
                now
        );

        assertEquals("AUTO", log.getReason());
    }

    @Test
    void testBuilder() {
        EscalationLog log = EscalationLog.builder()
                .ticketId("t2")
                .agentId("a2")
                .reason("TEST")
                .build();

        assertEquals("t2", log.getTicketId());
        assertEquals("a2", log.getAgentId());
    }

    @Test
    void testEqualsHashCodeToString() {
        EscalationLog l1 = EscalationLog.builder().ticketId("t1").build();
        EscalationLog l2 = EscalationLog.builder().ticketId("t1").build();

        assertEquals(l1, l2);
        assertEquals(l1.hashCode(), l2.hashCode());
        assertNotNull(l1.toString());
    }
}
