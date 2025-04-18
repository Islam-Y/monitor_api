package com.apimonitor.model.impl;

import com.apimonitor.model.ApiMetrics;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
/**
 * apiUrl - URL API (например, "https://api.example.com/users")
 * apiName - Человекочитаемое имя из конфига
 * statusCode - HTTP-статус (200, 404, 500...)
 * responseTimeMs - Время ответа в миллисекундах
 * timestamp - Дата и время проверки
 * success - Успешен ли запрос
 */
@Entity
@Table(name = "api_metrics")
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ApiMetricsImpl implements ApiMetrics {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endpoint_id")
    private ApiEndpointImpl endpoint;

    private String apiUrl;
    private String apiName;
    private int statusCode;
    private long responseTimeMs;
    private LocalDateTime timestamp = LocalDateTime.now();
    private boolean success;
    private String errorMessage;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "response_id", unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ApiResponseImpl response;
}
