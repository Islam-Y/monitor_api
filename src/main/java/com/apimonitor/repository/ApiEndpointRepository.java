package com.apimonitor.repository;

import com.apimonitor.model.impl.ApiEndpointImpl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторий для управления сущностями API-эндпоинтов.
 * Позволяет выполнять стандартные CRUD-операции и при необходимости расширяется пользовательскими запросами.
 */
public interface ApiEndpointRepository  extends JpaRepository<ApiEndpointImpl, Long> {
    /**
     * Находит эндпоинт по его уникальному имени.
     *
     * @param name имя эндпоинта
     * @return Optional с сущностью ApiEndpointImpl, если найден
     */
    Optional<ApiEndpointImpl> findByName(String name);

    /**
     * Проверяет, существует ли эндпоинт с указанным именем.
     *
     * @param name имя эндпоинта
     * @return true, если сущность существует
     */
    boolean existsByName(String name);
}
