package com.example.scalablechat.llm;

import com.example.scalablechat.config.AppProperties;
import com.example.scalablechat.model.ChatMessage;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ConfiguredLlmClient implements LlmClient {

    private final WebClient webClient;
    private final AppProperties properties;

    public ConfiguredLlmClient(WebClient llmWebClient, AppProperties properties) {
        this.webClient = llmWebClient;
        this.properties = properties;
    }

    @Override
    public Mono<String> complete(List<ChatMessage> history) {
        String provider = properties.getLlm().getProvider();
        if ("mock".equalsIgnoreCase(provider)) {
            return Mono.just(mockReply(history));
        }

        List<Map<String, String>> messages = history.stream()
                .map(msg -> Map.of("role", msg.role(), "content", msg.content()))
                .collect(Collectors.toList());

        Map<String, Object> payload = Map.of(
                "model", properties.getLlm().getModel(),
                "messages", messages,
                "temperature", 0.4
        );

        return webClient.post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getLlm().getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(this::extractContent)
                .timeout(Duration.ofSeconds(properties.getLlm().getTimeoutSeconds()))
                .onErrorResume(ex -> Mono.just("Temporary upstream issue. Please retry."));
    }

    @Override
    public Flux<String> stream(List<ChatMessage> history) {
        return complete(history)
                .flatMapMany(reply -> Flux.fromArray(reply.split("\\s+"))
                        .filter(token -> !token.isBlank())
                        .map(token -> token + " "));
    }

    private String extractContent(JsonNode root) {
        JsonNode content = root.path("choices")
                .path(0)
                .path("message")
                .path("content");
        if (content.isMissingNode() || content.asText().isBlank()) {
            return "No response generated.";
        }
        return content.asText();
    }

    private String mockReply(List<ChatMessage> history) {
        String lastUserMessage = history.stream()
                .filter(message -> "user".equalsIgnoreCase(message.role()))
                .reduce((first, second) -> second)
                .map(ChatMessage::content)
                .orElse("hello");
        return "Scalable AI response: " + lastUserMessage;
    }
}
