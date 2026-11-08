package dev.geeks.studentmanagement.auth.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/** Explicit temporal mapping: do not let EclipseLink serialize Instant as binary data. */
@Converter
public class InstantTimestampConverter implements AttributeConverter<Instant, OffsetDateTime> {
  @Override
  public OffsetDateTime convertToDatabaseColumn(Instant value) {
    return value == null ? null : value.atOffset(ZoneOffset.UTC);
  }

  @Override
  public Instant convertToEntityAttribute(OffsetDateTime value) {
    return value == null ? null : value.toInstant();
  }
}
