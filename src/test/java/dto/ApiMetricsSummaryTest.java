package dto;

import com.apimonitor.dto.ApiMetricsSummary;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiMetricsSummaryTest {

    @Test
    void testApiMetricsSummaryBuilder() {
        // Данные для теста
        String apiName = "API";
        String apiUrl = "http://example.com/api";
        long totalRequests = 100;
        long successfulRequests = 80;
        long failedRequests = 20;
        double avgResponseMs = 250.5;
        LocalDateTime summaryGeneratedAt = LocalDateTime.of(2025, 4, 1, 10, 0, 0, 0);

        // Создание объекта через builder
        ApiMetricsSummary summary = ApiMetricsSummary.builder()
                .apiName(apiName)
                .apiUrl(apiUrl)
                .totalRequests(totalRequests)
                .successfulRequests(successfulRequests)
                .failedRequests(failedRequests)
                .avgResponseMs(avgResponseMs)
                .summaryGeneratedAt(summaryGeneratedAt)
                .successRate(calculateSuccessRate(successfulRequests, totalRequests))
                .build();

        // Проверка значений через геттеры
        assertEquals(apiName, summary.getApiName());
        assertEquals(apiUrl, summary.getApiUrl());
        assertEquals(totalRequests, summary.getTotalRequests());
        assertEquals(successfulRequests, summary.getSuccessfulRequests());
        assertEquals(failedRequests, summary.getFailedRequests());
        assertEquals(avgResponseMs, summary.getAvgResponseMs());
        assertEquals(summaryGeneratedAt, summary.getSummaryGeneratedAt());

        // Проверка корректности вычисления successRate
        double expectedSuccessRate = (successfulRequests / (double) totalRequests) * 100;
        assertEquals(expectedSuccessRate, summary.getSuccessRate(), 0.01);
    }

    // Метод для вычисления successRate
    private double calculateSuccessRate(long successfulRequests, long totalRequests) {
        if (totalRequests == 0) return 0;
        return (successfulRequests / (double) totalRequests) * 100;
    }
}
