// фильтрация, сортировка и пагинация (разбиение на группы\страницы) списка студентов

package dev.geeks.studentmanagement.student.dto;

public record StudentSearchRequest(
    Integer ISU,
    String name,
    String group,
    Integer dormitory,
    Integer room,
    @com.fasterxml.jackson.annotation.JsonProperty("is_foreign")
        @com.fasterxml.jackson.annotation.JsonAlias("isForeign")
        Boolean isForeign,
    String sort,
    Integer page,
    Integer size) {
  public StudentSearchRequest {
    name = name == null ? null : name.strip();
    group = group == null ? null : group.strip();
  }
}
