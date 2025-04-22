package config;

import com.apimonitor.config.ApiConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes = ApiConfigTest.TestConfig.class
)
@TestPropertySource(properties = {
        "api.monitoringInterval=3000",
        "api.endpoints[0].url=https://jsonplaceholder.typicode.com/posts/1",
        "api.endpoints[0].method=GET",
        "api.endpoints[0].frequencyMs=5000",
        "api.endpoints[0].name=Test API",
        "api.endpoints[1].url=https://jsonplaceholder.typicode.com/users/1",
        "api.endpoints[1].method=GET",
        "api.endpoints[1].frequencyMs=10000",
        "api.endpoints[1].name=User API"
})
class ApiConfigTest {

    @Autowired
    private ApiConfig apiConfig;

    @Test
    void testApiConfigLoadedCorrectly() {
        // убедимся, что бин вообще создался
        assertNotNull(apiConfig, "ApiConfig должен быть создан в контексте");

        // глобальный интервал
        assertEquals(3000, apiConfig.getMonitoringInterval());

        // два эндпоинта
        List<ApiConfig.ApiEndpoint> endpoints = apiConfig.getEndpoints();
        assertNotNull(endpoints);
        assertEquals(2, endpoints.size());

        ApiConfig.ApiEndpoint first = endpoints.get(0);
        assertEquals("https://jsonplaceholder.typicode.com/posts/1", first.getUrl());
        assertEquals("GET", first.getMethod());
        assertEquals(5000, first.getFrequencyMs());
        assertEquals("Test API", first.getName());

        ApiConfig.ApiEndpoint second = endpoints.get(1);
        assertEquals("https://jsonplaceholder.typicode.com/users/1", second.getUrl());
        assertEquals("GET", second.getMethod());
        assertEquals(10000, second.getFrequencyMs());
        assertEquals("User API", second.getName());
    }

    /**
     * Минимальный класс конфигурации для теста.
     * Spring Boot найдёт его как точку входа,
     * и подключит ApiConfig в контекст.
     */
    @SpringBootConfiguration
    @EnableConfigurationProperties(ApiConfig.class)
    static class TestConfig {
        // пусто — все бины тащим через @EnableConfigurationProperties
    }
}
