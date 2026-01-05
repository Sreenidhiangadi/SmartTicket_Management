package com.files.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AgentWorkloadTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        AgentWorkload w = new AgentWorkload();

        w.setAgentId("a1");
        w.setActiveTickets(5);

        assertEquals("a1", w.getAgentId());
        assertEquals(5, w.getActiveTickets());
    }

    @Test
    void testAllArgsConstructor() {
        AgentWorkload w = new AgentWorkload("a2", 3);

        assertEquals("a2", w.getAgentId());
        assertEquals(3, w.getActiveTickets());
    }

    @Test
    void testEqualsHashCodeToString() {
        AgentWorkload w1 = new AgentWorkload("a1", 2);
        AgentWorkload w2 = new AgentWorkload("a1", 2);

        assertEquals(w1, w2);
        assertEquals(w1.hashCode(), w2.hashCode());
        assertNotNull(w1.toString());
    }
}
