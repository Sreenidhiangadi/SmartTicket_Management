package com.files.reports;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReportSummaryDtoTest {

    @Test
    void shouldCreateObjectUsingAllArgsConstructor() {
        ReportSummaryDto dto = new ReportSummaryDto(10L, 5L, 5L);

        assertEquals(10L, dto.getTotalTickets());
        assertEquals(5L, dto.getResolved());
        assertEquals(5L, dto.getInProgress());
    }

    @Test
    void shouldUpdateValuesUsingSetters() {
        ReportSummaryDto dto = new ReportSummaryDto(0L, 0L, 0L);

        dto.setTotalTickets(20L);
        dto.setResolved(12L);
        dto.setInProgress(8L);

        assertEquals(20L, dto.getTotalTickets());
        assertEquals(12L, dto.getResolved());
        assertEquals(8L, dto.getInProgress());
    }

    @Test
    void shouldSupportEqualsAndHashCode() {
        ReportSummaryDto dto1 = new ReportSummaryDto(10L, 5L, 5L);
        ReportSummaryDto dto2 = new ReportSummaryDto(10L, 5L, 5L);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void shouldReturnNonNullToString() {
        ReportSummaryDto dto = new ReportSummaryDto(10L, 5L, 5L);

        String result = dto.toString();

        assertNotNull(result);
        assertTrue(result.contains("totalTickets"));
        assertTrue(result.contains("resolved"));
        assertTrue(result.contains("inProgress"));
    }
}
