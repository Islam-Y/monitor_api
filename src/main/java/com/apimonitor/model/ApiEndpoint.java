package com.apimonitor.model;

import java.util.Map;

public interface ApiEndpoint {
    public String getUrl();

    public void setUrl(String url);

    public String getMethod();

    public void setMethod(String method);

    public long getFrequencyMs();

    public void setFrequencyMs(long frequencyMs);

    public String getName();

    public void setName(String name);

    public Map<String, String> getHeaders();

    public void setHeaders(Map<String, String> headers);
}
