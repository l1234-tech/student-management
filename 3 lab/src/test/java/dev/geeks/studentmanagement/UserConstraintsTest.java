package dev.geeks.studentmanagement;

import static org.junit.jupiter.api.Assertions.*;

import dev.geeks.studentmanagement.auth.dto.RegisterRequest;
import dev.geeks.studentmanagement.auth.model.*;
import jakarta.validation.Validation;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class UserConstraintsTest {
  @Test
  void validatesEntityAndRegistrationBoundaries() {
    try (var factory = Validation.buildDefaultValidatorFactory()) {
      var validator = factory.getValidator();
      var user = new User();
      assertEquals(4, validator.validate(user).size());
      user.setUsername("a".repeat(50));
      user.setEmail("valid@example.org");
      user.setPasswordHash("x".repeat(255));
      user.setRole(Role.USER);
      assertTrue(validator.validate(user).isEmpty());
      user.setUsername("a".repeat(51));
      user.setEmail("invalid");
      user.setPasswordHash(" ");
      assertEquals(3, validator.validate(user).size());
      assertTrue(
          validator
              .validate(new RegisterRequest("abc", "valid@example.org", "x".repeat(72)))
              .isEmpty());
      assertFalse(
          validator
              .validate(new RegisterRequest("ab", "valid@example.org", "x".repeat(73)))
              .isEmpty());
    }
  }

  @Test
  void temporalConverterPreservesUtcInstantAndNulls() {
    var converter = new InstantTimestampConverter();
    var instant = Instant.parse("2026-10-22T10:12:13.123456Z");
    assertEquals(
        instant, converter.convertToEntityAttribute(converter.convertToDatabaseColumn(instant)));
    assertNull(converter.convertToDatabaseColumn(null));
    assertNull(converter.convertToEntityAttribute(null));
  }
}
