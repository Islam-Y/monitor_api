//package repository;
//
//import com.apimonitor.model.impl.ApiEndpointImpl;
//import com.apimonitor.model.impl.ApiMetricsImpl;
//import com.apimonitor.model.impl.ApiResponseImpl;
//import com.apimonitor.repository.ApiEndpointRepository;
//import com.apimonitor.repository.MetricsRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//@Transactional
//class MetricsRepositoryTest {
//
//    @Autowired
//    private MetricsRepository metricsRepository;
//
//    @Autowired
//    private ApiEndpointRepository endpointRepository;
//
//    private ApiEndpointImpl endpoint;
//
//    @BeforeEach
//    void setUp() {
//        // Создание тестового эндпоинта
//        endpoint = new ApiEndpointImpl();
//        endpoint.setName("Test Endpoint");
//        endpoint.setBaseUrl("http://localhost/api");
//        endpoint = endpointRepository.save(endpoint);
//
//        // Очистка таблицы метрик
//        metricsRepository.deleteAll();
//
//        // Добавление тестовых данных
//        ApiResponseImpl response = new ApiResponseImpl();
//        response.setHeaders("{\"Content-Type\": \"application/json\"}");
//        response.setBody("{\"message\": \"success\"}");
//
//        ApiMetricsImpl metric = ApiMetricsImpl.builder()
//                .endpoint(endpoint)
//                .apiUrl("http://localhost/api/resource")
//                .apiName("TestAPI")
//                .statusCode(200)
//                .responseTimeMs(100)
//                .success(true)
//                .timestamp(LocalDateTime.now())
//                .response(response)
//                .build();
//
//        metricsRepository.save(metric);
//    }
//
//    @Test
//    void testFindDistinctApiName() {
//        List<String> apiNames = metricsRepository.findDistinctApiName();
//        assertThat(apiNames).containsExactly("TestAPI");
//    }
//
//    @Test
//    void testCountBySuccessFalse() {
//        long count = metricsRepository.countBySuccessFalse();
//        assertThat(count).isEqualTo(0);
//    }
//}