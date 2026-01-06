package com.files.reports;

import com.files.dto.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReportService {

    Flux<TicketsByStatusReport> ticketsByStatus();

    Flux<TicketsByPriorityReport> ticketsByPriority();

    Mono<AvgResolutionTimeReport> avgResolutionTime();

    Flux<SlaBreachReport> slaBreaches();
    
    Mono<ReportSummaryDto> summary();

}
