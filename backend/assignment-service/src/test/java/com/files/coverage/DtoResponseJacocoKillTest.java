package com.files.coverage;

import org.junit.jupiter.api.Test;

import com.files.dto.response.AgentWorkloadResponse;
import com.files.dto.response.AssignmentResponse;
import com.files.dto.response.AutoAssignmentResponse;
import com.files.dto.response.EscalationResponse;
import com.files.dto.response.SlaCheckResult;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class DtoResponseJacocoKillTest {

    @Test
    void assignmentResponse_allPaths() {
        Instant now = Instant.now();

        AssignmentResponse r1 = AssignmentResponse.builder()
                .ticketId("t1")
                .agentId("a1")
                .priority("HIGH")
                .assignedAt(now)
                .slaDueAt(now)
                .escalated(true)
                .build();

        AssignmentResponse r2 = AssignmentResponse.builder()
                .ticketId("t1")
                .agentId("a1")
                .priority("HIGH")
                .assignedAt(now)
                .slaDueAt(now)
                .escalated(true)
                .build();

        AssignmentResponse r3 = AssignmentResponse.builder().build();

        assertEquals(r1, r1);
        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertNotEquals(r1, null);
        assertNotEquals(r1, "x");

        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotNull(r1.toString());
        assertNotNull(r3.toString());
    }

    @Test
    void escalationResponse_allPaths() {
        Instant now = Instant.now();

        EscalationResponse e1 = EscalationResponse.builder()
                .ticketId("t1")
                .agentId("a1")
                .escalatedToManagerId("m1")
                .reason("SLA")
                .escalatedAt(now)
                .build();

        EscalationResponse e2 = EscalationResponse.builder()
                .ticketId("t1")
                .agentId("a1")
                .escalatedToManagerId("m1")
                .reason("SLA")
                .escalatedAt(now)
                .build();

        EscalationResponse e3 = EscalationResponse.builder().build();

        assertEquals(e1, e1);
        assertEquals(e1, e2);
        assertNotEquals(e1, e3);
        assertNotEquals(e1, null);
        assertNotEquals(e1, 123);

        assertEquals(e1.hashCode(), e2.hashCode());
        assertNotNull(e1.toString());
        assertNotNull(e3.toString());
    }

    @Test
    void autoAssignmentResponse_allPaths() {
        Instant now = Instant.now();

        AutoAssignmentResponse a1 = AutoAssignmentResponse.builder()
                .ticketId("t1")
                .agentId("a1")
                .priority("MEDIUM")
                .slaDueAt(now)
                .build();

        AutoAssignmentResponse a2 = AutoAssignmentResponse.builder()
                .ticketId("t1")
                .agentId("a1")
                .priority("MEDIUM")
                .slaDueAt(now)
                .build();

        AutoAssignmentResponse a3 = AutoAssignmentResponse.builder().build();

        assertEquals(a1, a1);
        assertEquals(a1, a2);
        assertNotEquals(a1, a3);
        assertNotEquals(a1, null);
        assertNotEquals(a1, new Object());

        assertEquals(a1.hashCode(), a2.hashCode());
        assertNotNull(a1.toString());
        assertNotNull(a3.toString());
    }

    @Test
    void slaCheckResult_allPaths() {
        Instant now = Instant.now();

        SlaCheckResult s1 = new SlaCheckResult("t1", true, now, true);
        SlaCheckResult s2 = new SlaCheckResult("t1", true, now, true);
        SlaCheckResult s3 = new SlaCheckResult(null, false, null, false);

        assertEquals(s1, s1);
        assertEquals(s1, s2);
        assertNotEquals(s1, s3);
        assertNotEquals(s1, null);
        assertNotEquals(s1, "bad");

        assertEquals(s1.hashCode(), s2.hashCode());
        assertNotNull(s1.toString());
        assertNotNull(s3.toString());
    }

    @Test
    void agentWorkloadResponse_allPaths() {
        AgentWorkloadResponse w1 = new AgentWorkloadResponse("a1", 5);
        AgentWorkloadResponse w2 = new AgentWorkloadResponse("a1", 5);
        AgentWorkloadResponse w3 = new AgentWorkloadResponse(null, 0);

        assertEquals(w1, w1);
        assertEquals(w1, w2);
        assertNotEquals(w1, w3);
        assertNotEquals(w1, null);
        assertNotEquals(w1, "x");

        assertEquals(w1.hashCode(), w2.hashCode());
        assertNotNull(w1.toString());
        assertNotNull(w3.toString());
    }
}
