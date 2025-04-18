package com.apimonitor.controller;

import com.apimonitor.dto.ApiMetricsReport;
import com.apimonitor.dto.ApiMetricsSummary;
import com.apimonitor.model.impl.ApiMetricsImpl;
import com.apimonitor.service.MetricsService;
import com.apimonitor.service.ReportService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST контроллер для получения метрик и отчетов по производительности API.
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Отчеты и метрики", description = "Операции для получения списков метрик и отчетов по API")
public class ReportController {

    private final MetricsService metricsService;
    private final ReportService reportService;

    /**
     * 1) Список всех метрик или отфильтрованный по apiName и/или по времени.
     *
     * @param apiName имя API для фильтрации (опционально)
     * @param from    начало периода (ISO_DATE_TIME, опционально)
     * @param to      конец периода (ISO_DATE_TIME, опционально)
     * @return 200 OK + список {@link ApiMetricsImpl} (или DTO, если вы добавите маппинг)
     */
    @Operation(
            summary = "Список метрик",
            description = "Получение списка всех метрик или с фильтрацией по API и/или периоду",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список метрик",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ApiMetricsReport.class))
                            )
                    )
            }
    )
    @GetMapping("/metrics")
    public ResponseEntity<List<ApiMetricsImpl>> getMetrics(
            @RequestParam(required = false) String apiName,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        log.info("Запрос метрик: apiName={}, from={}, to={}", apiName, from, to);
        List<ApiMetricsImpl> list = (apiName != null && from != null && to != null)
                ? metricsService.findByFilter(apiName, from, to)
                : metricsService.findAll();
        return ResponseEntity.ok(list);
    }

    /**
     * 2) Сводка по одному API за указанный период:
     * среднее время, кол-во успешных и неуспешных запросов и т.д.
     *
     * @param apiName имя API (обязательно)
     * @param from    начало периода (ISO_DATE_TIME, обязательно)
     * @param to      конец периода (ISO_DATE_TIME, обязательно)
     * @return 200 OK + {@link ApiMetricsSummary}
     */
    @Operation(
            summary = "Сводка по API",
            description = "Получение сводной статистики по API за указанный период",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Сводная статистика по API",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiMetricsSummary.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Недопустимый запрос"),
                    @ApiResponse(responseCode = "404", description = "API не найден")
            }
    )
    @GetMapping("/reports/summary")
    public ResponseEntity<ApiMetricsSummary> getSummary(
            @RequestParam @NotBlank String apiName,
            @RequestParam @NotNull
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @NotNull
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        log.info("Запрос сводки: apiName={}, from={}, to={}", apiName, from, to);
        ApiMetricsSummary summary = reportService.aggregateSummary(apiName, from, to);
        return ResponseEntity.ok(summary);
    }

    /**
     * 3) Детальный отчёт (Report) по всем API за период:
     * avg/min/max времена, распределение по статус-кодам, заголовки.
     *
     * @param from начало периода (ISO_DATE_TIME, обязательно)
     * @param to   конец периода (ISO_DATE_TIME, обязательно)
     * @return 200 OK + список {@link ApiMetricsReport}
     */
    @Operation(
            summary = "Детальный отчет по API",
            description = "Получение детального отчета по всем API за указанный период",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Детальный отчет",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ApiMetricsReport.class))
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Недопустимый запрос")
            }
    )
    @GetMapping("/reports/detailed")
    public ResponseEntity<List<ApiMetricsReport>> getDetailedReports(
            @RequestParam @NotNull
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @NotNull
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        log.info("Запрос детального отчета: from={}, to={}", from, to);
        List<ApiMetricsReport> reports = metricsService.buildReports(from, to);
        return ResponseEntity.ok(reports);
    }
}

