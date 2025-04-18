package com.apimonitor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Конфигурационный класс, загружающий параметры из application.yml с префиксом "api".
 * Содержит список API-эндпоинтов для мониторинга и интервал мониторинга.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "api")
public class ApiConfig {

    /**
     * Список API-эндпоинтов, которые необходимо мониторить.
     */
    private List<ApiEndpoint> endpoints;

    /**
     * Глобальный интервал мониторинга (в миллисекундах) по умолчанию.
     * Может быть переопределён для каждого эндпоинта отдельно.
     */
    private long monitoringInterval;

    /**
     * Представление одного API-эндпоинта для мониторинга.
     */
    @Getter
    @Setter
    public static class ApiEndpoint {

        /**
         * URL API-эндпоинта (например, <a href="https://example.com/api/data">...</a>).
         */
        private String url;

        /**
         * HTTP-метод запроса (GET, POST и т.д.).
         */
        private String method;

        /**
         * Частота мониторинга этого эндпоинта в миллисекундах.
         */
        private long frequencyMs;

        /**
         * Уникальное имя API, используемое для идентификации в отчётах.
         */
        private String name;
    }
}
