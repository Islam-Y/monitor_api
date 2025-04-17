package com.apimonitor.scheduler;

import com.apimonitor.service.ApiMonitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MonitoringScheduler {
    private final ApiMonitorService apiMonitorService;

    @Scheduled(fixedRateString = "${api.monitoring.interval:5000}")
    public void performScheduledMonitoring() {
        apiMonitorService.monitorAllEndpoints();
    }
}
