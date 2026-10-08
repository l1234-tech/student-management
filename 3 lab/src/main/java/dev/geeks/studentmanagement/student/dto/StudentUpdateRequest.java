package dev.geeks.studentmanagement.student.dto;

// DTO полного или частичного обновления студента. Для PATCH поля могут быть null.
public record StudentUpdateRequest(
        String name,
        String group,
        Integer dormitory,
        Integer room,
        String checkinDate,
        Boolean isForeign,
        String note
) {
}
