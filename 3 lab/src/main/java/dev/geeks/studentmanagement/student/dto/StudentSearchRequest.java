package dev.geeks.studentmanagement.student.dto;

// DTO фильтрации, сортировки и пагинации списка студентов.
public record StudentSearchRequest(
        Integer ISU,
        String name,
        String group,
        Integer dormitory,
        Integer room,
        @com.fasterxml.jackson.annotation.JsonProperty("is_foreign") @com.fasterxml.jackson.annotation.JsonAlias("isForeign") Boolean isForeign,
        String sort,
        Integer page,
        Integer size
) {
    public StudentSearchRequest {
        name = name == null ? null : name.strip();
        group = group == null ? null : group.strip();
    }
}
