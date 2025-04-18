package com.apimonitor.service;

import com.apimonitor.dto.ApiMetricsReport;
import com.apimonitor.model.impl.ApiMetricsImpl;

import java.time.LocalDateTime;
import java.util.List;

public interface MetricsService {
    List<ApiMetricsImpl> findAll();
    List<ApiMetricsImpl> findByFilter(String apiName, LocalDateTime from, LocalDateTime to);
    double getAverageResponseTime(String apiName, LocalDateTime from, LocalDateTime to);
    long getErrorCount(String apiName, LocalDateTime from, LocalDateTime to);

    /**
     * Генерация отчетов для всех API-ендпоинтов
     */
    List<ApiMetricsReport> buildReports(LocalDateTime from, LocalDateTime to);
}
