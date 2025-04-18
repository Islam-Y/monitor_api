package com.apimonitor.repository;

import com.apimonitor.model.impl.ApiMetricsImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MetricsRepository extends JpaRepository<ApiMetricsImpl, Long> {
  /**
   * Возвращает список всех уникальных названий API (apiName).
   */
  @Query("SELECT DISTINCT m.apiName FROM ApiMetricsImpl m")
  List<String> findDistinctApiName();

  /**
   * Поиск метрик по имени API (apiName) и диапазону временных меток (timestamp).
   *
   * @param apiName имя API
   * @param from начальная граница диапазона
   * @param to конечная граница диапазона
   * @return список метрик, соответствующих критериям
   */
  List<ApiMetricsImpl> findByApiNameAndTimestampBetween(
          String apiName,
          LocalDateTime from,
          LocalDateTime to
  );

  /**
   * Подсчёт общего количества запросов для заданного имени API (apiName) в указанном диапазоне времени.
   *
   * @param apiName имя API
   * @param from начальная граница диапазона
   * @param to конечная граница диапазона
   * @return количество запросов
   */
  long countByApiNameAndTimestampBetween(
          String apiName,
          LocalDateTime from,
          LocalDateTime to
  );

  /**
   * Подсчёт количества неуспешных запросов (success = false) для заданного API (apiName)
   * в указанном диапазоне времени.
   *
   * @param apiName имя API
   * @param from начальная граница диапазона
   * @param to конечная граница диапазона
   * @return количество неуспешных запросов
   */
  long countByApiNameAndTimestampBetweenAndSuccessFalse(
          String apiName,
          LocalDateTime from,
          LocalDateTime to
  );

  /**
   * Подсчёт количества всех неуспешных запросов (success = false) без фильтрации по имени API.
   *
   * @return общее количество неуспешных запросов
   */
  long countBySuccessFalse();

  /**
   * Вычисляет среднее время ответа (responseTimeMs) для заданного API (apiName)
   * в указанном диапазоне времени.
   *
   * @param apiName имя API
   * @param from начальная граница диапазона
   * @param to конечная граница диапазона
   * @return среднее время ответа в миллисекундах
   */
  @Query("""
            SELECT AVG(m.responseTimeMs) FROM ApiMetricsImpl m\s
            WHERE m.apiName = :apiName\s
              AND m.timestamp BETWEEN :from AND :to
           \s""")
  Double findAverageResponseTime(
          @Param("apiName") String apiName,
          @Param("from") LocalDateTime from,
          @Param("to") LocalDateTime to
  );

  /**
   * Вычисляет среднее время ответа (responseTimeMs) для всех запросов без фильтрации.
   *
   * @return среднее время ответа в миллисекундах
   */
  @Query("SELECT AVG(m.responseTimeMs) FROM ApiMetricsImpl m")
  Double findOverallAverageResponseTime();
}