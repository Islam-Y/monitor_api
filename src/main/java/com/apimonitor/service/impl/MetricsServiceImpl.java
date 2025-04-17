package com.apimonitor.service.impl;

import com.apimonitor.controller.ApiMetricsReport;
import com.apimonitor.model.impl.ApiMetricsImpl;
import com.apimonitor.repository.MetricsRepository;
import com.apimonitor.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetricsServiceImpl implements MetricsService {

    private final MetricsRepository metricsRepository;

    @Override
    public List<ApiMetricsImpl> findAll() {
        return metricsRepository.findAll();
    }

    @Override
    public List<ApiMetricsImpl> findByFilter(String apiName, LocalDateTime from, LocalDateTime to) {
        if (apiName != null && from != null && to != null) {
            return metricsRepository.findByApiNameAndTimestampBetween(apiName, from, to);
        }
        return findAll();
    }

    @Override
    public double getAverageResponseTime(String apiName, LocalDateTime from, LocalDateTime to) {
        Double avg = metricsRepository.findAverageResponseTime(apiName, from, to);
        return avg != null ? avg : 0.0;
    }

    @Override
    public long getErrorCount(String apiName, LocalDateTime from, LocalDateTime to) {
        return metricsRepository
                .countByApiNameAndTimestampBetweenAndSuccessFalse(apiName, from, to);
    }

    @Override
    public List<ApiMetricsReport> buildReports(LocalDateTime from, LocalDateTime to) {
        List<String> names = metricsRepository.findDistinctApiName();
        return names.stream()
                .map(name -> {
                    long total = metricsRepository
                            .countByApiNameAndTimestampBetween(name, from, to);
                    long errors = metricsRepository
                            .countByApiNameAndTimestampBetweenAndSuccessFalse(name, from, to);
                    double avg = getAverageResponseTime(name, from, to);
                    return new ApiMetricsReport(name, total, errors, avg);
                })
                .collect(Collectors.toList());
    }
}
