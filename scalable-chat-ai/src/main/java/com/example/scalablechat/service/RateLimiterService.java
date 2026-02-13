package com.example.scalablechat.service;

import reactor.core.publisher.Mono;

public interface RateLimiterService {
    Mono<Void> assertWithinLimit(String clientId);
}
