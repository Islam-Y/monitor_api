package com.apimonitor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "api")
@Getter
@Setter
public class ApiConfig {
    private List<ApiEndpoint> endpoints;
    private long monitoringInterval;

    @Getter
    @Setter
    public static class ApiEndpoint {
        private String url;
        private String method;
        private long frequencyMs;
        private String name;
    }
}
