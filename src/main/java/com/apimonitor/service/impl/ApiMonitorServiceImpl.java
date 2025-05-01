package com.apimonitor.service.impl;

import com.apimonitor.config.ApiConfig;
import com.apimonitor.dto.ApiMetricsReport;
import com.apimonitor.dto.ApiMetricsSummary;
import com.apimonitor.mapper.ApiMetricsMapper;
import com.apimonitor.model.impl.ApiEndpointImpl;
import com.apimonitor.model.impl.ApiMetricsImpl;
import com.apimonitor.model.impl.ApiResponseImpl;
import com.apimonitor.repository.ApiEndpointRepository;
import com.apimonitor.repository.MetricsRepository;
import com.apimonitor.service.ApiMonitorService;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервис для мониторинга API-эндпоинтов и получения отчетов по метрикам.
 * <p>
 * При сохранении метрик, каждый ApiMetricsImpl получает ссылку на сущность ApiEndpointImpl,
 * загруженную или созданную на основании настроек из ApiConfig.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApiMonitorServiceImpl implements ApiMonitorService {

    private final RestTemplate restTemplate;
    private final MetricsRepository metricsRepository;
    private final ApiEndpointRepository endpointRepository;
    private final ApiMetricsMapper metricsMapper;
    private final ApiConfig apiConfig;

    /**
     * Запускает мониторинг всех эндпоинтов из конфигурации.
     */
    @Override
    @Transactional
    @Scheduled(fixedDelayString = "${api.monitoring.interval:5000}")
    public void monitorAllEndpoints() {
        List<ApiConfig.ApiEndpoint> endpoints = apiConfig.getEndpoints();
        log.info("Запуск мониторинга по конфигу: {} эндпоинтов", endpoints.size());
        endpoints.forEach(this::monitorSingleConfigEndpoint);
        log.info("Мониторинг завершен");
    }

    /**
     * Выполняет запрос к одному эндпоинту конфигурации и сохраняет метрику.
     * Устанавливает связь с ApiEndpointImpl из БД.
     */
    @Transactional
    public void monitorSingleConfigEndpoint(ApiConfig.ApiEndpoint conf) {
        ApiEndpointImpl endpoint = findOrCreateEndpoint(conf);
        ApiResponseImpl response;

        long start = System.currentTimeMillis();
        int status;
        long responseTime;
        boolean success;
        String errorMsg = null;

        try {
            ResponseEntity<String> resp = restTemplate.exchange(
                    conf.getUrl(), HttpMethod.valueOf(conf.getMethod()), null, String.class);
            responseTime = System.currentTimeMillis() - start;
            status = resp.getStatusCode().value();
            success = resp.getStatusCode().is2xxSuccessful();

            response = ApiResponseImpl.builder()
                    .body(resp.getBody())
                    .headers(resp.getHeaders().toSingleValueMap())
                    .build();

        } catch (HttpStatusCodeException ex) {
            responseTime = System.currentTimeMillis() - start;
            status = ex.getStatusCode().value();
            errorMsg = ex.getResponseBodyAsString();
            success = false;

            response = ApiResponseImpl.builder()
                    .body(errorMsg)
                    .headers(
                            ex.getResponseHeaders() != null
                                    ? ex.getResponseHeaders().toSingleValueMap()
                                    : Collections.emptyMap()
                    )
                    .build();

        } catch (RestClientException ex) {
            responseTime = System.currentTimeMillis() - start;
            status = 500;
            errorMsg = ex.getMessage();
            success = false;

            response = ApiResponseImpl.builder()
                    .body(errorMsg)
                    .headers(null)
                    .build();
        }

        ApiMetricsImpl m = ApiMetricsImpl.builder()
                .endpoint(endpoint)
                .apiName(conf.getName())
                .apiUrl(conf.getUrl())
                .statusCode(status)
                .responseTimeMs(responseTime)
                .success(success)
                .errorMessage(errorMsg)
                .response(response)
                .build();

        metricsRepository.save(m);
        log.debug("Сохранена метрика для {}: status={}, time={}ms", conf.getName(), status, responseTime);
    }

    /**
     * Ищет в БД ApiEndpointImpl по уникальному имени или URL, иначе создаёт новую запись.
     */
    private ApiEndpointImpl findOrCreateEndpoint(ApiConfig.ApiEndpoint conf) {
        Optional<ApiEndpointImpl> opt = endpointRepository.findByName(conf.getName());
        if (opt.isPresent()) {
            return opt.get();
        }
        ApiEndpointImpl entity = new ApiEndpointImpl();
        entity.setName(conf.getName());
        entity.setUrl(conf.getUrl());
        entity.setMethod(conf.getMethod());
        entity.setFrequencyMs(conf.getFrequencyMs());
        // headers нет в конфиге
        return endpointRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApiMetricsSummary> getAllMetricsSummaries() {
        return metricsRepository.findAll().stream()
                .map(metricsMapper::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ApiMetricsReport getMetricsReport(Long endpointId, LocalDateTime from, LocalDateTime to) {
        ApiEndpointImpl endpoint = endpointRepository.findById(endpointId)
                .orElseThrow(() -> new EntityNotFoundException("Endpoint not found: " + endpointId));
        List<ApiMetricsImpl> metrics = metricsRepository.findByApiNameAndTimestampBetween(
                endpoint.getName(), from, to);
        return metricsMapper.toReport(
                new ReportWrapper(endpoint, metrics, from, to)
        );
    }


    /**
     * Wrapper для передачи списка в mapper.
     */
    @Getter
    private static class ReportWrapper extends ApiMetricsImpl {
        private final List<ApiMetricsImpl> list;
        private final LocalDateTime from;
        private final LocalDateTime to;

        public ReportWrapper(ApiEndpointImpl endpoint, List<ApiMetricsImpl> list,
                             LocalDateTime from, LocalDateTime to) {
            super();
            setEndpoint(endpoint);
            setApiName(endpoint.getName());
            setApiUrl(endpoint.getUrl());
            this.list = list;
            this.from = from;
            this.to = to;
        }

    }
}
