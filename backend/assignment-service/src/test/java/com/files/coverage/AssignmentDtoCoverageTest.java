package com.files.coverage;

import com.files.dto.AgentDto;
import com.files.dto.request.AssignTicketRequest;
import com.files.dto.request.SlaCheckRequest;
import com.files.dto.response.*;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class AssignmentDtoCoverageTest {

    @Test
    void agentDto_fullCoverage() {
        AgentDto a1 = new AgentDto();
        a1.setId("a1");
        a1.setActive(true);

        AgentDto a2 = new AgentDto();
        a2.setId("a1");
        a2.setActive(true);

        assertEquals("a1", a1.getId());
        assertTrue(a1.isActive());

        assertEquals(a1, a2);
        assertEquals(a1.hashCode(), a2.hashCode());
        assertNotNull(a1.toString());
    }

    @Test
    void assignTicketRequest_fullCoverage() {
        AssignTicketRequest r1 = new AssignTicketRequest();
        r1.setAgentId("agent1");
        r1.setPriority("HIGH");

        AssignTicketRequest r2 = new AssignTicketRequest();
        r2.setAgentId("agent1");
        r2.setPriority("HIGH");

        assertEquals("agent1", r1.getAgentId());
        assertEquals("HIGH", r1.getPriority());
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotNull(r1.toString());
    }

    @Test
    void slaCheckRequest_fullCoverage() {
        SlaCheckRequest r1 = new SlaCheckRequest();
        r1.setDryRun(true);

        SlaCheckRequest r2 = new SlaCheckRequest();
        r2.setDryRun(true);

        assertTrue(r1.isDryRun());
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotNull(r1.toString());
    }

    @Test
    void agentWorkloadResponse_fullCoverage() {
        AgentWorkloadResponse r1 =
                new AgentWorkloadResponse("agent1", 5L);

        AgentWorkloadResponse r2 =
                new AgentWorkloadResponse("agent1", 5L);

        assertEquals("agent1", r1.getAgentId());
        assertEquals(5L, r1.getActiveTickets());
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotNull(r1.toString());
    }

    @Test
    void assignmentResponse_fullCoverage() {
        Instant now = Instant.now();

        AssignmentResponse r1 =
                AssignmentResponse.builder()
                        .ticketId("t1")
                        .agentId("a1")
                        .priority("MEDIUM")
                        .assignedAt(now)
                        .slaDueAt(now.plusSeconds(10))
                        .escalated(false)
                        .build();

        AssignmentResponse r2 =
                AssignmentResponse.builder()
                        .ticketId("t1")
                        .agentId("a1")
                        .priority("MEDIUM")
                        .assignedAt(now)
                        .slaDueAt(now.plusSeconds(10))
                        .escalated(false)
                        .build();

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotNull(r1.toString());
    }

    @Test
    void autoAssignmentResponse_fullCoverage() {
        Instant now = Instant.now();

        AutoAssignmentResponse r1 =
                AutoAssignmentResponse.builder()
                        .ticketId("t1")
                        .agentId("a1")
                        .priority("HIGH")
                        .slaDueAt(now)
                        .build();

        AutoAssignmentResponse r2 =
                AutoAssignmentResponse.builder()
                        .ticketId("t1")
                        .agentId("a1")
                        .priority("HIGH")
                        .slaDueAt(now)
                        .build();

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotNull(r1.toString());
    }

    @Test
    void escalationResponse_fullCoverage() {
        Instant now = Instant.now();

        EscalationResponse r1 =
                EscalationResponse.builder()
                        .ticketId("t1")
                        .agentId("a1")
                        .escalatedToManagerId("m1")
                        .reason("SLA")
                        .escalatedAt(now)
                        .build();

        EscalationResponse r2 =
                EscalationResponse.builder()
                        .ticketId("t1")
                        .agentId("a1")
                        .escalatedToManagerId("m1")
                        .reason("SLA")
                        .escalatedAt(now)
                        .build();

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotNull(r1.toString());
    }
    @Test
    void agentDto_equals_allBranches() {
        AgentDto a1 = new AgentDto();
        a1.setId("a1");
        a1.setActive(true);

        AgentDto a2 = new AgentDto();
        a2.setId("a1");
        a2.setActive(true);

        AgentDto a3 = new AgentDto();
        a3.setId("a2");        
        a3.setActive(true);

        AgentDto a4 = new AgentDto();
        a4.setId("a1");
        a4.setActive(false);
        assertEquals(a1, a1);
        assertEquals(a1, a2);
        assertNotEquals(a1, a3);
        assertNotEquals(a1, a4);
        assertNotEquals(a1, null);
        assertNotEquals(a1, "not-an-agent");
        assertEquals(a1.hashCode(), a2.hashCode());
        assertNotNull(a1.toString());
    }

    @Test
    void slaCheckResult_fullCoverage() {
        Instant now = Instant.now();

        SlaCheckResult r1 =
                new SlaCheckResult("t1", true, now, true);

        SlaCheckResult r2 =
                new SlaCheckResult("t1", true, now, true);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotNull(r1.toString());
        assertTrue(r1.isBreached());
        assertTrue(r1.isEscalated());
    }
}
