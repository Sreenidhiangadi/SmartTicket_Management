package com.files;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class TicketServiceApplicationTest {

    @Test
    void contextLoads() {
        // verifies Spring context starts
    }

    @Test
    void mainMethodRuns() {
        assertDoesNotThrow(() ->
                TicketServiceApplication.main(new String[] {})
        );
    }
}
