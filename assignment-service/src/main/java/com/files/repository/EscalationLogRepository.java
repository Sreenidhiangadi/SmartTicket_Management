package com.files.repository;

import com.files.model.EscalationLog;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface EscalationLogRepository
        extends ReactiveMongoRepository<EscalationLog, String> {

    Flux<EscalationLog> findByEscalatedToManagerId(String managerId);
}
