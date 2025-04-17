package com.apimonitor.controller;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiMetricsSummary {
    private String apiName;
    private long totalRequests;
    private long successfulRequests;
    private long failedRequests;
    private double averageResponseTimeMs;
}
