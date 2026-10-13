package dev.geeks.studentmanagement.student.dto;

// DTO ответа API для студента. Внешний контракт не должен зависеть напрямую от JPA entity.
public record StudentDto(
        Integer ISU,
        String name,
        String group,
        Integer dormitory,
        Integer room,
        String checkinDate,
        Boolean isForeign,
        String note
) {
}
