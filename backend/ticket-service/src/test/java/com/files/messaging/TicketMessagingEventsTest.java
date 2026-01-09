package com.files.messaging;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TicketMessagingEventsTest {

    @Test
    void ticketAssignedEvent() {
    	 Instant now = Instant.now();

         TicketAssignedEvent e1 = new TicketAssignedEvent();
         e1.setTicketId("t1");
         e1.setAgentId("a1");
         e1.setAssignedAt(now);

         TicketAssignedEvent e2 = new TicketAssignedEvent();
         e2.setTicketId("t1");
         e2.setAgentId("a1");
         e2.setAssignedAt(now);

         assertEquals("t1", e1.getTicketId());
         assertEquals("a1", e1.getAgentId());
         assertEquals(now, e1.getAssignedAt());

         assertEquals(e1, e2);
         assertEquals(e1.hashCode(), e2.hashCode());

         assertNotEquals(e1, null);
         assertNotEquals(e1, "string");
         assertNotEquals(e1, new TicketAssignedEvent());

         String value = e1.toString();
         assertNotNull(value);
         assertTrue(value.contains("ticketId"));
         assertTrue(value.contains("agentId"));
         assertTrue(value.contains("agentEmail"));
         assertTrue(value.contains("assignedAt"));
    }

    @Test
    void ticketCreatedEvent() {
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

          assertEquals("t1", e1.getTicketId());
          assertEquals("u1", e1.getCreatedByUserId());
          assertEquals("user@test.com", e1.getCreatedByEmail());
          assertEquals("Test", e1.getTitle());

          assertEquals(e1, e2);
          assertEquals(e1.hashCode(), e2.hashCode());

          assertNotEquals(e1, null);
          assertNotEquals(e1, "string");
          assertNotEquals(e1, new TicketCreatedEvent());

          String value = e1.toString();
          assertNotNull(value);
          assertTrue(value.contains("ticketId"));
          assertTrue(value.contains("createdByUserId"));
          assertTrue(value.contains("createdByEmail"));
          assertTrue(value.contains("title"));
    }

    @Test
    void ticketStatusChangedEvent() {
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

         assertEquals("t1", e1.getTicketId());
         assertEquals("u1", e1.getUserId());
         assertEquals("user@test.com", e1.getUserEmail());
         assertEquals("ASSIGNED", e1.getOldStatus());
         assertEquals("IN_PROGRESS", e1.getNewStatus());

         assertEquals(e1, e2);
         assertEquals(e1.hashCode(), e2.hashCode());

         assertNotEquals(e1, null);
         assertNotEquals(e1, "string");
         assertNotEquals(e1, new TicketStatusChangedEvent());

         String value = e1.toString();
         assertNotNull(value);
         assertTrue(value.contains("ticketId"));
         assertTrue(value.contains("userId"));
         assertTrue(value.contains("userEmail"));
         assertTrue(value.contains("oldStatus"));
         assertTrue(value.contains("newStatus"));
    }
}
