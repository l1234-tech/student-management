// DTO ответа API для студента, внешний контракт не должен зависеть напрямую от JPA сущности

package dev.geeks.studentmanagement.student.dto;

public record StudentDto(
    Integer ISU,
    String name,
    String group,
    Integer dormitory,
    Integer room,
    @com.fasterxml.jackson.annotation.JsonProperty("checkin_date")
        @com.fasterxml.jackson.annotation.JsonAlias("checkinDate")
        String checkinDate,
    @com.fasterxml.jackson.annotation.JsonProperty("is_foreign")
        @com.fasterxml.jackson.annotation.JsonAlias("isForeign")
        Boolean isForeign,
    String note) {}
