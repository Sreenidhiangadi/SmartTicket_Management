package com.files.service;
import com.files.dto.response.AutoAssignmentResponse;
import com.files.model.Assignment;
import reactor.core.publisher.Mono;

public interface AssignmentService {

    Mono<Assignment> assignTicket(String ticketId, String agentId, String priority);
    
    Mono<Assignment> escalate(Assignment assignment, String reason);
    
    Mono<AutoAssignmentResponse> autoAssign(String ticketId,String priority);

}
