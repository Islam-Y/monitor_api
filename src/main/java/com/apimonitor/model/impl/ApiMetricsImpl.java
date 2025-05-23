package com.apimonitor.model.impl;

import com.apimonitor.model.ApiMetrics;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * JPA-сущность для хранения собранных метрик по запросам к API.
 * Реализует интерфейс {@link ApiMetrics}.
 * <p>
 * Аннотируется для игнорирования ленивых связей при JSON-сериализации.
 */
@Entity
@Table(name = "api_metrics",
        indexes = {
                @Index(name = "idx_api_metrics_endpoint", columnList = "endpoint_id"),
                @Index(name = "idx_api_metrics_timestamp", columnList = "timestamp")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "response")

public class ApiMetricsImpl implements ApiMetrics {

    /**
     * Уникальный идентификатор записи метрики.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Ссылка на конфигурацию эндпоинта, к которому относятся эти метрики.
     * Ленивая связь игнорируется при JSON-сериализации.
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "endpoint_id", nullable = false)
    private ApiEndpointImpl endpoint;

    /**
     * URL эндпоинта на момент выполнения запроса.
     */
    @Column(name = "api_url", nullable = false, length = 2048)
    private String apiUrl;

    /**
     * Имя эндпоинта на момент выполнения запроса.
     */
    @Column(name = "api_name", nullable = false, length = 256)
    private String apiName;

    /**
     * HTTP-статус ответа.
     */
    @Column(name = "status_code", nullable = false)
    private int statusCode;

    /**
     * Время ответа в миллисекундах.
     */
    @Column(name = "response_time_ms", nullable = false)
    private long responseTimeMs;

    /**
     * Временная метка записи метрики. Заполняется автоматически при создании записи.
     */
    @CreationTimestamp
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    /**
     * Флаг успешности запроса (true — статус 2xx, false — иначе).
     */
    @Column(name = "success", nullable = false)
    private boolean success;

    /**
     * Сообщение об ошибке, если запрос завершился неуспешно.
     */
    @Column(name = "error_message", length = 2048)
    private String errorMessage;

    /**
     * Ассоциированный детальный объект ответа (например, заголовки, тело и т.д.).
     */
    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "response_id")
    private ApiResponseImpl response;
}