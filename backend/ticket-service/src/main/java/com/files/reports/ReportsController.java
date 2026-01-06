package com.files.reports;


import com.files.dto.*;
import com.files.reports.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportsController {

    private final ReportService reportService;

    @GetMapping("/tickets-by-status")
    public Flux<TicketsByStatusReport> ticketsByStatus() {
        return reportService.ticketsByStatus();
    }

    @GetMapping("/tickets-by-priority")
    public Flux<TicketsByPriorityReport> ticketsByPriority() {
        return reportService.ticketsByPriority();
    }

    @GetMapping("/avg-resolution-time")
    public Mono<AvgResolutionTimeReport> avgResolutionTime() {
        return reportService.avgResolutionTime();
    }

    @GetMapping("/sla-breaches")
    public Flux<SlaBreachReport> slaBreaches() {
        return reportService.slaBreaches();
    }

    @GetMapping("/summary")
    public Mono<ReportSummaryDto> summary() {
        return reportService.summary();
    }
}
