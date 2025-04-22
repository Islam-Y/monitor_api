package dto;

import com.apimonitor.dto.ApiMetricsReport;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ApiMetricsReportTest {

    @Test
    void testApiMetricsReportBuilder() {
        LocalDateTime from = LocalDateTime.of(2025, 4, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2025, 4, 2, 0, 0);

        // Создание объекта через builder
        ApiMetricsReport report = ApiMetricsReport.builder()
                .apiName("API")
                .apiUrl("http://example.com/api")
                .totalRequests(100)
                .errorCount(10)
                .avgResponseMs(200.5)
                .minResponseMs(150.0)
                .maxResponseMs(300.0)
                .statusCodeDistribution(Map.of(200, 50L, 404, 10L, 500, 40L))  // Используем Long
                .reportStartTime(from)
                .reportEndTime(to)
                .headers(Map.of("Content-Type", "application/json"))
                .build();

        // Проверка значений через геттеры
        assertEquals("API", report.getApiName());
        assertEquals("http://example.com/api", report.getApiUrl());
        assertEquals(100, report.getTotalRequests());
        assertEquals(10, report.getErrorCount());
        assertEquals(200.5, report.getAvgResponseMs());
        assertEquals(150.0, report.getMinResponseMs());
        assertEquals(300.0, report.getMaxResponseMs());
        assertEquals(from, report.getReportStartTime());
        assertEquals(to, report.getReportEndTime());
        assertEquals(Map.of("Content-Type", "application/json"), report.getHeaders());

        // Сравнение карт без учета реализации
        assertEquals(new HashMap<>(Map.of(200, 50L, 404, 10L, 500, 40L)), new HashMap<>(report.getStatusCodeDistribution()));
    }
}