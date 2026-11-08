package dev.geeks.studentmanagement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import dev.geeks.studentmanagement.student.dto.StudentCreateRequest;
import dev.geeks.studentmanagement.student.dto.StudentSearchRequest;
import dev.geeks.studentmanagement.student.dto.StudentUpdateRequest;
import org.junit.jupiter.api.Test;

class StudentTextNormalizationTest {
  @Test
  void trimsCreationFieldsButKeepsSpacesBetweenWords() {
    var request =
        new StudentCreateRequest(
            123456,
            "  Маша Иванова  ",
            "  P1234  ",
            5,
            1234,
            " 2020-09-01 ",
            false,
            "  заметка  ");

    assertEquals("Маша Иванова", request.name());
    assertEquals("P1234", request.group());
    assertEquals("2020-09-01", request.checkinDate());
    assertEquals("заметка", request.note());
  }

  @Test
  void patchKeepsMissingFieldsAndTrimsProvidedValues() {
    var request =
        new StudentUpdateRequest(null, "  P1234  ", null, null, null, null, "   ");

    assertNull(request.name());
    assertNull(request.checkinDate());
    assertEquals("P1234", request.group());
    assertEquals("", request.note());
  }

  @Test
  void whitespaceOnlySearchIsAnEmptyCriterionNotAnAbsentOne() {
    var request =
        new StudentSearchRequest(
            null, "   ", "\t ", null, null, null, null, null, null, null, null);
    var empty =
        new StudentSearchRequest(
            null, null, null, null, null, null, null, null, null, null, null);

    assertEquals("", request.name());
    assertEquals("", request.group());
    assertNull(empty.name());
    assertNull(empty.group());
  }
}
