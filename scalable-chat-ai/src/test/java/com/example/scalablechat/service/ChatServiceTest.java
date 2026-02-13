package com.example.scalablechat.service;

import com.example.scalablechat.llm.LlmClient;
import com.example.scalablechat.model.ChatMessage;
import com.example.scalablechat.model.ChatRequest;
import com.example.scalablechat.model.ChatResponse;
import com.example.scalablechat.storage.ConversationStore;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class ChatServiceTest {

    @Test
    void chatShouldPersistConversationAndReturnReply() {
        ConversationStore store = new InMemoryConversationStore();
        LlmClient llmClient = new FixedLlmClient("hello back");
        RateLimiterService rateLimiterService = clientId -> Mono.empty();
        ChatService service = new ChatService(store, llmClient, rateLimiterService);

        Mono<ChatResponse> result = service.chat(
                new ChatRequest("s-1", "hello"),
                "client-1",
                "req-1"
        );

        StepVerifier.create(result)
                .assertNext(response -> {
                    if (!"s-1".equals(response.sessionId())) {
                        throw new AssertionError("Unexpected session ID");
                    }
                    if (!"hello back".equals(response.reply())) {
                        throw new AssertionError("Unexpected reply");
                    }
                })
                .verifyComplete();

        StepVerifier.create(store.getHistory("s-1").collectList())
                .assertNext(history -> {
                    if (history.size() != 2) {
                        throw new AssertionError("Expected user and assistant message");
                    }
                })
                .verifyComplete();
    }

    @Test
    void chatShouldFailWhenRateLimited() {
        ConversationStore store = new InMemoryConversationStore();
        LlmClient llmClient = new FixedLlmClient("ignored");
        RateLimiterService rateLimiterService =
                clientId -> Mono.error(new RateLimitExceededException("Rate limit exceeded"));
        ChatService service = new ChatService(store, llmClient, rateLimiterService);

        StepVerifier.create(service.chat(new ChatRequest("s-2", "hi"), "client-2", "req-2"))
                .expectError(RateLimitExceededException.class)
                .verify();
    }

    private static class FixedLlmClient implements LlmClient {
        private final String reply;

        private FixedLlmClient(String reply) {
            this.reply = reply;
        }

        @Override
        public Mono<String> complete(List<ChatMessage> history) {
            return Mono.just(reply);
        }

        @Override
        public Flux<String> stream(List<ChatMessage> history) {
            return Flux.just(reply);
        }
    }

    private static class InMemoryConversationStore implements ConversationStore {
        private final Map<String, List<ChatMessage>> data = new ConcurrentHashMap<>();

        @Override
        public Flux<ChatMessage> getHistory(String sessionId) {
            return Flux.fromIterable(data.getOrDefault(sessionId, List.of()));
        }

        @Override
        public Mono<Void> append(String sessionId, ChatMessage message) {
            data.computeIfAbsent(sessionId, ignored -> new ArrayList<>())
                    .add(new ChatMessage(message.role(), message.content(), Instant.now()));
            return Mono.empty();
        }
    }
}
