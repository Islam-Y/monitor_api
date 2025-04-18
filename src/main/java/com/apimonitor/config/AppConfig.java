package com.apimonitor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Конфигурационный класс для создания и настройки бина RestTemplate.
 * Используется для выполнения HTTP-запросов в приложении.
 */
@Configuration
public class AppConfig {

    /**
     * Создаёт и возвращает экземпляр {@link RestTemplate}, который будет использоваться для выполнения
     * HTTP-запросов. Это основной компонент для взаимодействия с внешними API.
     *
     * @return экземпляр {@link RestTemplate}
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
