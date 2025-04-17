package com.apimonitor.model.impl;

import com.apimonitor.model.ApiEndpoint;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Map;

@Entity
@Table(name = "api_endpoints")
@Data
public class ApiEndpointImpl implements ApiEndpoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;
    private String method;
    private long frequencyMs;
    private String name;

    @ElementCollection
    @CollectionTable(name = "api_endpoint_headers", joinColumns = @JoinColumn(name = "endpoint_id"))
    @MapKeyColumn(name = "header_name")
    @Column(name = "header_value")
    private Map<String, String> headers;
}
