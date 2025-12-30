package com.files.dto;

import java.time.Instant;

public record TimelineItemResponse(
        String type,         
        String action,        
        String performedBy,   
        String message,
        Instant timestamp
) {}
