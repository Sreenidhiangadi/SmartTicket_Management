package com.files.dto.response;

import java.time.Instant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssignmentResponse {

	private String ticketId;
	private String agentId;
	private String priority;

	private Instant assignedAt;
	private Instant slaDueAt;

	private boolean escalated;
}
