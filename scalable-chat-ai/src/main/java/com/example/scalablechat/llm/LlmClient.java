package com.example.scalablechat.llm;

import com.example.scalablechat.model.ChatMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface LlmClient {
    Mono<String> complete(List<ChatMessage> history);

    Flux<String> stream(List<ChatMessage> history);
}
