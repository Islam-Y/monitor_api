package com.apimonitor.controller;

import com.apimonitor.config.ApiConfig;
import com.apimonitor.service.impl.ApiMonitorServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monitor")
@RequiredArgsConstructor
public class ApiMonitorController {

    private final ApiMonitorServiceImpl apiMonitorServiceImpl;
    private final ApiConfig apiConfig;

    /**
     * Вернёт список всех эндпоинтов из конфигурации.
     */
    @GetMapping("/endpoints")
    public ResponseEntity<List<ApiConfig.ApiEndpoint>> listEndpoints() {
        return ResponseEntity.ok(apiConfig.getEndpoints());
    }

    /**
     * Триггерит мониторинг всех эндпоинтов «вручную».
     */
    @PostMapping("/run")
    public ResponseEntity<Void> runOnce() {
        apiMonitorServiceImpl.monitorAllEndpoints();
        return ResponseEntity.ok().build();
    }
}