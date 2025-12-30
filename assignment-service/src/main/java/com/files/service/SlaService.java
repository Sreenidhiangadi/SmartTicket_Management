package com.files.service;

import reactor.core.publisher.Flux;
import com.files.dto.response.SlaCheckResult;
import com.files.model.AgentWorkload;

public interface SlaService {

    Flux<SlaCheckResult> checkSla(boolean dryRun);

    Flux<AgentWorkload> calculateWorkload();
}
