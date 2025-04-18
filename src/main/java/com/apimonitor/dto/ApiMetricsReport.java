package com.apimonitor.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;

//@Getter
//@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor

public class ApiMetricsReport {

    /**
     * Название API, к которому относится данный отчёт.
     */
    private String apiName;

    /**
     * URL-адрес API, использовавшийся при мониторинге.
     */
    private String apiUrl;

    /**
     * Общее количество запросов, отправленных к API за указанный период.
     */
    private long totalRequests;

    /**
     * Количество запросов, завершившихся с ошибкой (например, статус 4xx или 5xx).
     */
    private long errorCount;

    /**
     * Среднее время отклика (в миллисекундах) за указанный период.
     */
    private double avgResponseMs;

    /**
     * Минимальное зафиксированное время отклика (в миллисекундах).
     */
    private double minResponseMs;

    /**
     * Максимальное зафиксированное время отклика (в миллисекундах).
     */
    private double maxResponseMs;

    /**
     * Распределение ответов по HTTP-статусам. Ключ — статус-код, значение — количество таких ответов.
     */
    private Map<Integer, Long> statusCodeDistribution;

    /**
     * Время начала периода, за который формируется отчёт.
     */
    private LocalDateTime reportStartTime;

    /**
     * Время окончания периода, за который формируется отчёт.
     */
    private LocalDateTime reportEndTime;

    /**
     * Заголовки, возвращённые API при последнем или типичном ответе (если сохраняются).
     */
    private Map<String, String> headers;

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

    public long getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(long errorCount) {
        this.errorCount = errorCount;
    }

    public double getAvgResponseMs() {
        return avgResponseMs;
    }

    public void setAvgResponseMs(double avgResponseMs) {
        this.avgResponseMs = avgResponseMs;
    }

    public double getMinResponseMs() {
        return minResponseMs;
    }

    public void setMinResponseMs(double minResponseMs) {
        this.minResponseMs = minResponseMs;
    }

    public double getMaxResponseMs() {
        return maxResponseMs;
    }

    public void setMaxResponseMs(double maxResponseMs) {
        this.maxResponseMs = maxResponseMs;
    }

    public Map<Integer, Long> getStatusCodeDistribution() {
        return statusCodeDistribution;
    }

    public void setStatusCodeDistribution(Map<Integer, Long> statusCodeDistribution) {
        this.statusCodeDistribution = statusCodeDistribution;
    }

    public LocalDateTime getReportStartTime() {
        return reportStartTime;
    }

    public void setReportStartTime(LocalDateTime reportStartTime) {
        this.reportStartTime = reportStartTime;
    }

    public LocalDateTime getReportEndTime() {
        return reportEndTime;
    }

    public void setReportEndTime(LocalDateTime reportEndTime) {
        this.reportEndTime = reportEndTime;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
