package com.files.dto;

import jakarta.validation.constraints.NotBlank;

public record AddCommentRequest(
        @NotBlank
        String comment
) {}
