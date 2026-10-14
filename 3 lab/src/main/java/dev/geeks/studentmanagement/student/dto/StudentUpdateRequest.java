package dev.geeks.studentmanagement.student.dto;

// DTO полного или частичного обновления студента. Для PATCH поля могут быть null.
public record StudentUpdateRequest(
        String name,
        String group,
        Integer dormitory,
        Integer room,
        @com.fasterxml.jackson.annotation.JsonProperty("checkin_date") @com.fasterxml.jackson.annotation.JsonAlias("checkinDate") String checkinDate,
        @com.fasterxml.jackson.annotation.JsonProperty("is_foreign") @com.fasterxml.jackson.annotation.JsonAlias("isForeign") Boolean isForeign,
        String note
) {
}
