package com.apimonitor.controller;

import com.apimonitor.config.ApiConfig;
import com.apimonitor.service.ApiMonitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контроллер для управления и запуска мониторинга производительности настроенных API-эндпоинтов.
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/monitor")
@Tag(name = "Мониторинг API", description = "Операции для мониторинга производительности API")
public class ApiMonitorController {

    private final ApiMonitorService apiMonitorService;
    private final ApiConfig apiConfig;

    /**
     * GET /api/monitor/endpoints
     * <p>
     * Возвращает список всех API-эндпоинтов, настроенных для мониторинга.
     * Здесь мы напрямую используем модель конфигурации {@link ApiConfig.ApiEndpoint},
     * аннотированную в классе конфигурации для Swagger.
     *
     * @return 200 OK с JSON-массивом {@link ApiConfig.ApiEndpoint}
     */
    @Operation(
            summary = "Список настроенных эндпоинтов",
            description = "Получение списка всех API-эндпоинтов, сконфигурированных для мониторинга",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешный ответ со списком эндпоинтов",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ApiConfig.ApiEndpoint.class))
                            )
                    )
            }
    )
    @GetMapping("/endpoints")
    public ResponseEntity<List<ApiConfig.ApiEndpoint>> listEndpoints() {
        log.info("Возвращаем {} эндпоинтов для мониторинга", apiConfig.getEndpoints().size());
        return ResponseEntity.ok(apiConfig.getEndpoints());
    }

    /**
     * POST /api/monitor/run
     * <p>
     * Запускает мониторинг всех сконфигурированных эндпоинтов вручную.
     * Обычно мониторинг выполняется по расписанию, но этот метод позволяет запускать проверку сразу.
     *
     * @return 202 Accepted без тела
     */
    @Operation(
            summary = "Запуск мониторинга вручную",
            description = "Инициализация одноразовой проверки производительности всех настроенных эндпоинтов",
            responses = {
                    @ApiResponse(responseCode = "202", description = "Запрос принят к исполнению"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
            }
    )
    @PostMapping("/run")
    public ResponseEntity<Void> runOnce() {
        log.info("Manual start of monitoring");
        apiMonitorService.monitorAllEndpoints();
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}