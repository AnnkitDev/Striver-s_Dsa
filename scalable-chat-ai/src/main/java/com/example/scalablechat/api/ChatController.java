package com.example.scalablechat.api;

import com.example.scalablechat.model.ChatRequest;
import com.example.scalablechat.model.ChatResponse;
import com.example.scalablechat.model.ChatTokenEvent;
import com.example.scalablechat.service.ChatService;
import com.example.scalablechat.util.RequestId;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public Mono<ResponseEntity<ChatResponse>> chat(
            @Valid @RequestBody ChatRequest request,
            @RequestHeader(name = "X-Client-Id", defaultValue = "anonymous") String clientId
    ) {
        String requestId = RequestId.newId();
        return chatService.chat(request, clientId, requestId)
                .map(response -> ResponseEntity.ok()
                        .header("X-Request-Id", requestId)
                        .body(response));
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<ChatTokenEvent>> stream(
            @Valid @RequestBody ChatRequest request,
            @RequestHeader(name = "X-Client-Id", defaultValue = "anonymous") String clientId
    ) {
        String requestId = RequestId.newId();
        Flux<ServerSentEvent<ChatTokenEvent>> tokens = chatService.stream(request, clientId)
                .map(token -> ServerSentEvent.<ChatTokenEvent>builder()
                        .event("token")
                        .id(requestId)
                        .data(new ChatTokenEvent(token, false, requestId))
                        .build());

        ServerSentEvent<ChatTokenEvent> doneEvent = ServerSentEvent.<ChatTokenEvent>builder()
                .event("done")
                .id(requestId)
                .data(new ChatTokenEvent("", true, requestId))
                .build();

        return tokens.concatWith(Mono.just(doneEvent));
    }
}
