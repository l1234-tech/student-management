// DTO создания студента, проверка полей выполняется через Bean Validation
// описываем поля , которые клиент должен передать при создании студента (сущности)  

package dev.geeks.studentmanagement.student.dto;

import jakarta.validation.constraints.*;

public record StudentCreateRequest(
    @NotNull @Min(100000) @Max(999999) Integer ISU,
    @NotBlank(message = "Введите ФИО")
        @Size(max = 60, message = "ФИО должно быть не длиннее 60 символов")
        @Pattern(
            regexp =
                "[A-Za-zА-Яа-яЁё]{2,}(?:-[A-Za-zА-Яа-яЁё]{2,})*(?: [A-Za-zА-Яа-яЁё]{2,}(?:-[A-Za-zА-Яа-яЁё]{2,})*){1,2}",
            message =
                "Введите фамилию и имя, при необходимости отчество; используйте буквы, пробелы и дефисы")
        String name,
    @NotBlank @Pattern(regexp = "[A-Za-zА-Яа-яЁё][0-9]{4,5}") String group,
    @NotNull @Min(1) @Max(20) Integer dormitory,
    @NotNull @Min(1000) @Max(9999) Integer room,
    @NotBlank
        @com.fasterxml.jackson.annotation.JsonProperty("checkin_date")
        @com.fasterxml.jackson.annotation.JsonAlias("checkinDate")
        String checkinDate,
    @NotNull
        @com.fasterxml.jackson.annotation.JsonProperty("is_foreign")
        @com.fasterxml.jackson.annotation.JsonAlias("isForeign")
        Boolean isForeign,
    @Size(max = 2000) String note) {
  public StudentCreateRequest {
    name = name == null ? null : name.strip();
    group = group == null ? null : group.strip();
    checkinDate = checkinDate == null ? null : checkinDate.strip();
    note = note == null ? null : note.strip();
  }
}
