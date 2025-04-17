package com.apimonitor.service.impl;

import com.apimonitor.config.ApiConfig;
import com.apimonitor.model.impl.ApiEndpointImpl;
import com.apimonitor.model.impl.ApiMetricsImpl;
import com.apimonitor.repository.ApiEndpointRepository;
import com.apimonitor.repository.MetricsRepository;
import com.apimonitor.service.ApiMonitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiMonitorServiceImpl implements ApiMonitorService {
    private final RestTemplate restTemplate;
    private final MetricsRepository metricsRepository;
    private final ApiEndpointRepository endpointRepository;

    /**
     * Запускается по расписанию каждые 30 секунд (можно вынести в конфиг).
     */
    @Scheduled(fixedDelayString = "${monitor.fixedDelayMs:30000}")
    public void monitorAllEndpoints() {
        endpointRepository.findAll()
                .forEach(this::monitorSingleEndpoint);
    }

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
            ResponseEntity<String> response = restTemplate.exchange(
                    endpoint.getUrl(),
                    HttpMethod.valueOf(endpoint.getMethod()),
                    entity,
                    String.class
            );
            responseTime = System.currentTimeMillis() - startTime;
            status = response.getStatusCodeValue();
            success = response.getStatusCode().is2xxSuccessful();
            saveMetrics(endpoint, status, responseTime, success, null);
            log.info("Monitoring success: {} ({} ms)", endpoint.getName(), responseTime);
        } catch (HttpStatusCodeException ex) {
            responseTime = System.currentTimeMillis() - startTime;
            status = ex.getRawStatusCode();
            errorMsg = ex.getResponseBodyAsString();
            saveMetrics(endpoint, status, responseTime, false, errorMsg);
            log.error("Monitoring HTTP error: {} - {}", endpoint.getName(), status);
        } catch (RestClientException ex) {
            responseTime = System.currentTimeMillis() - startTime;
            status = HttpStatus.INTERNAL_SERVER_ERROR.value();
            errorMsg = ex.getMessage();
            saveMetrics(endpoint, status, responseTime, false, errorMsg);
            log.error("Monitoring failed: {} - {}", endpoint.getName(), errorMsg);
        }
    }

    private void saveMetrics(ApiEndpointImpl endpoint,
                             int statusCode,
                             long responseTime,
                             boolean success,
                             String errorMessage) {
        ApiMetricsImpl metrics = new ApiMetricsImpl();
        metrics.setEndpoint(endpoint);
        metrics.setApiUrl(endpoint.getUrl());
        metrics.setApiName(endpoint.getName());
        metrics.setStatusCode(statusCode);
        metrics.setResponseTimeMs(responseTime);
        metrics.setSuccess(success);
        metrics.setErrorMessage(errorMessage);
        metricsRepository.save(metrics);
    }
}
