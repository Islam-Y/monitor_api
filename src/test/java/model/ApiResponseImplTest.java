package model;

import com.apimonitor.model.impl.ApiMetricsImpl;
import com.apimonitor.model.impl.ApiResponseImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit tests for {@link ApiResponseImpl}.
 */
class ApiResponseImplTest {

    private ApiResponseImpl apiResponse;
    private ApiMetricsImpl metrics;
    private final String testBody = "{\"status\":\"success\",\"data\":{\"id\":123}}";

    @BeforeEach
    void setUp() {
        metrics = new ApiMetricsImpl();
        metrics.setId(1L);

        apiResponse = ApiResponseImpl.builder()
                .id(1L)
                .body(testBody)
                .metrics(metrics)
                .build();
    }

    @Test
    void testNoArgsConstructor() {
        ApiResponseImpl empty = new ApiResponseImpl();
        assertNotNull(empty);
        assertNull(empty.getId(), "ID should be null");
        assertNull(empty.getBody(), "Body should be null");
        assertNull(empty.getMetrics(), "Metrics should be null");
        assertNotNull(empty.getHeaders(), "Headers map should be initialized");
        assertTrue(empty.getHeaders().isEmpty(), "Headers map should be empty");
    }

    @Test
    void testAllArgsConstructor() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        ApiResponseImpl full = new ApiResponseImpl(
                2L,
                "{\"error\":\"Not found\"}",
                metrics,
                headers
        );

        assertEquals(2L, full.getId());
        assertEquals("{\"error\":\"Not found\"}", full.getBody());
        assertEquals(metrics, full.getMetrics());
        assertSame(headers, full.getHeaders());
    }

    @Test
    void testBuilderWithoutHeaders() {
        // Builder does not set headers by default -> should be null
        assertNull(apiResponse.getHeaders(), "Headers should be null when not set in builder");
    }

    @Test
    void testBuilderWithHeaders() {
        Map<String, String> map = Collections.singletonMap("Accept", "*/*");
        ApiResponseImpl withHeaders = ApiResponseImpl.builder()
                .id(3L)
                .body("OK")
                .metrics(metrics)
                .headers(map)
                .build();

        assertEquals(3L, withHeaders.getId());
        assertEquals("OK", withHeaders.getBody());
        assertEquals(metrics, withHeaders.getMetrics());
        assertSame(map, withHeaders.getHeaders());
    }

    @Test
    void testHeadersSetterGetter() {
        Map<String, String> map = Collections.singletonMap("Accept", "*/*");
        apiResponse.setHeaders(map);
        assertSame(map, apiResponse.getHeaders());
    }

    @Test
    void testEqualsAndHashCode() {
        ApiResponseImpl a = new ApiResponseImpl();
        a.setId(10L);
        ApiResponseImpl b = new ApiResponseImpl();
        b.setId(10L);
        ApiResponseImpl c = new ApiResponseImpl();
        c.setId(11L);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a.hashCode(), c.hashCode());
    }

    @Test
    void testToStringExcludesMetrics() {
        String tostr = apiResponse.toString();
        assertNotNull(tostr);
        assertTrue(tostr.contains("ApiResponseImpl"));
        assertTrue(tostr.contains("id=1"));
        assertTrue(tostr.contains("body=" + testBody));
        assertFalse(tostr.contains("metrics="), "toString should not include metrics field");
    }

    @Test
    void testBodyLarge() {
        String large = "x".repeat(5000);
        apiResponse.setBody(large);
        assertEquals(large, apiResponse.getBody());
    }

    @Test
    void testBidirectionalRelationship() {
        ApiResponseImpl resp = new ApiResponseImpl();
        resp.setId(5L);
        ApiMetricsImpl met = new ApiMetricsImpl();
        met.setId(5L);

        met.setResponse(resp);
        resp.setMetrics(met);

        assertEquals(met, resp.getMetrics());
        assertEquals(resp, met.getResponse());
    }
}
