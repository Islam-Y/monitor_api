package com.apimonitor.model.impl;

import com.apimonitor.model.ApiResponse;
import jakarta.persistence.*;
import lombok.*;

/**
 * Сущность, представляющая тело ответа API, связанное с метрикой.
 * Хранит тело HTTP-ответа в виде строки.
 */
@Entity
@Table(name = "api_responses")
//@Getter
//@Setter
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

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public ApiMetricsImpl getMetrics() {
        return metrics;
    }

    @Override
    public void setMetrics(ApiMetricsImpl metrics) {
        this.metrics = metrics;
    }
}

