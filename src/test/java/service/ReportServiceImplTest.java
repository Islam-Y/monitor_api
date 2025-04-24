package service;

import com.apimonitor.dto.ApiMetricsSummary;
import com.apimonitor.repository.MetricsRepository;
import com.apimonitor.service.impl.ReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private MetricsRepository metricsRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    private LocalDateTime from;
    private LocalDateTime to;

    @BeforeEach
    void setUp() {
        from = LocalDateTime.now().minusDays(1);
        to = LocalDateTime.now();
    }

    @Test
    void aggregateSummary_withApiName_usesFilteredCountsAndAverage() {
        // given
        when(metricsRepository.countByApiNameAndTimestampBetween("api1", from, to)).thenReturn(10L);
        when(metricsRepository.countByApiNameAndTimestampBetweenAndSuccessFalse("api1", from, to)).thenReturn(3L);
        when(metricsRepository.findAverageResponseTime("api1", from, to)).thenReturn(200.0);

        // when
        LocalDateTime before = LocalDateTime.now();
        ApiMetricsSummary summary = reportService.aggregateSummary("api1", from, to);
        LocalDateTime after = LocalDateTime.now();

        // then
        assertEquals("api1", summary.getApiName());
        assertEquals(10L, summary.getTotalRequests());
        assertEquals(7L, summary.getSuccessfulRequests());
        assertEquals(3L, summary.getFailedRequests());
        assertEquals(200.0, summary.getAvgResponseMs());
        assertNotNull(summary.getSummaryGeneratedAt());
        assertFalse(summary.getSummaryGeneratedAt().isBefore(before));
        assertFalse(summary.getSummaryGeneratedAt().isAfter(after));

        verify(metricsRepository).countByApiNameAndTimestampBetween("api1", from, to);
        verify(metricsRepository).countByApiNameAndTimestampBetweenAndSuccessFalse("api1", from, to);
        verify(metricsRepository).findAverageResponseTime("api1", from, to);
        verify(metricsRepository, never()).count();
        verify(metricsRepository, never()).countBySuccessFalse();
        verify(metricsRepository, never()).findOverallAverageResponseTime();
    }

    @Test
    void aggregateSummary_withApiName_nullAverage_returnsZero() {
        when(metricsRepository.countByApiNameAndTimestampBetween("apiX", from, to)).thenReturn(5L);
        when(metricsRepository.countByApiNameAndTimestampBetweenAndSuccessFalse("apiX", from, to)).thenReturn(1L);
        when(metricsRepository.findAverageResponseTime("apiX", from, to)).thenReturn(null);

        ApiMetricsSummary summary = reportService.aggregateSummary("apiX", from, to);

        assertEquals(5L, summary.getTotalRequests());
        assertEquals(4L, summary.getSuccessfulRequests());
        assertEquals(1L, summary.getFailedRequests());
        assertEquals(0.0, summary.getAvgResponseMs());
    }

    @Test
    void aggregateSummary_withoutApiName_usesOverallCountsAndAverage() {
        when(metricsRepository.count()).thenReturn(20L);
        when(metricsRepository.countBySuccessFalse()).thenReturn(4L);
        when(metricsRepository.findOverallAverageResponseTime()).thenReturn(150.0);

        LocalDateTime before = LocalDateTime.now();
        ApiMetricsSummary summary = reportService.aggregateSummary(null, from, to);
        LocalDateTime after = LocalDateTime.now();

        assertNull(summary.getApiName());
        assertEquals(20L, summary.getTotalRequests());
        assertEquals(16L, summary.getSuccessfulRequests());
        assertEquals(4L, summary.getFailedRequests());
        assertEquals(150.0, summary.getAvgResponseMs());
        assertNotNull(summary.getSummaryGeneratedAt());
        assertFalse(summary.getSummaryGeneratedAt().isBefore(before));
        assertFalse(summary.getSummaryGeneratedAt().isAfter(after));

        verify(metricsRepository).count();
        verify(metricsRepository).countBySuccessFalse();
        verify(metricsRepository).findOverallAverageResponseTime();
        verify(metricsRepository, never()).countByApiNameAndTimestampBetween(any(), any(), any());
        verify(metricsRepository, never()).countByApiNameAndTimestampBetweenAndSuccessFalse(any(), any(), any());
        verify(metricsRepository, never()).findAverageResponseTime(any(), any(), any());
    }

    @Test
    void aggregateSummary_withoutApiName_nullOverallAverage_returnsZero() {
        when(metricsRepository.count()).thenReturn(8L);
        when(metricsRepository.countBySuccessFalse()).thenReturn(2L);
        when(metricsRepository.findOverallAverageResponseTime()).thenReturn(null);

        ApiMetricsSummary summary = reportService.aggregateSummary(null, from, to);

        assertEquals(8L, summary.getTotalRequests());
        assertEquals(6L, summary.getSuccessfulRequests());
        assertEquals(2L, summary.getFailedRequests());
        assertEquals(0.0, summary.getAvgResponseMs());
    }
}