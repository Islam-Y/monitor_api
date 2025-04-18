package com.apimonitor.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс для настройки OpenAPI документации с использованием Swagger.
 * Этот класс генерирует документацию для API, включая информацию о версиях и описаниях.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Создаёт и настраивает объект OpenAPI для генерации документации API.
     * Включает информацию о названии API, версии и описании.
     *
     * @return настроенный объект {@link OpenAPI}
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("My API")
                        .version("1.0")
                        .description("Документация для тестового API"));
    }
}
