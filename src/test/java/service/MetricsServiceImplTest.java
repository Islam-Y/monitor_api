package service;

import com.apimonitor.repository.MetricsRepository;
import com.apimonitor.model.impl.ApiMetricsImpl;
import com.apimonitor.dto.ApiMetricsReport;
import com.apimonitor.service.impl.MetricsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MetricsServiceImplTest {

    @Mock
    private MetricsRepository metricsRepository;

    @InjectMocks
    private MetricsServiceImpl metricsService;

    private LocalDateTime from;
    private LocalDateTime to;

    @BeforeEach
    void setUp() {
        from = LocalDateTime.now().minusHours(1);
        to = LocalDateTime.now();
    }

    @Test
    void findAll_returnsAllMetrics() {
        List<ApiMetricsImpl> expected = Arrays.asList(
                ApiMetricsImpl.builder().apiName("api1").build(),
                ApiMetricsImpl.builder().apiName("api2").build()
        );
        when(metricsRepository.findAll()).thenReturn(expected);

        List<ApiMetricsImpl> actual = metricsService.findAll();

        assertEquals(expected, actual);
        verify(metricsRepository, times(1)).findAll();
    }

    @Test
    void findByFilter_withAllParams_callsFilteredRepo() {
        List<ApiMetricsImpl> filtered = Collections.singletonList(
                ApiMetricsImpl.builder().apiName("api1").build()
        );
        when(metricsRepository.findByApiNameAndTimestampBetween("api1", from, to))
                .thenReturn(filtered);

        List<ApiMetricsImpl> actual = metricsService.findByFilter("api1", from, to);

        assertEquals(filtered, actual);
        verify(metricsRepository, times(1)).findByApiNameAndTimestampBetween("api1", from, to);
    }

    @Test
    void findByFilter_withNullParam_returnsAll() {
        List<ApiMetricsImpl> all = Collections.singletonList(
                ApiMetricsImpl.builder().apiName("apiX").build()
        );
        when(metricsRepository.findAll()).thenReturn(all);

        assertEquals(all, metricsService.findByFilter(null, from, to));
        assertEquals(all, metricsService.findByFilter("api1", null, to));
        assertEquals(all, metricsService.findByFilter("api1", from, null));
        verify(metricsRepository, times(3)).findAll();
    }

    @Test
    void getAverageResponseTime_returnsValueOrZero() {
        when(metricsRepository.findAverageResponseTime("api1", from, to)).thenReturn(250.5);
        assertEquals(250.5, metricsService.getAverageResponseTime("api1", from, to));

        when(metricsRepository.findAverageResponseTime("api2", from, to)).thenReturn(null);
        assertEquals(0.0, metricsService.getAverageResponseTime("api2", from, to));
    }

    @Test
    void getErrorCount_returnsCount() {
        when(metricsRepository.countByApiNameAndTimestampBetweenAndSuccessFalse("api1", from, to))
                .thenReturn(5L);
        assertEquals(5L, metricsService.getErrorCount("api1", from, to));
    }

    @Test
    void buildReports_aggregatesMetricsCorrectly() {
        // Prepare repository stubs
        when(metricsRepository.findDistinctApiName()).thenReturn(Arrays.asList("api1", "api2"));

        ApiMetricsImpl m1 = ApiMetricsImpl.builder()
                .apiName("api1").apiUrl("url1").statusCode(200).responseTimeMs(100).success(true).build();
        ApiMetricsImpl m2 = ApiMetricsImpl.builder()
                .apiName("api1").apiUrl("url1").statusCode(500).responseTimeMs(300).success(false).build();
        when(metricsRepository.findByApiNameAndTimestampBetween("api1", from, to))
                .thenReturn(Arrays.asList(m1, m2));

        ApiMetricsImpl m3 = ApiMetricsImpl.builder()
                .apiName("api2").apiUrl("url2").statusCode(404).responseTimeMs(150).success(false).build();
        when(metricsRepository.findByApiNameAndTimestampBetween("api2", from, to))
                .thenReturn(Collections.singletonList(m3));

        List<ApiMetricsReport> reports = metricsService.buildReports(from, to);

        assertEquals(2, reports.size());
        // Validate report for api1
        ApiMetricsReport r1 = reports.stream()
                .filter(r -> "api1".equals(r.getApiName()))
                .findFirst().orElseThrow();
        assertEquals("url1", r1.getApiUrl());
        assertEquals(2, r1.getTotalRequests());
        assertEquals(1, r1.getErrorCount());
        assertEquals((100 + 300) / 2.0, r1.getAvgResponseMs());
        assertEquals(100.0, r1.getMinResponseMs());
        assertEquals(300.0, r1.getMaxResponseMs());
        Map<Integer, Long> expectedDist1 = new HashMap<>();
        expectedDist1.put(200, 1L);
        expectedDist1.put(500, 1L);
        assertEquals(expectedDist1, r1.getStatusCodeDistribution());

        // Validate report for api2
        ApiMetricsReport r2 = reports.stream()
                .filter(r -> "api2".equals(r.getApiName()))
                .findFirst().orElseThrow();
        assertEquals("url2", r2.getApiUrl());
        assertEquals(1, r2.getTotalRequests());
        assertEquals(1, r2.getErrorCount());
        assertEquals(150.0, r2.getAvgResponseMs());
        assertEquals(150.0, r2.getMinResponseMs());
        assertEquals(150.0, r2.getMaxResponseMs());
        Map<Integer, Long> expectedDist2 = new HashMap<>();
        expectedDist2.put(404, 1L);
        assertEquals(expectedDist2, r2.getStatusCodeDistribution());
    }
}
