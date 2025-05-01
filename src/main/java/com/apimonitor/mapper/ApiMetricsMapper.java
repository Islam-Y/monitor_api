package com.apimonitor.mapper;

import com.apimonitor.dto.ApiMetricsReport;
import com.apimonitor.dto.ApiMetricsSummary;
import com.apimonitor.model.impl.ApiMetricsImpl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper для преобразования внутренней модели метрик {@link ApiMetricsImpl}
 * в DTO-объекты отчётов и сводок по API.
 * <p>
 * Использует MapStruct с компонентной моделью Spring.
 */
@Mapper(componentModel = "spring")
public interface ApiMetricsMapper {

    /**
     * Преобразует объект метрик в подробный отчёт {@link ApiMetricsReport}.
     * Заполняет поля apiName и apiUrl из вложенного свойства endpoint.
     *
     * @param metrics объект с метриками и информацией об endpoint
     * @return DTO подробного отчёта по API
     */
    @Mapping(source = "apiName", target = "apiName")
    @Mapping(source = "apiUrl", target = "apiUrl")
    ApiMetricsReport toReport(ApiMetricsImpl metrics);

    /**
     * Преобразует объект метрик в сводную статистику {@link ApiMetricsSummary}.
     * Заполняет поля apiName и apiUrl из вложенного свойства endpoint.
     *
     * @param metrics объект с метриками и информацией об endpoint
     * @return DTO сводной статистики по API
     */
    @Mapping(source = "apiName", target = "apiName")
    @Mapping(source = "apiUrl", target = "apiUrl")
    ApiMetricsSummary toSummary(ApiMetricsImpl metrics);

    /**
     * Статический экземпляр для прямого вызова (не рекомендуется при использовании Spring).
     * Лучше инжектить бина через Spring Context.
     */
    ApiMetricsMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(ApiMetricsMapper.class);
}
