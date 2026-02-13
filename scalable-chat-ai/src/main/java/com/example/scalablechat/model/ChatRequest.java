package com.example.scalablechat.model;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(
        @NotBlank(message = "sessionId is required")
        String sessionId,
        @NotBlank(message = "message is required")
        String message
) {
}
