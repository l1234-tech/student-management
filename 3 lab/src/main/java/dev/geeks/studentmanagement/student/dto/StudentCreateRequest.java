package dev.geeks.studentmanagement.student.dto;

// DTO создания студента. Здесь позже нужно добавить Bean Validation аннотации.
public record StudentCreateRequest(
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
