package com.files.dashboard;

import reactor.core.publisher.Mono;

public interface DashboardService {

    Mono<DashboardSummaryResponse> getSummary();
}
