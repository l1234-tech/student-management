// полный (PUT) или частичный (PATCH (могут быть null поля)) обновления студента

package dev.geeks.studentmanagement.student.dto;

public record StudentUpdateRequest(
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
    String note) {
  public StudentUpdateRequest {
    name = name == null ? null : name.strip();
    group = group == null ? null : group.strip();
    checkinDate = checkinDate == null ? null : checkinDate.strip();
    note = note == null ? null : note.strip();
  }
}
