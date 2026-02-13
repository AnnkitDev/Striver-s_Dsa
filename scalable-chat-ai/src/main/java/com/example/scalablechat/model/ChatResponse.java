package com.example.scalablechat.model;

import java.time.Instant;

public record ChatResponse(
        String sessionId,
        String reply,
        String requestId,
        Instant timestamp
) {
}
