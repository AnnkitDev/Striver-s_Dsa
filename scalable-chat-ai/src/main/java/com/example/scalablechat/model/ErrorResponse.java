package com.example.scalablechat.model;

import java.time.Instant;

public record ErrorResponse(
        String requestId,
        String message,
        Instant timestamp
) {
}
