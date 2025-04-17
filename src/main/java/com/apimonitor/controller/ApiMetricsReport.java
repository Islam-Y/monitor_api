package com.apimonitor.controller;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiMetricsReport {
    private String apiName;
    private long totalRequests;
    private long errorCount;
    private double avgResponseMs;
}
