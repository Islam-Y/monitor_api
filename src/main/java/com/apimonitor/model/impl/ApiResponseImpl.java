package com.apimonitor.model.impl;

import com.apimonitor.model.ApiResponse;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "api_responses")
@Data
public class ApiResponseImpl implements ApiResponse {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String body;

    @OneToOne(mappedBy = "response", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ApiMetricsImpl metrics;
}
