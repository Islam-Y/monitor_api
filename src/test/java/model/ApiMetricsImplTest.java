package model;

import com.apimonitor.model.impl.ApiEndpointImpl;
import com.apimonitor.model.impl.ApiMetricsImpl;
import com.apimonitor.model.impl.ApiResponseImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ApiMetricsImplTest {

    private ApiMetricsImpl metrics;
    private ApiEndpointImpl endpoint;
    private ApiResponseImpl response;
    private final LocalDateTime testTimestamp = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        endpoint = new ApiEndpointImpl();
        endpoint.setId(1L);
        endpoint.setName("Test Endpoint");
        endpoint.setUrl("https://api.example.com/test");

        response = new ApiResponseImpl();
        response.setId(1L);

        metrics = ApiMetricsImpl.builder()
                .id(1L)
                .endpoint(endpoint)
                .apiUrl("https://api.example.com/test")
                .apiName("Test Endpoint")
                .statusCode(200)
                .responseTimeMs(150)
                .timestamp(testTimestamp)
                .success(true)
                .errorMessage(null)
                .response(response)
                .build();
    }

    @Test
    void testBuilder() {
        assertNotNull(metrics);
        assertEquals(1L, metrics.getId());
        assertEquals(endpoint, metrics.getEndpoint());
        assertEquals("https://api.example.com/test", metrics.getApiUrl());
        assertEquals("Test Endpoint", metrics.getApiName());
        assertEquals(200, metrics.getStatusCode());
        assertEquals(150, metrics.getResponseTimeMs());
        assertEquals(testTimestamp, metrics.getTimestamp());
        assertTrue(metrics.isSuccess());
        assertNull(metrics.getErrorMessage());
        assertEquals(response, metrics.getResponse());
    }

    @Test
    void testNoArgsConstructor() {
        ApiMetricsImpl emptyMetrics = new ApiMetricsImpl();
        assertNotNull(emptyMetrics);
        assertNull(emptyMetrics.getId());
        assertNull(emptyMetrics.getEndpoint());
        assertNull(emptyMetrics.getApiUrl());
        assertNull(emptyMetrics.getApiName());
        assertEquals(0, emptyMetrics.getStatusCode());
        assertEquals(0, emptyMetrics.getResponseTimeMs());
        assertNull(emptyMetrics.getTimestamp());
        assertFalse(emptyMetrics.isSuccess());
        assertNull(emptyMetrics.getErrorMessage());
        assertNull(emptyMetrics.getResponse());
    }

    @Test
    void testAllArgsConstructor() {
        ApiMetricsImpl constructedMetrics = new ApiMetricsImpl(
                2L, endpoint, "https://api.example.com/another",
                "Another Endpoint", 404, 200, testTimestamp.plusHours(1),
                false, "Not Found", response
        );

        assertEquals(2L, constructedMetrics.getId());
        assertEquals("https://api.example.com/another", constructedMetrics.getApiUrl());
        assertEquals("Another Endpoint", constructedMetrics.getApiName());
        assertEquals(404, constructedMetrics.getStatusCode());
        assertEquals(200, constructedMetrics.getResponseTimeMs());
        assertEquals(testTimestamp.plusHours(1), constructedMetrics.getTimestamp());
        assertFalse(constructedMetrics.isSuccess());
        assertEquals("Not Found", constructedMetrics.getErrorMessage());
        assertEquals(response, constructedMetrics.getResponse());
    }

    @Test
    void testEqualsAndHashCode() {
        ApiMetricsImpl metrics1 = new ApiMetricsImpl();
        metrics1.setId(1L);

        ApiMetricsImpl metrics2 = new ApiMetricsImpl();
        metrics2.setId(1L);

        ApiMetricsImpl metrics3 = new ApiMetricsImpl();
        metrics3.setId(2L);

        assertEquals(metrics1, metrics2);
        assertNotEquals(metrics1, metrics3);
        assertEquals(metrics1.hashCode(), metrics2.hashCode());
        assertNotEquals(metrics1.hashCode(), metrics3.hashCode());
    }

    @Test
    void testToString() {
        // Given the metrics object is set up in @BeforeEach

        // When
        String toStringResult = metrics.toString();

        // Then
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("ApiMetricsImpl"));
        assertTrue(toStringResult.contains("id=1"));
        assertTrue(toStringResult.contains("statusCode=200"));

        // Verify response is excluded
        assertFalse(toStringResult.contains("response="),
                "toString() should not include the response field");

        // Verify other important fields are included
        assertTrue(toStringResult.contains("apiUrl="));
        assertTrue(toStringResult.contains("apiName="));
        assertTrue(toStringResult.contains("responseTimeMs="));
        assertTrue(toStringResult.contains("success="));
    }

    @ParameterizedTest
    @ValueSource(ints = {200, 201, 204})
    void testSuccessStatusCodes(int statusCode) {
        metrics.setStatusCode(statusCode);
        metrics.setSuccess(true);
        assertTrue(metrics.isSuccess());
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 401, 404, 500, 503})
    void testFailureStatusCodes(int statusCode) {
        metrics.setStatusCode(statusCode);
        metrics.setSuccess(false);
        metrics.setErrorMessage("Error occurred");
        assertFalse(metrics.isSuccess());
        assertEquals("Error occurred", metrics.getErrorMessage());
    }

    @Test
    void testResponseTimeEdgeCases() {
        metrics.setResponseTimeMs(0);
        assertEquals(0, metrics.getResponseTimeMs());

        metrics.setResponseTimeMs(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, metrics.getResponseTimeMs());
    }

    @Test
    void testTimestampAutoGeneration() {
        ApiMetricsImpl newMetrics = new ApiMetricsImpl();
        assertNull(newMetrics.getTimestamp());

        // In a real JPA environment, this would be set by @CreationTimestamp
        newMetrics.setTimestamp(LocalDateTime.now());
        assertNotNull(newMetrics.getTimestamp());
    }

    @Test
    void testResponseAssociation() {
        ApiResponseImpl newResponse = new ApiResponseImpl();
        newResponse.setId(2L);
        metrics.setResponse(newResponse);
        assertEquals(newResponse, metrics.getResponse());
    }

    @Test
    void testEndpointAssociation() {
        ApiEndpointImpl newEndpoint = new ApiEndpointImpl();
        newEndpoint.setId(2L);
        metrics.setEndpoint(newEndpoint);
        assertEquals(newEndpoint, metrics.getEndpoint());
    }
}
