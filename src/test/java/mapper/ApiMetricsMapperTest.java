package mapper;

import com.apimonitor.dto.ApiMetricsReport;
import com.apimonitor.dto.ApiMetricsSummary;
import com.apimonitor.model.impl.ApiMetricsImpl;
import org.junit.jupiter.api.Test;
import com.apimonitor.mapper.ApiMetricsMapper;

import static org.junit.jupiter.api.Assertions.*;

class ApiMetricsMapperTest {

    private final ApiMetricsMapper mapper = ApiMetricsMapper.INSTANCE;

    @Test
    void testToReport() {
        // Данные для теста
        ApiMetricsImpl metrics = new ApiMetricsImpl();
        metrics.setApiName("API");
        metrics.setApiUrl("http://example.com/api");

        // Преобразование в ApiMetricsReport
        ApiMetricsReport report = mapper.toReport(metrics);

        // Проверка значений после преобразования
        assertEquals(metrics.getApiName(), report.getApiName());
        assertEquals(metrics.getApiUrl(), report.getApiUrl());
    }

    @Test
    void testToSummary() {
        // Данные для теста
        ApiMetricsImpl metrics = new ApiMetricsImpl();
        metrics.setApiName("API");
        metrics.setApiUrl("http://example.com/api");

        // Преобразование в ApiMetricsSummary
        ApiMetricsSummary summary = mapper.toSummary(metrics);

        // Проверка значений после преобразования
        assertEquals(metrics.getApiName(), summary.getApiName());
        assertEquals(metrics.getApiUrl(), summary.getApiUrl());
    }
}

