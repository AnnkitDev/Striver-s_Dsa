package com.example.scalablechat.model;

public record ChatTokenEvent(
        String token,
        boolean done,
        String requestId
) {
}
