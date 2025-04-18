package com.apimonitor.scheduler;

import com.apimonitor.service.ApiMonitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Компонент, отвечающий за выполнение мониторинга API эндпоинтов по расписанию.
 * Интервал выполнения задаётся через параметр `api.monitoring.interval` в конфигурации
 * (по умолчанию 5000 миллисекунд).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MonitoringScheduler {
    private final ApiMonitorService apiMonitorService;

    /**
     * Выполняет мониторинг всех зарегистрированных эндпоинтов с заданным интервалом.
     * Метод вызывается автоматически в соответствии с расписанием.
     */
    @Scheduled(fixedRateString = "${api.monitoring.interval:5000}")
    public void performScheduledMonitoring() {
        log.info("Launching scheduled Endpoint API monitoring...");
        apiMonitorService.monitorAllEndpoints();
        log.info("Scheduled monitoring completed.");
    }
}
