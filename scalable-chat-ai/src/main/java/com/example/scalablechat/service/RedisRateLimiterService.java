package com.example.scalablechat.service;

import com.example.scalablechat.config.AppProperties;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@Component
public class RedisRateLimiterService implements RateLimiterService {

    private static final String PREFIX = "chat:rl:";
    private final ReactiveStringRedisTemplate redisTemplate;
    private final int perMinuteLimit;

    public RedisRateLimiterService(ReactiveStringRedisTemplate redisTemplate, AppProperties properties) {
        this.redisTemplate = redisTemplate;
        this.perMinuteLimit = properties.getChat().getPerMinuteLimit();
    }

    @Override
    public Mono<Void> assertWithinLimit(String clientId) {
        String key = PREFIX + clientId + ":" + Instant.now().getEpochSecond() / 60;
        return redisTemplate.opsForValue()
                .increment(key)
                .flatMap(count -> redisTemplate.expire(key, Duration.ofMinutes(2))
                        .thenReturn(count))
                .handle((count, sink) -> {
                    if (count > perMinuteLimit) {
                        sink.error(new RateLimitExceededException("Rate limit exceeded"));
                        return;
                    }
                    sink.next(count);
                })
                .then();
    }
}
