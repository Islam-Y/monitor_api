package com.apimonitor.service.impl;

import com.apimonitor.dto.ApiMetricsReport;
import com.apimonitor.model.impl.ApiMetricsImpl;
import com.apimonitor.repository.MetricsRepository;
import com.apimonitor.service.MetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricsServiceImpl implements MetricsService {

    private final MetricsRepository metricsRepository;

    /**
     * Возвращает все записи метрик без фильтрации.
     *
     * @return список всех {@link ApiMetricsImpl}
     */
    @Override
    public List<ApiMetricsImpl> findAll() {
        return metricsRepository.findAll();
    }

    /**
     * Возвращает записи метрик по заданному имени API и диапазону времени.
     * Если один из параметров apiName, from или to равен null, возвращает все записи.
     *
     * @param apiName имя API для фильтрации (точное совпадение)
     * @param from    начало временного диапазона (включительно)
     * @param to      конец временного диапазона (включительно)
     * @return отфильтрованный список {@link ApiMetricsImpl}
     */
    @Override
    public List<ApiMetricsImpl> findByFilter(String apiName, LocalDateTime from, LocalDateTime to) {
        if (apiName != null && from != null && to != null) {
            return metricsRepository.findByApiNameAndTimestampBetween(apiName, from, to);
        }
        return findAll();
    }

    /**
     * Вычисляет среднее время отклика в миллисекундах для заданного API за период.
     *
     * @param apiName имя API
     * @param from    начало периода (включительно)
     * @param to      конец периода (включительно)
     * @return среднее время отклика, или 0.0, если данных нет
     */
    @Override
    public double getAverageResponseTime(String apiName, LocalDateTime from, LocalDateTime to) {
        Double avg = metricsRepository.findAverageResponseTime(apiName, from, to);
        return avg != null ? avg : 0.0;
    }

    /**
     * Подсчитывает количество неуспешных запросов (success = false)
     * для заданного API за указанный период.
     *
     * @param apiName имя API
     * @param from    начало периода (включительно)
     * @param to      конец периода (включительно)
     * @return количество неуспешных запросов
     */
    @Override
    public long getErrorCount(String apiName, LocalDateTime from, LocalDateTime to) {
        return metricsRepository
                .countByApiNameAndTimestampBetweenAndSuccessFalse(apiName, from, to);
    }

    /**
     * Строит список агрегированных отчётов по каждому уникальному API-имени
     * в заданном временном диапазоне.
     *
     * @param from начало периода (включительно)
     * @param to   конец периода (включительно)
     * @return список {@link ApiMetricsReport} с полной статистикой по каждому API
     */
    @Override
    @Transactional(readOnly = true)
    public List<ApiMetricsReport> buildReports(LocalDateTime from, LocalDateTime to) {
        // получаем все разные apiName
        List<String> names = metricsRepository.findDistinctApiName();

        return names.stream()
                .map(name -> {
                    // достаём все метрики по имени и времени
                    List<ApiMetricsImpl> metrics = metricsRepository
                            .findByApiNameAndTimestampBetween(name, from, to);
                    return buildReportForApi(name, from, to, metrics);
                })
                .collect(Collectors.toList());
    }

    private ApiMetricsReport buildReportForApi(String name, LocalDateTime from, LocalDateTime to, List<ApiMetricsImpl> metrics) {
        long total = metrics.size();
        long errorCount = metrics.stream()
                .filter(m -> !m.isSuccess())
                .count();

        double avg = metrics.stream()
                .mapToLong(ApiMetricsImpl::getResponseTimeMs)
                .average()
                .orElse(0.0);
        double min = metrics.stream()
                .mapToLong(ApiMetricsImpl::getResponseTimeMs)
                .min()
                .orElse(0L);
        double max = metrics.stream()
                .mapToLong(ApiMetricsImpl::getResponseTimeMs)
                .max()
                .orElse(0L);

        Map<Integer, Long> statusCodeDistribution = metrics.stream()
                .collect(Collectors.groupingBy(
                        ApiMetricsImpl::getStatusCode,
                        Collectors.counting()
                ));

        String apiUrl = metrics.isEmpty()
                ? null
                : metrics.getFirst().getApiUrl();

        // собираем DTO
        return ApiMetricsReport.builder()
                .apiName(name)
                .apiUrl(apiUrl)
                .totalRequests(total)
                .errorCount(errorCount)
                .avgResponseMs(avg)
                .minResponseMs(min)
                .maxResponseMs(max)
                .statusCodeDistribution(statusCodeDistribution)
                .reportStartTime(from)
                .reportEndTime(to)
                .headers(Collections.emptyMap()) // или получить из endpoint, если доступно
                .build();
    }
}
