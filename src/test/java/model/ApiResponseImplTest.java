package model;

import com.apimonitor.model.impl.ApiMetricsImpl;
import com.apimonitor.model.impl.ApiResponseImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    void testBuilder() {
        assertNotNull(apiResponse);
        assertEquals(1L, apiResponse.getId());
        assertEquals(testBody, apiResponse.getBody());
        assertEquals(metrics, apiResponse.getMetrics());
    }

    @Test
    void testNoArgsConstructor() {
        ApiResponseImpl emptyResponse = new ApiResponseImpl();
        assertNotNull(emptyResponse);
        assertNull(emptyResponse.getId());
        assertNull(emptyResponse.getBody());
        assertNull(emptyResponse.getMetrics());
    }

    @Test
    void testAllArgsConstructor() {
        ApiResponseImpl constructedResponse = new ApiResponseImpl(
                2L,
                "{\"error\":\"Not found\"}",
                metrics
        );

        assertEquals(2L, constructedResponse.getId());
        assertEquals("{\"error\":\"Not found\"}", constructedResponse.getBody());
        assertEquals(metrics, constructedResponse.getMetrics());
    }

    @Test
    void testEqualsAndHashCode() {
        ApiResponseImpl response1 = new ApiResponseImpl();
        response1.setId(1L);

        ApiResponseImpl response2 = new ApiResponseImpl();
        response2.setId(1L);

        ApiResponseImpl response3 = new ApiResponseImpl();
        response3.setId(2L);

        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }

    @Test
    void testToString() {
        String toStringResult = apiResponse.toString();
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("ApiResponseImpl"));
        assertTrue(toStringResult.contains("id=1"));
        assertTrue(toStringResult.contains("body=" + testBody));
        assertFalse(toStringResult.contains("metrics="),
                "toString() should not include the metrics field");
    }

    @Test
    void testBodyManagement() {
        // Test null body
        apiResponse.setBody(null);
        assertNull(apiResponse.getBody());

        // Test empty body
        apiResponse.setBody("");
        assertEquals("", apiResponse.getBody());

        // Test large body
        String largeBody = "a".repeat(10000);
        apiResponse.setBody(largeBody);
        assertEquals(largeBody, apiResponse.getBody());
    }

    @Test
    void testBidirectionalRelationship() {
        ApiResponseImpl newResponse = new ApiResponseImpl();
        newResponse.setId(2L);

        ApiMetricsImpl newMetrics = new ApiMetricsImpl();
        newMetrics.setId(2L);
        newMetrics.setResponse(newResponse);
        newResponse.setMetrics(newMetrics);

        assertEquals(newMetrics, newResponse.getMetrics());
        assertEquals(newResponse, newMetrics.getResponse());
    }

    @Test
    void testLobAnnotationBehavior() {
        // This would typically be tested with an integration test against a real database
        // Here we just verify the field can handle large text
        String xmlBody = "<response><data>".repeat(1000) + "</data></response>";
        apiResponse.setBody(xmlBody);
        assertEquals(xmlBody, apiResponse.getBody());
    }
}
