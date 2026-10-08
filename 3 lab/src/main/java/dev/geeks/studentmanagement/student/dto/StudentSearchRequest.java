package dev.geeks.studentmanagement.student.dto;

// DTO фильтрации, сортировки и пагинации списка студентов.
public record StudentSearchRequest(
        Integer ISU,
        String name,
        String group,
        Integer dormitory,
        Integer room,
        Boolean isForeign,
        String sort,
        Integer page,
        Integer size
) {
}
