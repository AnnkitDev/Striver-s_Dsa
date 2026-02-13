package com.example.scalablechat.api;

import com.example.scalablechat.model.ErrorResponse;
import com.example.scalablechat.service.RateLimitExceededException;
import com.example.scalablechat.util.RequestId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RateLimitExceededException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleRateLimit(RateLimitExceededException ex) {
        String requestId = RequestId.newId();
        ErrorResponse body = new ErrorResponse(requestId, ex.getMessage(), Instant.now());
        return Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-Request-Id", requestId)
                .body(body));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage() == null ? "Invalid request" : error.getDefaultMessage())
                .orElse("Invalid request");
        String requestId = RequestId.newId();
        ErrorResponse body = new ErrorResponse(requestId, message, Instant.now());
        return Mono.just(ResponseEntity.badRequest()
                .header("X-Request-Id", requestId)
                .body(body));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleWebFluxValidation(WebExchangeBindException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage() == null ? "Invalid request" : error.getDefaultMessage())
                .orElse("Invalid request");
        String requestId = RequestId.newId();
        ErrorResponse body = new ErrorResponse(requestId, message, Instant.now());
        return Mono.just(ResponseEntity.badRequest()
                .header("X-Request-Id", requestId)
                .body(body));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGeneric(Exception ex) {
        String requestId = RequestId.newId();
        ErrorResponse body = new ErrorResponse(requestId, "Internal server error", Instant.now());
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("X-Request-Id", requestId)
                .body(body));
    }
}
