package com.example.scalablechat.service;

import com.example.scalablechat.llm.LlmClient;
import com.example.scalablechat.model.ChatMessage;
import com.example.scalablechat.model.ChatRequest;
import com.example.scalablechat.model.ChatResponse;
import com.example.scalablechat.storage.ConversationStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

    private final ConversationStore conversationStore;
    private final LlmClient llmClient;
    private final RateLimiterService rateLimiterService;

    public ChatService(
            ConversationStore conversationStore,
            LlmClient llmClient,
            RateLimiterService rateLimiterService
    ) {
        this.conversationStore = conversationStore;
        this.llmClient = llmClient;
        this.rateLimiterService = rateLimiterService;
    }

    public Mono<ChatResponse> chat(ChatRequest request, String clientId, String requestId) {
        ChatMessage userMessage = new ChatMessage("user", request.message(), Instant.now());
        return rateLimiterService.assertWithinLimit(clientId)
                .then(conversationStore.getHistory(request.sessionId()).collectList())
                .flatMap(history -> {
                    List<ChatMessage> context = new ArrayList<>(history);
                    context.add(userMessage);
                    return conversationStore.append(request.sessionId(), userMessage)
                            .then(llmClient.complete(context))
                            .flatMap(reply -> persistAndBuildReply(request.sessionId(), reply, requestId));
                });
    }

    public Flux<String> stream(ChatRequest request, String clientId) {
        ChatMessage userMessage = new ChatMessage("user", request.message(), Instant.now());
        return rateLimiterService.assertWithinLimit(clientId)
                .thenMany(conversationStore.getHistory(request.sessionId()).collectList())
                .flatMapMany(history -> {
                    List<ChatMessage> context = new ArrayList<>(history);
                    context.add(userMessage);
                    return conversationStore.append(request.sessionId(), userMessage)
                            .then(llmClient.complete(context))
                            .flatMapMany(reply -> {
                                ChatMessage assistant = new ChatMessage("assistant", reply, Instant.now());
                                return conversationStore.append(request.sessionId(), assistant)
                                        .thenMany(tokenize(reply));
                            });
                });
    }

    private Mono<ChatResponse> persistAndBuildReply(String sessionId, String reply, String requestId) {
        ChatMessage assistantMessage = new ChatMessage("assistant", reply, Instant.now());
        return conversationStore.append(sessionId, assistantMessage)
                .thenReturn(new ChatResponse(sessionId, reply, requestId, Instant.now()));
    }

    private Flux<String> tokenize(String reply) {
        return Flux.fromArray(reply.split("\\s+"))
                .filter(token -> !token.isBlank())
                .map(token -> token + " ");
    }
}
