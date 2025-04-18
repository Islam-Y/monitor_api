package com.apimonitor.model.impl;

import com.apimonitor.model.ApiResponse;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "api_responses")
@Getter
@Setter
@EqualsAndHashCode
@ToString
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
