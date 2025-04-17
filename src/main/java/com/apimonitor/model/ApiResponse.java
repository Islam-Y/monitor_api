package com.apimonitor.model;

import com.apimonitor.model.impl.ApiMetricsImpl;
import com.apimonitor.model.impl.ApiResponseImpl;

public interface ApiResponse {
    public Long getId();

    public void setId(Long id);

    public String getBody();

    public void setBody(String body);

    public ApiMetricsImpl getMetrics();

    public void setMetrics(ApiMetricsImpl metrics);
}
