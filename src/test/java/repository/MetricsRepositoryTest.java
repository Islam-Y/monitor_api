//package repository;
//
//import com.apimonitor.model.impl.ApiMetricsImpl;
//import com.apimonitor.repository.MetricsRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.SpringBootConfiguration;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest(
//        classes = MetricsRepositoryTest.TestConfig.class
//)
//class MetricsRepositoryTest {
//
//    @SpringBootConfiguration
//    @EnableConfigurationProperties(MetricsRepository.class)
//    static class TestConfig {
//        // пусто — все бины тащим через @EnableConfigurationProperties
//    }
//
//    @Autowired
//    private MetricsRepository metricsRepository;
//
//    @Autowired
//    private TestEntityManager entityManager;
//
//    private final LocalDateTime now = LocalDateTime.now();
//    private final String testApiName = "testApi";
//    private final String anotherApiName = "anotherApi";
//
//    @BeforeEach
//    void setUp() {
//        entityManager.clear();
//    }
//
//    @Test
//    void findDistinctApiName_ReturnsUniqueApiNames() {
//        // Подготовка данных
//        metricsRepository.save(createMetrics(testApiName, now.minusHours(2), true, 150));
//        metricsRepository.save(createMetrics(testApiName, now.minusHours(1), true, 200));
//        metricsRepository.save(createMetrics(anotherApiName, now, false, 500));
//
//        // Выполнение запроса
//        List<String> result = metricsRepository.findDistinctApiName();
//
//        // Проверка результатов
//        assertEquals(2, result.size());
//        assertTrue(result.containsAll(List.of(testApiName, anotherApiName)));
//    }
//
//    @Test
//    void findByApiNameAndTimestampBetween_ReturnsFilteredMetrics() {
//        // Подготовка данных
//        LocalDateTime from = now.minusDays(1);
//
//        ApiMetricsImpl metric1 = metricsRepository.save(createMetrics(testApiName, now.minusHours(2), true, 150));
//        ApiMetricsImpl metric2 = metricsRepository.save(createMetrics(testApiName, now.minusHours(1), false, 300));
//        metricsRepository.save(createMetrics(testApiName, now.plusHours(1), true, 200)); // Вне диапазона
//        metricsRepository.save(createMetrics(anotherApiName, now.minusHours(3), true, 250)); // Другое API
//
//        // Выполнение запроса
//        List<ApiMetricsImpl> result = metricsRepository.findByApiNameAndTimestampBetween(testApiName, from, now);
//
//        // Проверка результатов
//        assertEquals(2, result.size());
//        assertTrue(result.containsAll(List.of(metric1, metric2)));
//    }
//
//    @Test
//    void countByApiNameAndTimestampBetween_ReturnsCorrectCount() {
//        // Подготовка данных
//        LocalDateTime from = now.minusHours(3);
//        LocalDateTime to = now.minusMinutes(30);
//
//        metricsRepository.save(createMetrics(testApiName, now.minusHours(2), true, 150));
//        metricsRepository.save(createMetrics(testApiName, now.minusHours(1), false, 300));
//        metricsRepository.save(createMetrics(testApiName, now, true, 200)); // Вне диапазона
//        metricsRepository.save(createMetrics(anotherApiName, now.minusHours(2), true, 250)); // Другое API
//
//        // Выполнение запроса
//        long count = metricsRepository.countByApiNameAndTimestampBetween(testApiName, from, to);
//
//        // Проверка результатов
//        assertEquals(2, count);
//    }
//
//    @Test
//    void countByApiNameAndTimestampBetweenAndSuccessFalse_ReturnsFailedRequestsCount() {
//        // Подготовка данных
//        LocalDateTime from = now.minusDays(1);
//
//        metricsRepository.save(createMetrics(testApiName, now.minusHours(2), false, 300));
//        metricsRepository.save(createMetrics(testApiName, now.minusHours(1), false, 500));
//        metricsRepository.save(createMetrics(testApiName, now.minusMinutes(30), true, 200)); // Успешный
//        metricsRepository.save(createMetrics(anotherApiName, now.minusHours(3), false, 400)); // Другое API
//
//        // Выполнение запроса
//        long count = metricsRepository.countByApiNameAndTimestampBetweenAndSuccessFalse(testApiName, from, now);
//
//        // Проверка результатов
//        assertEquals(2, count);
//    }
//
//    @Test
//    void countBySuccessFalse_ReturnsTotalFailedRequests() {
//        // Подготовка данных
//        metricsRepository.save(createMetrics(testApiName, now.minusHours(2), false, 300));
//        metricsRepository.save(createMetrics(testApiName, now.minusHours(1), true, 200));
//        metricsRepository.save(createMetrics(anotherApiName, now.minusMinutes(30), false, 500));
//
//        // Выполнение запроса
//        long count = metricsRepository.countBySuccessFalse();
//
//        // Проверка результатов
//        assertEquals(2, count);
//    }
//
//    @Test
//    void findAverageResponseTime_CalculatesCorrectAverage() {
//        // Подготовка данных
//        metricsRepository.save(createMetrics(testApiName, now.minusHours(3), true, 100));
//        metricsRepository.save(createMetrics(testApiName, now.minusHours(1), true, 200));
//        metricsRepository.save(createMetrics(testApiName, now.minusMinutes(10), true, 300)); // Вне диапазона
//
//        // Выполнение запроса
//        Double average = metricsRepository.findAverageResponseTime(
//                testApiName,
//                now.minusDays(1),
//                now.minusMinutes(30)
//        );
//
//        // Проверка результатов
//        assertEquals(150.0, average, "Среднее время отклика не соответствует ожидаемому");
//    }
//
//    @Test
//    void findAverageResponseTime_ReturnsNullWhenNoData() {
//        // Выполнение запроса без данных
//        Double average = metricsRepository.findAverageResponseTime(
//                "nonExistentApi",
//                now.minusDays(1),
//                now
//        );
//
//        // Проверка результатов
//        assertNull(average);
//    }
//
//    @Test
//    void findOverallAverageResponseTime_CalculatesCorrectAverage() {
//        // Подготовка данных
//        metricsRepository.save(createMetrics(testApiName, now.minusHours(2), true, 100));
//        metricsRepository.save(createMetrics(testApiName, now.minusHours(1), true, 200));
//        metricsRepository.save(createMetrics(anotherApiName, now, true, 300));
//
//        // Выполнение запроса
//        Double average = metricsRepository.findOverallAverageResponseTime();
//
//        // Проверка результатов
//        assertEquals(200.0, average);
//    }
//
//    @Test
//    void findOverallAverageResponseTime_ReturnsNullWhenEmpty() {
//        Double average = metricsRepository.findOverallAverageResponseTime();
//        assertNull(average, "Ожидается null для пустой базы данных");
//    }
//
//    private ApiMetricsImpl createMetrics(String apiName, LocalDateTime timestamp,
//                                         boolean success, long responseTime) {
//        ApiMetricsImpl metrics = new ApiMetricsImpl();
//        metrics.setApiName(apiName);
//        metrics.setTimestamp(timestamp);
//        metrics.setSuccess(success);
//        metrics.setResponseTimeMs(responseTime);
//        return entityManager.persistAndFlush(metrics);
//    }
//}