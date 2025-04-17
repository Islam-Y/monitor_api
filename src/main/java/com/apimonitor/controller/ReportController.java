package com.apimonitor.controller;

import com.apimonitor.model.ApiMetrics;
import com.apimonitor.service.MetricsService;
import com.apimonitor.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final MetricsService metricsService;
    private final ReportService reportService;

    /**
     * Выбрать все записи или отфильтровать по имени API и/или по интервалу.
     * Пример:
     * GET /api/reports?apiName=User%20API&from=2025-04-01T00:00:00&to=2025-04-17T23:59:59
     */
    @GetMapping
    public List<ApiMetrics> getMetrics(
            @RequestParam(required = false) String apiName,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to
    ) {
        if (apiName == null && from == null && to == null) {
            return metricsService.findAll();
        } else {
            return metricsService.findByFilter(apiName, from, to);
        }
    }

    /**
     * Генерация агрегированного отчёта: среднее время, количество ошибок и т.д.
     */
    @GetMapping("/summary")
    public ApiMetricsSummary getSummary(
            @RequestParam String apiName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return reportService.aggregateSummary(apiName, from, to);
    }
}
