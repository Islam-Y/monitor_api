package controller;

import com.apimonitor.config.ApiConfig;
import com.apimonitor.controller.ApiMonitorController;
import com.apimonitor.service.ApiMonitorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for ApiMonitorController using standalone MockMvc and Mockito.
 * Stubbing marked as lenient to avoid UnnecessaryStubbingException.
 */
@ExtendWith(MockitoExtension.class)
class ApiMonitorControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ApiMonitorService apiMonitorService;

    @Mock
    private ApiConfig apiConfig;

    @InjectMocks
    private ApiMonitorController controller;

    private ApiConfig.ApiEndpoint endpoint1;
    private ApiConfig.ApiEndpoint endpoint2;

    @BeforeEach
    void setUp() {
        // Initialize MockMvc with the controller under test
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        // Prepare test endpoints
        endpoint1 = new ApiConfig.ApiEndpoint();
        endpoint1.setUrl("https://jsonplaceholder.typicode.com/posts/1");
        endpoint1.setMethod("GET");
        endpoint1.setFrequencyMs(5000);
        endpoint1.setName("Test API");

        endpoint2 = new ApiConfig.ApiEndpoint();
        endpoint2.setUrl("https://jsonplaceholder.typicode.com/users/1");
        endpoint2.setMethod("GET");
        endpoint2.setFrequencyMs(10000);
        endpoint2.setName("User API");

        // Lenient stub for configuration so it's used only when needed
        lenient().when(apiConfig.getEndpoints()).thenReturn(List.of(endpoint1, endpoint2));
    }

    @Test
    void listEndpoints_ShouldReturnConfiguredEndpoints() throws Exception {
        mockMvc.perform(get("/api/monitor/endpoints")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].url", is(endpoint1.getUrl())))
                .andExpect(jsonPath("$[0].method", is(endpoint1.getMethod())))
                .andExpect(jsonPath("$[0].frequencyMs", is((int) endpoint1.getFrequencyMs())))
                .andExpect(jsonPath("$[0].name", is(endpoint1.getName())))
                .andExpect(jsonPath("$[1].url", is(endpoint2.getUrl())))
                .andExpect(jsonPath("$[1].method", is(endpoint2.getMethod())))
                .andExpect(jsonPath("$[1].frequencyMs", is((int) endpoint2.getFrequencyMs())))
                .andExpect(jsonPath("$[1].name", is(endpoint2.getName())));
    }

    @Test
    void runOnce_ShouldTriggerMonitoringAndReturnAccepted() throws Exception {
        mockMvc.perform(post("/api/monitor/run"))
                .andExpect(status().isAccepted());

        // Verify that the monitoring service was invoked
        verify(apiMonitorService, times(1)).monitorAllEndpoints();
    }
}

