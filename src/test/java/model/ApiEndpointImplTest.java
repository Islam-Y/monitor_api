package model;

import com.apimonitor.model.impl.ApiEndpointImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ApiEndpointImplTest {

    private ApiEndpointImpl endpoint;
    private Map<String, String> testHeaders;

    @BeforeEach
    void setUp() {
        endpoint = new ApiEndpointImpl();
        testHeaders = new HashMap<>();
        testHeaders.put("Content-Type", "application/json");
        testHeaders.put("Authorization", "Bearer token");
    }

    @Test
    void testGettersAndSetters() {
        // Set values
        endpoint.setId(1L);
        endpoint.setUrl("https://api.example.com/users");
        endpoint.setMethod("GET");
        endpoint.setFrequencyMs(5000);
        endpoint.setName("Get Users");
        endpoint.setHeaders(testHeaders);

        // Verify values
        assertEquals(1L, endpoint.getId());
        assertEquals("https://api.example.com/users", endpoint.getUrl());
        assertEquals("GET", endpoint.getMethod());
        assertEquals(5000, endpoint.getFrequencyMs());
        assertEquals("Get Users", endpoint.getName());
        assertEquals(testHeaders, endpoint.getHeaders());
    }

    @Test
    void testEqualsAndHashCode() {
        ApiEndpointImpl endpoint1 = new ApiEndpointImpl();
        endpoint1.setId(1L);

        ApiEndpointImpl endpoint2 = new ApiEndpointImpl();
        endpoint2.setId(1L);

        ApiEndpointImpl endpoint3 = new ApiEndpointImpl();
        endpoint3.setId(2L);

        // Test equals
        assertEquals(endpoint1, endpoint2);
        assertNotEquals(endpoint1, endpoint3);

        // Test hashCode
        assertEquals(endpoint1.hashCode(), endpoint2.hashCode());
        assertNotEquals(endpoint1.hashCode(), endpoint3.hashCode());
    }

    @Test
    void testToString() {
        endpoint.setId(1L);
        endpoint.setName("Test Endpoint");
        endpoint.setUrl("https://api.example.com/test");
        endpoint.setMethod("POST");

        String toStringResult = endpoint.toString();

        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("ApiEndpointImpl"));
        assertTrue(toStringResult.contains("id=1"));
        assertTrue(toStringResult.contains("name=Test Endpoint"));
        assertFalse(toStringResult.contains("headers")); // headers should be excluded
    }

    @Test
    void testNoArgsConstructor() {
        assertNotNull(new ApiEndpointImpl());
    }

    @Test
    void testHeaderManagement() {
        endpoint.setHeaders(testHeaders);
        assertEquals(2, endpoint.getHeaders().size());

        // Modify the headers map
        Map<String, String> newHeaders = new HashMap<>();
        newHeaders.put("Accept", "application/xml");
        endpoint.setHeaders(newHeaders);

        assertEquals(1, endpoint.getHeaders().size());
        assertTrue(endpoint.getHeaders().containsKey("Accept"));
    }

    @Test
    void testNullValues() {
        endpoint.setUrl(null);
        endpoint.setMethod(null);
        endpoint.setName(null);
        endpoint.setHeaders(null);

        assertNull(endpoint.getUrl());
        assertNull(endpoint.getMethod());
        assertNull(endpoint.getName());
        assertNull(endpoint.getHeaders());
    }

    @Test
    void testFrequencyMsEdgeCases() {
        endpoint.setFrequencyMs(0);
        assertEquals(0, endpoint.getFrequencyMs());

        endpoint.setFrequencyMs(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, endpoint.getFrequencyMs());
    }
}