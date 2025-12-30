package com.files.dto.request;

import java.time.Instant;

import lombok.Data;

@Data
public class SlaCheckRequest {
    private boolean dryRun;
}
