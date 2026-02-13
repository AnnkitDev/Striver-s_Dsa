package com.example.scalablechat.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Chat chat = new Chat();
    private final Llm llm = new Llm();

    public Chat getChat() {
        return chat;
    }

    public Llm getLlm() {
        return llm;
    }

    public static class Chat {
        @Min(1)
        @Max(200)
        private int historyLimit = 30;

        @Min(1)
        @Max(500)
        private int perMinuteLimit = 60;

        public int getHistoryLimit() {
            return historyLimit;
        }

        public void setHistoryLimit(int historyLimit) {
            this.historyLimit = historyLimit;
        }

        public int getPerMinuteLimit() {
            return perMinuteLimit;
        }

        public void setPerMinuteLimit(int perMinuteLimit) {
            this.perMinuteLimit = perMinuteLimit;
        }
    }

    public static class Llm {
        @NotBlank
        private String provider = "mock";

        @NotBlank
        private String baseUrl = "https://api.openai.com/v1";

        @NotBlank
        private String model = "gpt-4o-mini";

        private String apiKey = "";

        @Min(1)
        @Max(120)
        private int timeoutSeconds = 20;

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public int getTimeoutSeconds() {
            return timeoutSeconds;
        }

        public void setTimeoutSeconds(int timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
        }
    }
}
