package com.example.scalablechat.storage;

import com.example.scalablechat.model.ChatMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ConversationStore {
    Flux<ChatMessage> getHistory(String sessionId);

    Mono<Void> append(String sessionId, ChatMessage message);
}
