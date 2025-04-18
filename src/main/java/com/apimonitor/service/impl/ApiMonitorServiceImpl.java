package com.apimonitor.service.impl;

import com.apimonitor.dto.ApiMetricsReport;
import com.apimonitor.dto.ApiMetricsSummary;
import com.apimonitor.mapper.ApiMetricsMapper;
import com.apimonitor.model.impl.ApiEndpointImpl;
import com.apimonitor.model.impl.ApiMetricsImpl;
import com.apimonitor.repository.ApiEndpointRepository;
import com.apimonitor.repository.MetricsRepository;
import com.apimonitor.service.ApiMonitorService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiMonitorServiceImpl implements ApiMonitorService {
    private final RestTemplate restTemplate;
    private final MetricsRepository metricsRepository;
    private final ApiEndpointRepository endpointRepository;
    private final ApiMetricsMapper metricsMapper;

    /**
     * Запускается по расписанию каждые 30 секунд (можно вынести в конфиг).
     */
    @Scheduled(fixedDelayString = "${monitor.fixedDelayMs:30000}")
    public void monitorAllEndpoints() {
        endpointRepository.findAll()
                .forEach(this::monitorSingleEndpoint);
    }

    /**
     * Выполняет один запрос к заданному эндпоинту и сохраняет результаты в БД.
     *
     * @param endpoint сущность эндпоинта для мониторинга
     */
    @Transactional
    public void monitorSingleEndpoint(ApiEndpointImpl endpoint) {
        long startTime = System.currentTimeMillis();
        HttpHeaders headers = new HttpHeaders();
        if (endpoint.getHeaders() != null) {
            endpoint.getHeaders().forEach(headers::add);
        }
        HttpEntity<Void> entity = new HttpEntity<>(null, headers);

        int status;
        long responseTime;
        boolean success;
        String errorMsg = null;

        try {
            ResponseEntity<String> resp = restTemplate.exchange(
                    endpoint.getUrl(),
                    HttpMethod.valueOf(endpoint.getMethod()),
                    entity,
                    String.class
            );
            responseTime = System.currentTimeMillis() - startTime;
            status       = resp.getStatusCode().value();
            success      = resp.getStatusCode().is2xxSuccessful();
            saveMetrics(endpoint, status, responseTime, success, null);
            log.info("Monitoring success: {} ({} ms)", endpoint.getName(), responseTime);

        } catch (HttpStatusCodeException ex) {
            responseTime = System.currentTimeMillis() - startTime;
            status       = ex.getStatusCode().value();
            errorMsg     = ex.getResponseBodyAsString();
            saveMetrics(endpoint, status, responseTime, false, errorMsg);
            log.error("Monitoring HTTP error: {} - {}", endpoint.getName(), status);

        } catch (RestClientException ex) {
            responseTime = System.currentTimeMillis() - startTime;
            status       = HttpStatus.INTERNAL_SERVER_ERROR.value();
            errorMsg     = ex.getMessage();
            saveMetrics(endpoint, status, responseTime, false, errorMsg);
            log.error("Monitoring failed: {} - {}", endpoint.getName(), errorMsg);
        }
    }

    private void saveMetrics(ApiEndpointImpl endpoint,
                             int statusCode,
                             long responseTime,
                             boolean success,
                             String errorMessage) {
        ApiMetricsImpl m = new ApiMetricsImpl();
        m.setEndpoint(endpoint);
        m.setApiUrl(endpoint.getUrl());
        m.setApiName(endpoint.getName());
        m.setStatusCode(statusCode);
        m.setResponseTimeMs(responseTime);
        m.setSuccess(success);
        m.setErrorMessage(errorMessage);
        metricsRepository.save(m);
    }

    /**
     * Возвращает список всех записей метрик в виде DTO‑сводок.
     *
     * @return List<ApiMetricsSummary> — для каждого результата мониторинга
     */
    @Override
    @Transactional(readOnly = true)
    public List<ApiMetricsSummary> getAllMetricsSummaries() {
        return metricsRepository.findAll().stream()
                .map(metricsMapper::toSummary)
                .collect(Collectors.toList());
    }

    /**
     * Генерирует агрегированный отчёт по метрикам указанного эндпоинта за заданный период.
     *
     * @param endpointId ID эндпоинта, для которого строится отчёт
     * @param from       начало периода (включительно)
     * @param to         конец периода (включительно)
     * @return {@link ApiMetricsReport} со следующими данными:
     *         <ul>
     *           <li>apiName — имя эндпоинта;</li>
     *           <li>apiUrl — URL эндпоинта;</li>
     *           <li>totalRequests — общее количество запросов;</li>
     *           <li>errorCount — количество неуспешных запросов;</li>
     *           <li>avgResponseMs — среднее время отклика;</li>
     *           <li>minResponseMs — минимальное время отклика;</li>
     *           <li>maxResponseMs — максимальное время отклика;</li>
     *           <li>statusCodeDistribution — распределение количества ответов по статус-кодам;</li>
     *           <li>reportStartTime, reportEndTime — границы периода;</li>
     *           <li>headers — заголовки, с которыми выполнялся запрос.</li>
     *         </ul>
     * @throws EntityNotFoundException если эндпоинт с указанным ID не найден
     */
    @Override
    @Transactional(readOnly = true)
    public ApiMetricsReport getMetricsReport(Long endpointId,
                                             LocalDateTime from,
                                             LocalDateTime to) {
        // 1. Достаём endpoint и apiName
        ApiEndpointImpl endpoint = endpointRepository.findById(endpointId)
                .orElseThrow(() -> new EntityNotFoundException("Endpoint not found: " + endpointId));
        String apiName = endpoint.getName();
        String apiUrl  = endpoint.getUrl();

        // 2. Собираем «сырые» метрики за период
        List<ApiMetricsImpl> metrics = metricsRepository
                .findByApiNameAndTimestampBetween(apiName, from, to);

        // 3. Агрегируем:
        long totalRequests = metrics.size();
        long errorCount    = metrics.stream().filter(m -> !m.isSuccess()).count();
        double avgResponseMs = metrics.stream()
                .mapToLong(ApiMetricsImpl::getResponseTimeMs)
                .average()
                .orElse(0.0);
        double minResponseMs = metrics.stream()
                .mapToLong(ApiMetricsImpl::getResponseTimeMs)
                .min()
                .orElse(0L);
        double maxResponseMs = metrics.stream()
                .mapToLong(ApiMetricsImpl::getResponseTimeMs)
                .max()
                .orElse(0L);

        Map<Integer, Long> statusCodeDistribution = metrics.stream()
                .collect(Collectors.groupingBy(
                        ApiMetricsImpl::getStatusCode,
                        Collectors.counting()
                ));

        // 4. Собираем DTO через Builder
        return ApiMetricsReport.builder()
                .apiName(apiName)
                .apiUrl(apiUrl)
                .totalRequests(totalRequests)
                .errorCount(errorCount)
                .avgResponseMs(avgResponseMs)
                .minResponseMs(minResponseMs)
                .maxResponseMs(maxResponseMs)
                .statusCodeDistribution(statusCodeDistribution)
                .reportStartTime(from)
                .reportEndTime(to)
                .headers(endpoint.getHeaders())
                .build();
    }
}

