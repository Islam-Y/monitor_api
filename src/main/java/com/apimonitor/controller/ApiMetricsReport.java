package com.apimonitor.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiMetricsReport {
    private String apiName;
    private long totalRequests;
    private long errorCount;
    private double avgResponseMs;
}
