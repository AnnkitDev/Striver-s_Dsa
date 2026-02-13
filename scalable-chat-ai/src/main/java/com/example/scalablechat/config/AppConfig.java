package com.example.scalablechat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class AppConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    public WebClient llmWebClient(AppProperties properties) {
        HttpClient client = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(properties.getLlm().getTimeoutSeconds()));
        return WebClient.builder()
                .baseUrl(properties.getLlm().getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(client))
                .build();
    }
}
