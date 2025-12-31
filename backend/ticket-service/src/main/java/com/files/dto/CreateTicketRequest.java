package com.files.dto;

import com.files.model.TicketCategory;
import com.files.model.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTicketRequest(

        @NotBlank
        String title,

        @NotBlank
        String description,

        @NotNull
        TicketCategory category,

        @NotNull
        TicketPriority priority


) {}
