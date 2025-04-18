package com.apimonitor.repository;

import com.apimonitor.model.impl.ApiEndpointImpl;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для управления сущностями API-эндпоинтов.
 * Позволяет выполнять стандартные CRUD-операции и при необходимости расширяется пользовательскими запросами.
 */
public interface ApiEndpointRepository  extends JpaRepository<ApiEndpointImpl, Long> {

}
