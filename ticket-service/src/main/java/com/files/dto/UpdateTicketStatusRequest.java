package com.files.dto;


import com.files.model.TicketStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTicketStatusRequest(
        @NotNull TicketStatus status
) {}
