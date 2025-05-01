package controller;

import com.apimonitor.controller.ReportController;
import com.apimonitor.dto.ApiMetricsReport;
import com.apimonitor.dto.ApiMetricsSummary;
import com.apimonitor.model.impl.ApiMetricsImpl;
import com.apimonitor.service.MetricsService;
import com.apimonitor.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    private MockMvc mockMvc;
    private MetricsService metricsService;
    private ReportService reportService;
    private ReportController controller;

    @BeforeEach
    void setUp() {
        metricsService = mock(MetricsService.class);
        reportService = mock(ReportService.class);
        controller = new ReportController(metricsService, reportService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getMetrics_noFilter_shouldCallFindAllAndReturnEmptyList() throws Exception {
        when(metricsService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/metrics").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));

        verify(metricsService).findAll();
        verifyNoInteractions(reportService);
    }

    @Test
    void getMetrics_withFilter_shouldCallFindByFilter() throws Exception {
        LocalDateTime from = LocalDateTime.of(2025, 4, 1, 10, 0);
        LocalDateTime to = LocalDateTime.of(2025, 4, 2, 10, 0);
        when(metricsService.findByFilter(eq("API"), eq(from), eq(to)))
                .thenReturn(List.of(new ApiMetricsImpl()));

        mockMvc.perform(get("/api/metrics")
                        .param("apiName", "API")
                        .param("from", from.toString())
                        .param("to", to.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));

        verify(metricsService).findByFilter("API", from, to);
    }

    @Test
    void getSummary_shouldCallAggregateSummary() throws Exception {
        LocalDateTime from = LocalDateTime.of(2025, 4, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2025, 4, 2, 0, 0);
        ApiMetricsSummary summary = new ApiMetricsSummary();
        // default summary has zeros
        when(reportService.aggregateSummary(eq("API"), eq(from), eq(to))).thenReturn(summary);

        mockMvc.perform(get("/api/reports/summary")
                        .param("apiName", "API")
                        .param("from", from.toString())
                        .param("to", to.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // assert key summary fields have expected default values
                .andExpect(jsonPath("$.totalRequests", is((int) summary.getTotalRequests())))
                .andExpect(jsonPath("$.successfulRequests", is((int) summary.getSuccessfulRequests())))
                .andExpect(jsonPath("$.failedRequests", is((int) summary.getFailedRequests())))
                .andExpect(jsonPath("$.avgResponseMs", is((double) summary.getAvgResponseMs())));

        verify(reportService).aggregateSummary("API", from, to);
        verifyNoInteractions(metricsService);
    }



    @Test
    void getDetailedReports_shouldCallBuildReports() throws Exception {
        LocalDateTime from = LocalDateTime.of(2025, 4, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2025, 4, 2, 0, 0);
        ApiMetricsReport report = new ApiMetricsReport();
        when(metricsService.buildReports(eq(from), eq(to))).thenReturn(List.of(report));

        mockMvc.perform(get("/api/reports/detailed")
                        .param("from", from.toString())
                        .param("to", to.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));

        verify(metricsService).buildReports(from, to);
        verifyNoInteractions(reportService);
    }
}