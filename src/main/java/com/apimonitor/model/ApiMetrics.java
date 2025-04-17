package com.apimonitor.model;

import com.apimonitor.model.impl.ApiResponseImpl;

import java.time.LocalDateTime;

public interface ApiMetrics {
    public Long getId();

    public void setId(Long id);

    public String getApiUrl();

    public void setApiUrl(String apiUrl);

    public String getApiName();

    public void setApiName(String apiName);

    public int getStatusCode();

    public void setStatusCode(int statusCode);

    public long getResponseTimeMs();

    public void setResponseTimeMs(long responseTimeMs);

    public LocalDateTime getTimestamp();

    public void setTimestamp(LocalDateTime timestamp);

    public boolean isSuccess();

    public void setSuccess(boolean success);

    public String getErrorMessage();

    public void setErrorMessage(String errorMessage);

    public ApiResponseImpl getResponse();

    public void setResponse(ApiResponseImpl response);
}
