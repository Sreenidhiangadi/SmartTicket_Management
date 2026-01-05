package com.files.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class EdgeCases {

    @Test
    void agentWorkload_edgeCases() {
        AgentWorkload w1 = new AgentWorkload("a1", 1);
        AgentWorkload w2 = new AgentWorkload("a2", 1);
        AgentWorkload w3 = new AgentWorkload(null, 0);

        assertNotEquals(w1, w2);
        assertNotEquals(w1, null);
        assertNotEquals(w1, "string");

        assertNotNull(w3.hashCode());
        assertNotNull(w3.toString());
    }


    @Test
    void assignment_edgeCases() {
        Instant now = Instant.now();

        Assignment a1 = Assignment.builder()
                .ticketId("t1")
                .agentId("a1")
                .priority("HIGH")
                .assignedAt(now)
                .slaDueAt(now)
                .escalated(true)
                .build();

        Assignment a2 = Assignment.builder()
                .ticketId("t2") 
                .build();

        Assignment a3 = Assignment.builder().build(); 

        assertNotEquals(a1, a2);
        assertNotEquals(a1, a3);
        assertNotEquals(a1, null);
        assertNotEquals(a1, "bad");

        assertNotNull(a3.hashCode());
        assertNotNull(a3.toString());
    }

    @Test
    void assignment_builder_fullAndEmpty() {
        Assignment.AssignmentBuilder builder =
                Assignment.builder();

        Assignment empty = builder.build();
        Assignment full = builder
                .ticketId("t1")
                .agentId("a1")
                .priority("LOW")
                .escalated(false)
                .build();

        assertNotNull(empty);
        assertNotNull(full);
    }


    @Test
    void escalationLog_edgeCases() {
        Instant now = Instant.now();

        EscalationLog e1 = EscalationLog.builder()
                .ticketId("t1")
                .agentId("a1")
                .reason("SLA")
                .escalatedAt(now)
                .build();

        EscalationLog e2 = EscalationLog.builder()
                .ticketId("t2")
                .build();

        EscalationLog e3 = EscalationLog.builder().build();

        assertNotEquals(e1, e2);
        assertNotEquals(e1, e3);
        assertNotEquals(e1, null);
        assertNotEquals(e1, 123);

        assertNotNull(e3.hashCode());
        assertNotNull(e3.toString());
    }

    @Test
    void escalationLog_builder_paths() {
        EscalationLog.EscalationLogBuilder builder =
                EscalationLog.builder();

        EscalationLog empty = builder.build();
        EscalationLog full = builder
                .ticketId("t1")
                .agentId("a1")
                .escalatedToManagerId("m1")
                .reason("AUTO")
                .build();

        assertNotNull(empty);
        assertNotNull(full);
    }
}
