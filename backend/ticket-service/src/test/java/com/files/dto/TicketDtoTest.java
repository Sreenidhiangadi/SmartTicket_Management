package com.files.dto;

import com.files.model.TicketCategory;
import com.files.model.TicketPriority;
import com.files.model.TicketStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TicketDtoTest {

    @Test
    void addCommentRequest_record() {
        AddCommentRequest req = new AddCommentRequest("test comment");

        assertEquals("test comment", req.comment());
        assertNotNull(req.toString());
    }

    @Test
    void autoAssignmentResponse() {

    	 Instant now = Instant.now();

         AutoAssignmentResponse r1 = new AutoAssignmentResponse();
         r1.setTicketId("t1");
         r1.setAgentId("a1");
         r1.setPriority("HIGH");
         r1.setSlaDueAt(now);
         r1.setAgentEmail("agent@test.com");

         AutoAssignmentResponse r2 = new AutoAssignmentResponse();
         r2.setTicketId("t1");
         r2.setAgentId("a1");
         r2.setPriority("HIGH");
         r2.setSlaDueAt(now);
         r2.setAgentEmail("agent@test.com");

         assertEquals("t1", r1.getTicketId());
         assertEquals("a1", r1.getAgentId());
         assertEquals("HIGH", r1.getPriority());
         assertEquals(now, r1.getSlaDueAt());
         assertEquals("agent@test.com", r1.getAgentEmail());

         assertEquals(r1, r2);
         assertEquals(r1.hashCode(), r2.hashCode());

         assertNotEquals(r1, null);
         assertNotEquals(r1, "string");
         assertNotEquals(r1, new AutoAssignmentResponse());

         String value = r1.toString();
         assertNotNull(value);
         assertTrue(value.contains("ticketId"));
         assertTrue(value.contains("agentId"));
         assertTrue(value.contains("priority"));
         assertTrue(value.contains("slaDueAt"));
         assertTrue(value.contains("agentEmail"));
    }

    @Test
    void createTicketRequest_record() {
        CreateTicketRequest req = new CreateTicketRequest(
                "title",
                "desc",
                TicketCategory.SOFTWARE,
                TicketPriority.HIGH
        );

        assertEquals("title", req.title());
        assertEquals("desc", req.description());
        assertEquals(TicketCategory.SOFTWARE, req.category());
        assertEquals(TicketPriority.HIGH, req.priority());
        assertNotNull(req.toString());
    }

    @Test
    void slaBreachReport_record() {
        Instant now = Instant.now();

        SlaBreachReport report = new SlaBreachReport("t1", now, "IN_PROGRESS");

        assertEquals("t1", report.ticketId());
        assertEquals(now, report.slaDueAt());
        assertEquals("IN_PROGRESS", report.status());
        assertNotNull(report.toString());
    }

    @Test
    void ticketCommentResponse_record() {
        Instant now = Instant.now();

        TicketCommentResponse resp = new TicketCommentResponse(
                "c1",
                "comment",
                "user1",
                "USER",
                now
        );

        assertEquals("c1", resp.id());
        assertEquals("comment", resp.comment());
        assertEquals("user1", resp.commentedBy());
        assertEquals("USER", resp.role());
        assertEquals(now, resp.commentedAt());
        assertNotNull(resp.toString());
    }

    @Test
    void ticketResponse_record() {
        Instant now = Instant.now();

        TicketResponse resp = new TicketResponse(
                "t1",
                "title",
                "desc",
                TicketCategory.SOFTWARE,
                TicketPriority.MEDIUM,
                TicketStatus.CREATED,
                "user1",
                "agent1",
                now,
                now,
                null,
                null,
                now,
                false
        );

        assertEquals("t1", resp.id());
        assertEquals(TicketStatus.CREATED, resp.status());
        assertFalse(resp.slaBreached());
        assertNotNull(resp.toString());
    }

    @Test
    void timelineItemResponse_record() {
        Instant now = Instant.now();

        TimelineItemResponse item = new TimelineItemResponse(
                "HISTORY",
                "CREATED",
                "user1",
                "ticket created",
                now
        );

        assertEquals("HISTORY", item.type());
        assertEquals("CREATED", item.action());
        assertEquals("user1", item.performedBy());
        assertEquals("ticket created", item.message());
        assertEquals(now, item.timestamp());
        assertNotNull(item.toString());
    }

    @Test
    void updateTicketStatusRequest_record() {
        UpdateTicketStatusRequest req =
                new UpdateTicketStatusRequest(TicketStatus.RESOLVED);

        assertEquals(TicketStatus.RESOLVED, req.status());
        assertNotNull(req.toString());
    }
}
