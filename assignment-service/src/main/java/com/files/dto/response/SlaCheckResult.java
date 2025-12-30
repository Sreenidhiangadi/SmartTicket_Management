package com.files.dto.response;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class SlaCheckResult {
    private String ticketId;
    private boolean breached;
    private Instant slaDueAt;
    private boolean escalated;
}