// DTO фильтров списка студентов, сортировки и серверной пагинации.
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
    @com.fasterxml.jackson.annotation.JsonProperty("checkin_date")
        @com.fasterxml.jackson.annotation.JsonAlias("checkinDate")
        String checkinDate,
    String note,
    String sort,
    Integer page,
    Integer size) {
  public StudentSearchRequest {
    name = name == null ? null : name.strip();
    group = group == null ? null : group.strip();
    note = note == null ? null : note.strip();
    checkinDate = checkinDate == null ? null : checkinDate.strip();
  }
}
