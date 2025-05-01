package service;

import com.apimonitor.config.ApiConfig;
import com.apimonitor.dto.ApiMetricsReport;
import com.apimonitor.dto.ApiMetricsSummary;
import com.apimonitor.mapper.ApiMetricsMapper;
import com.apimonitor.model.impl.ApiEndpointImpl;
import com.apimonitor.model.impl.ApiMetricsImpl;
import com.apimonitor.repository.ApiEndpointRepository;
import com.apimonitor.repository.MetricsRepository;
import com.apimonitor.service.impl.ApiMonitorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ApiMonitorServiceImplTest {

    private RestTemplate restTemplate;
    private MetricsRepository metricsRepository;
    private ApiEndpointRepository endpointRepository;
    private ApiMetricsMapper metricsMapper;
    private ApiMonitorServiceImpl service;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        metricsRepository = mock(MetricsRepository.class);
        endpointRepository = mock(ApiEndpointRepository.class);
        metricsMapper = mock(ApiMetricsMapper.class);
        ApiConfig apiConfig = mock(ApiConfig.class);

        service = new ApiMonitorServiceImpl(
                restTemplate, metricsRepository, endpointRepository, metricsMapper, apiConfig);
    }

    @Test
    void testMonitorSingleConfigEndpoint_success() {
        // Arrange
        ApiConfig.ApiEndpoint conf = new ApiConfig.ApiEndpoint();
        conf.setName("testApi");
        conf.setUrl("http://example.com");
        conf.setMethod("GET");
        conf.setFrequencyMs(5000L);

        ApiEndpointImpl storedEndpoint = new ApiEndpointImpl();
        storedEndpoint.setId(1L);
        storedEndpoint.setName(conf.getName());
        storedEndpoint.setUrl(conf.getUrl());
        when(endpointRepository.findByName(conf.getName())).thenReturn(Optional.of(storedEndpoint));

        ResponseEntity<String> responseEntity = ResponseEntity.ok()
                .headers(new HttpHeaders())
                .body("response body");
        when(restTemplate.exchange(eq(conf.getUrl()), eq(HttpMethod.GET), isNull(), eq(String.class)))
                .thenReturn(responseEntity);

        // Act
        service.monitorSingleConfigEndpoint(conf);

        // Assert
        ArgumentCaptor<ApiMetricsImpl> captor = ArgumentCaptor.forClass(ApiMetricsImpl.class);
        verify(metricsRepository).save(captor.capture());
        ApiMetricsImpl saved = captor.getValue();
        assertThat(saved.getEndpoint()).isEqualTo(storedEndpoint);
        assertThat(saved.getApiName()).isEqualTo(conf.getName());
        assertThat(saved.getApiUrl()).isEqualTo(conf.getUrl());
        assertThat(saved.isSuccess()).isTrue();
        assertThat(saved.getStatusCode()).isEqualTo(200);
        assertThat(saved.getResponse().getBody()).isEqualTo("response body");
    }

    @Test
    void testMonitorSingleConfigEndpoint_httpError() {
        // Arrange
        ApiConfig.ApiEndpoint conf = new ApiConfig.ApiEndpoint();
        conf.setName("errApi");
        conf.setUrl("http://error.com");
        conf.setMethod("POST");

        when(endpointRepository.findByName(conf.getName())).thenReturn(Optional.empty());
        when(endpointRepository.save(any(ApiEndpointImpl.class))).thenAnswer(invocation -> invocation.getArgument(0));

        HttpStatusCodeException exception = mock(HttpStatusCodeException.class);
        when(exception.getStatusCode()).thenReturn(org.springframework.http.HttpStatus.NOT_FOUND);
        when(exception.getResponseBodyAsString()).thenReturn("Not Found");
        when(exception.getResponseHeaders()).thenReturn(null);
        when(restTemplate.exchange(eq(conf.getUrl()), eq(HttpMethod.POST), isNull(), eq(String.class)))
                .thenThrow(exception);

        // Act
        service.monitorSingleConfigEndpoint(conf);

        // Assert
        ArgumentCaptor<ApiMetricsImpl> captor = ArgumentCaptor.forClass(ApiMetricsImpl.class);
        verify(metricsRepository).save(captor.capture());
        ApiMetricsImpl saved = captor.getValue();
        assertThat(saved.getStatusCode()).isEqualTo(404);
        assertThat(saved.isSuccess()).isFalse();
        assertThat(saved.getErrorMessage()).isEqualTo("Not Found");
    }

    @Test
    void testGetAllMetricsSummaries() {
        // Arrange
        ApiMetricsImpl m1 = new ApiMetricsImpl();
        ApiMetricsImpl m2 = new ApiMetricsImpl();
        List<ApiMetricsImpl> metricsList = List.of(m1, m2);
        when(metricsRepository.findAll()).thenReturn(metricsList);

        ApiMetricsSummary s1 = new ApiMetricsSummary();
        ApiMetricsSummary s2 = new ApiMetricsSummary();
        when(metricsMapper.toSummary(m1)).thenReturn(s1);
        when(metricsMapper.toSummary(m2)).thenReturn(s2);

        // Act
        List<ApiMetricsSummary> summaries = service.getAllMetricsSummaries();

        // Assert
        assertThat(summaries).containsExactly(s1, s2);
    }

    @Test
    void testGetMetricsReport_success() {
        // Arrange
        long endpointId = 10L;
        ApiEndpointImpl endpoint = new ApiEndpointImpl();
        endpoint.setId(endpointId);
        endpoint.setName("reportApi");
        endpoint.setUrl("http://report.com");
        when(endpointRepository.findById(endpointId)).thenReturn(Optional.of(endpoint));

        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now();
        ApiMetricsImpl m = new ApiMetricsImpl();
        List<ApiMetricsImpl> metrics = List.of(m);
        when(metricsRepository.findByApiNameAndTimestampBetween(endpoint.getName(), from, to))
                .thenReturn(metrics);

        ApiMetricsReport report = new ApiMetricsReport();
        when(metricsMapper.toReport(any())).thenReturn(report);

        // Act
        ApiMetricsReport result = service.getMetricsReport(endpointId, from, to);

        // Assert
        assertThat(result).isSameAs(report);
    }

    @Test
    void testGetMetricsReport_endpointNotFound() {
        // Arrange
        when(endpointRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.getMetricsReport(99L, LocalDateTime.now(), LocalDateTime.now()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Endpoint not found: 99");
    }
}
