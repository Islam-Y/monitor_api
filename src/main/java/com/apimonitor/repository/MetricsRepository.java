package com.apimonitor.repository;

import com.apimonitor.model.impl.ApiMetricsImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MetricsRepository extends JpaRepository<ApiMetricsImpl, Long> {
//spring data jpa query method
  /**
   * Список всех уникальных apiName
   *
   */
  @Query("SELECT DISTINCT m.apiName FROM ApiMetricsImpl m")
  List<String> findDistinctApiName();

  /**
   * Поиск метрик по apiName и диапазону timestamp
   */
  List<ApiMetricsImpl> findByApiNameAndTimestampBetween(
          String apiName,
          LocalDateTime from,
          LocalDateTime to
  );

  /**
   * Общее количество запросов в диапазоне
   */
  long countByApiNameAndTimestampBetween(
          String apiName,
          LocalDateTime from,
          LocalDateTime to
  );

  /**
   * Количество неуспешных запросов (success = false)
   */
  long countByApiNameAndTimestampBetweenAndSuccessFalse(
          String apiName,
          LocalDateTime from,
          LocalDateTime to
  );

  /**
   * Количество всех неуспешных запросов (без фильтрации по apiName)
   */
  long countBySuccessFalse();

  /**
   * Среднее время ответа в миллисекундах для конкретного apiName
   */
  @Query("""
          SELECT AVG(m.responseTimeMs) FROM ApiMetricsImpl m \
                      WHERE m.apiName = :apiName \
                        AND m.timestamp BETWEEN :from AND :to""")
  Double findAverageResponseTime(
          @Param("apiName") String apiName,
          @Param("from") LocalDateTime from,
          @Param("to") LocalDateTime to
  );

  /**
   * Среднее время ответа в миллисекундах по всем записям
   */
  @Query("SELECT AVG(m.responseTimeMs) FROM ApiMetricsImpl m")
  Double findOverallAverageResponseTime();
}
