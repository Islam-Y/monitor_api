package com.apimonitor.model.impl;

import com.apimonitor.model.ApiEndpoint;
import jakarta.persistence.*;
import lombok.*;

import java.util.Map;

/**
 * JPA-сущность для хранения конфигурации мониторинга API-эндпоинтов.
 * Реализует интерфейс {@link ApiEndpoint}.
 */
@Entity
@Table(name = "api_endpoints",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"url", "method"}),
                @UniqueConstraint(columnNames = {"name"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = "headers")
public class ApiEndpointImpl implements ApiEndpoint {

    /**
     * Уникальный идентификатор записи.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * URL эндпоинта для мониторинга.
     */
    @Column(nullable = false)
    private String url;

    /**
     * HTTP-метод запроса (GET, POST и т.д.).
     */
    @Column(nullable = false, length = 10)
    private String method;

    /**
     * Интервал между проверками в миллисекундах.
     */
    @Column(name = "frequency_ms", nullable = false)
    private long frequencyMs;

    /**
     * Читабельное имя эндпоинта.
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Заголовки, которые надо отправлять вместе с запросами на эндпоинт.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "api_headers",
            joinColumns = @JoinColumn(name = "endpoint_id"))
    @MapKeyColumn(name = "header_name")
    @Column(name = "header_value")
    private Map<String, String> headers;
}