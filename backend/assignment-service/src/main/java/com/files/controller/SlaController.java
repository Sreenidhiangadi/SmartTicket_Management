package com.files.controller;

import com.files.dto.request.SlaCheckRequest;
import com.files.dto.response.AgentWorkloadResponse;
import com.files.dto.response.SlaCheckResult;
import com.files.service.SlaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/sla")
@RequiredArgsConstructor
public class SlaController {

    private final SlaService slaService;

    @PostMapping("/check")
    public Flux<SlaCheckResult> checkSla(
            @Valid @RequestBody SlaCheckRequest request
    ) {
        return slaService.checkSla(request.isDryRun());
    }

    @GetMapping("/workload")
    public Flux<AgentWorkloadResponse> workload() {
        return slaService.calculateWorkload()
                .map(w ->
                        new AgentWorkloadResponse(
                                w.getAgentId(),
                                w.getActiveTickets()
                        )
                );
    }
}
