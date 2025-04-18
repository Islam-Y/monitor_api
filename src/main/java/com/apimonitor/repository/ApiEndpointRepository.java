package com.apimonitor.repository;

import com.apimonitor.model.impl.ApiEndpointImpl;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiEndpointRepository  extends JpaRepository<ApiEndpointImpl, Long> {
}
