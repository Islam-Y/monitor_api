package com.apimonitor.service.impl;

import com.apimonitor.controller.ApiMetricsSummary;
import com.apimonitor.repository.MetricsRepository;
import com.apimonitor.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final MetricsRepository metricsRepository;

    @Override
    @Transactional(readOnly = true)
    public ApiMetricsSummary aggregateSummary(
            String apiName,
            LocalDateTime from,
            LocalDateTime to
    ) {
        long total = (apiName != null)
                ? metricsRepository.countByApiNameAndTimestampBetween(apiName, from, to)
                : metricsRepository.count();

        long failed = (apiName != null)
                ? metricsRepository.countByApiNameAndTimestampBetweenAndSuccessFalse(apiName, from, to)
                : metricsRepository.countBySuccessFalse();

        long success = total - failed;

        double avg = 0.0;
        if (apiName != null) {
            Double dbl = metricsRepository.findAverageResponseTime(apiName, from, to);
            avg = dbl != null ? dbl : 0.0;
        } else {
            Double dbl = metricsRepository.findOverallAverageResponseTime();
            avg = dbl != null ? dbl : 0.0;
        }

        return new ApiMetricsSummary(apiName, total, success, failed, avg);
    }
}
