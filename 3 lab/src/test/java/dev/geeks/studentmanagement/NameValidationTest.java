package dev.geeks.studentmanagement;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.geeks.studentmanagement.student.dto.StudentCreateRequest;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;

class NameValidationTest {
  @Test
  void validatesFullNameStructureAndLength() {
    try (var factory = Validation.buildDefaultValidatorFactory()) {
      var validator = factory.getValidator();
      String[] names = {
        "Маша Иванова",
        "Иванов Иван Иванович",
        "Анна-Мария Иванова",
        "John Smith",
        "dasadsadsadsadsadsadsadsadsadsadsadsadsad",
        "Иван",
        "Иванов 123",
        "Иванов Иван Иван Иван",
        "А Иванов",
        "Иванова\nМаша",
        "А".repeat(61) + " Иванова"
      };
      for (int i = 0; i < names.length; i++) {
        var request =
            new StudentCreateRequest(123456, names[i], "P1234", 5, 1234, "2020-09-01", false, "");
        assertEquals(i < 4, validator.validate(request).isEmpty(), names[i]);
      }
    }
  }
}
