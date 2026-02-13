package com.example.scalablechat.storage;

import com.example.scalablechat.config.AppProperties;
import com.example.scalablechat.model.ChatMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class RedisConversationStore implements ConversationStore {

    private static final String PREFIX = "chat:session:";
    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final int historyLimit;

    public RedisConversationStore(
            ReactiveStringRedisTemplate redisTemplate,
            ObjectMapper objectMapper,
            AppProperties properties
    ) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.historyLimit = properties.getChat().getHistoryLimit();
    }

    @Override
    public Flux<ChatMessage> getHistory(String sessionId) {
        return redisTemplate.opsForList()
                .range(key(sessionId), 0, -1)
                .flatMap(this::deserialize);
    }

    @Override
    public Mono<Void> append(String sessionId, ChatMessage message) {
        return serialize(message)
                .flatMap(json -> redisTemplate.opsForList().rightPush(key(sessionId), json))
                .then(redisTemplate.opsForList().trim(key(sessionId), -historyLimit, -1))
                .then();
    }

    private String key(String sessionId) {
        return PREFIX + sessionId;
    }

    private Mono<String> serialize(ChatMessage message) {
        try {
            return Mono.just(objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException ex) {
            return Mono.error(ex);
        }
    }

    private Mono<ChatMessage> deserialize(String json) {
        try {
            return Mono.just(objectMapper.readValue(json, ChatMessage.class));
        } catch (JsonProcessingException ex) {
            return Mono.error(ex);
        }
    }
}
