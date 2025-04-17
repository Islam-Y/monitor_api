package com.apimonitor.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "api")
@Data
public class ApiConfig {
    private List<ApiEndpoint> endpoints;
    private long monitoringInterval;

    @Data
    public static class ApiEndpoint {
        private String url;
        private String method;
        private long frequencyMs;
        private String name;
    }
}
