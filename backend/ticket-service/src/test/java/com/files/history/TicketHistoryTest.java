package com.files.history;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TicketHistoryTest {

    @Test
    void ticketHistory_fullLombokCoverage() {
    	 Instant now = Instant.now();

         TicketHistory h1 = new TicketHistory();
         h1.setId("1");
         h1.setTicketId("t1");
         h1.setAction(TicketHistoryAction.CREATED);
         h1.setPerformedBy("user1");
         h1.setDescription("created");
         h1.setCreatedAt(now);

         TicketHistory h2 = new TicketHistory(
                 "1",
                 "t1",
                 TicketHistoryAction.CREATED,
                 "user1",
                 "created",
                 now
         );

         TicketHistory h3 = TicketHistory.builder()
                 .id("1")
                 .ticketId("t1")
                 .action(TicketHistoryAction.CREATED)
                 .performedBy("user1")
                 .description("created")
                 .createdAt(now)
                 .build();

         assertEquals("1", h1.getId());
         assertEquals("t1", h1.getTicketId());
         assertEquals(TicketHistoryAction.CREATED, h1.getAction());
         assertEquals("user1", h1.getPerformedBy());
         assertEquals("created", h1.getDescription());
         assertEquals(now, h1.getCreatedAt());

         assertEquals(h1, h2);
         assertEquals(h1, h3);
         assertEquals(h1.hashCode(), h2.hashCode());

         assertNotEquals(h1, null);
         assertNotEquals(h1, "string");
         assertNotEquals(h1, new TicketHistory());

         String value = h1.toString();
         assertNotNull(value);
         assertTrue(value.contains("id"));
         assertTrue(value.contains("ticketId"));
         assertTrue(value.contains("action"));
         assertTrue(value.contains("performedBy"));
         assertTrue(value.contains("description"));
         assertTrue(value.contains("createdAt"));
    }
}
