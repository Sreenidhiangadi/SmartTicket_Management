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

        assertEquals(h1, h2);

        assertEquals(h1.hashCode(), h2.hashCode());

        assertNotEquals(h1, null);
        assertNotEquals(h1, "string");
        assertNotEquals(h1, new TicketHistory());

        assertNotNull(h1.toString());
        TicketHistory h3 = TicketHistory.builder()
                .id("1")
                .ticketId("t1")
                .action(TicketHistoryAction.CREATED)
                .performedBy("user1")
                .description("created")
                .createdAt(now)
                .build();

        assertEquals(h1, h3);
    }
}
