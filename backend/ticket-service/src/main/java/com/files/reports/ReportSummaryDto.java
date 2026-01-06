package com.files.reports;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportSummaryDto {
    private long totalTickets;
    private long resolved;
    private long inProgress;
}
