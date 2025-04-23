package com.apimonitor.model.impl;

import com.apimonitor.model.ApiResponse;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Сущность, представляющая тело ответа API, связанное с метрикой.
 * Хранит тело HTTP-ответа в виде строки.
 */
@Entity
@Table(name = "api_responses")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString(exclude = "metrics")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponseImpl implements ApiResponse {

    /**
     * Уникальный идентификатор ответа.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Тело HTTP-ответа. Может быть большим, поэтому используется @Lob.
     */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String body;

    /**
     * Обратная ссылка на связанную метрику API.
     */
    @OneToOne(mappedBy = "response", fetch = FetchType.LAZY)
    private ApiMetricsImpl metrics;

    /**
     * Заголовки ответа.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "api_headers",
            joinColumns = @JoinColumn(name = "response_id")
    )
    @MapKeyColumn(name = "header_name")
    @Column(name = "header_value")
    private Map<String, String> headers = new HashMap<>();

    public ApiResponseImpl headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }
}

