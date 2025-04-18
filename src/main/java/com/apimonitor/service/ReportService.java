package com.apimonitor.service;

import com.apimonitor.dto.ApiMetricsSummary;

import java.time.LocalDateTime;

public interface ReportService {
    ApiMetricsSummary aggregateSummary(String apiName, LocalDateTime from, LocalDateTime to);

}
