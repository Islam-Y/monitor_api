package com.apimonitor.dto;

import lombok.*;

import java.time.LocalDateTime;

//@Getter
//@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ApiMetricsSummary {

    /**
     * Название API, для которого формируется сводка.
     */
    private String apiName;

    /**
     * URL-адрес API, использованный при мониторинге.
     */
    private String apiUrl;

    /**
     * Общее количество запросов к API за рассматриваемый период.
     */
    private long totalRequests;

    /**
     * Количество успешных запросов (например, с кодом ответа 2xx).
     */
    private long successfulRequests;

    /**
     * Количество неуспешных запросов (например, с кодом ответа 4xx или 5xx).
     */
    private long failedRequests;

    /**
     * Среднее время отклика API за указанный период (в миллисекундах).
     */
    private double avgResponseMs;

    /**
     * Дата и время генерации сводки.
     */
    private LocalDateTime summaryGeneratedAt;

    /**
     * Уровень успешности запросов (успешные / общее * 100).
     */
    private double successRate;

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public long getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(long totalRequests) {
        this.totalRequests = totalRequests;
    }

    public long getSuccessfulRequests() {
        return successfulRequests;
    }

    public void setSuccessfulRequests(long successfulRequests) {
        this.successfulRequests = successfulRequests;
    }

    public long getFailedRequests() {
        return failedRequests;
    }

    public void setFailedRequests(long failedRequests) {
        this.failedRequests = failedRequests;
    }

    public double getAvgResponseMs() {
        return avgResponseMs;
    }

    public void setAvgResponseMs(double avgResponseMs) {
        this.avgResponseMs = avgResponseMs;
    }

    public LocalDateTime getSummaryGeneratedAt() {
        return summaryGeneratedAt;
    }

    public void setSummaryGeneratedAt(LocalDateTime summaryGeneratedAt) {
        this.summaryGeneratedAt = summaryGeneratedAt;
    }

    public double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(double successRate) {
        this.successRate = successRate;
    }
}
