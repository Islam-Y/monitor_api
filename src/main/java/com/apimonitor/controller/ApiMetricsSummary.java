package com.apimonitor.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiMetricsSummary {
    private String apiName;
    private long totalRequests;
    private long successfulRequests;
    private long failedRequests;
    private double averageResponseTimeMs;
}
