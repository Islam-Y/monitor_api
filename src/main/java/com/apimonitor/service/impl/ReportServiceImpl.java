package com.apimonitor.service.impl;

import com.apimonitor.dto.ApiMetricsSummary;
import com.apimonitor.repository.MetricsRepository;
import com.apimonitor.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final MetricsRepository metricsRepository;

    /**
     * Формирует сводную метрику (Summary) по одному API или по всем, если apiName == null.
     *
     * @param apiName имя API для фильтрации (точное совпадение).
     *                Если null — агрегируется по всем записям.
     * @param from    начало периода (включительно)
     * @param to      конец периода (включительно)
     * @return {@link ApiMetricsSummary} с данными:
     *         <ul>
     *           <li>apiName — имя API (или null для всех);</li>
     *           <li>totalRequests — общее количество запросов;</li>
     *           <li>successfulRequests — количество успешных запросов;</li>
     *           <li>failedRequests — количество неуспешных запросов;</li>
     *           <li>avgResponseMs — среднее время отклика;</li>
     *           <li>summaryGeneratedAt — время формирования сводки.</li>
     *         </ul>
     */
    @Override
    @Transactional(readOnly = true)
    public ApiMetricsSummary aggregateSummary(
            String apiName,
            LocalDateTime from,
            LocalDateTime to
    ) {
        // 1. Общее число запросов
        long total = (apiName != null)
                ? metricsRepository.countByApiNameAndTimestampBetween(apiName, from, to)
                : metricsRepository.count();

        // 2. Число неуспешных запросов
        long failed = (apiName != null)
                ? metricsRepository.countByApiNameAndTimestampBetweenAndSuccessFalse(apiName, from, to)
                : metricsRepository.countBySuccessFalse();

        // 3. Число успешных = общее − неуспешные
        long success = total - failed;

        // 4. Среднее время отклика
        double avg = (apiName != null)
                ? Optional.ofNullable(metricsRepository.findAverageResponseTime(apiName, from, to)).orElse(0.0)
                : Optional.ofNullable(metricsRepository.findOverallAverageResponseTime()).orElse(0.0);

        // 5. Сборка DTO через Builder
        return ApiMetricsSummary.builder()
                .apiName(apiName)
                .totalRequests(total)
                .successfulRequests(success)
                .failedRequests(failed)
                .avgResponseMs(avg)
                .summaryGeneratedAt(LocalDateTime.now())
                .build();
    }
}