package com.apimonitor.service;

import com.apimonitor.config.ApiConfig;
import com.apimonitor.dto.ApiMetricsReport;
import com.apimonitor.dto.ApiMetricsSummary;

import java.time.LocalDateTime;
import java.util.List;

public interface ApiMonitorService {
    void monitorAllEndpoints();
    List<ApiMetricsSummary> getAllMetricsSummaries();
    ApiMetricsReport getMetricsReport(Long endpointId,
                                      LocalDateTime from,
                                      LocalDateTime to);
}
