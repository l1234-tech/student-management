package dev.geeks.studentmanagement.common.error;

import dev.geeks.studentmanagement.common.exception.*;
import java.time.Instant;
import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private ResponseEntity<Map<String, ApiError>> error(
      int status, String message, Map<String, String> details) {
    return ResponseEntity.status(status)
        .body(
            Map.of(
                "error",
                new ApiError(
                    HttpStatus.valueOf(status).name(), message, status, Instant.now(), details)));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> beanValidation(MethodArgumentNotValidException e) {
    var details = new LinkedHashMap<String, String>();
    e.getBindingResult()
        .getFieldErrors()
        .forEach(f -> details.put(f.getField(), f.getDefaultMessage()));
    return error(422, "Проверьте введённые данные", details);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<?> missing(NotFoundException e) {
    return error(404, e.getMessage(), Map.of());
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<?> conflict(ConflictException e) {
    return error(409, e.getMessage(), Map.of());
  }

  @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
  public ResponseEntity<?> integrity(Exception e) {
    return error(409, "Конфликт данных: проверьте уникальность полей", Map.of());
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<?> status(ResponseStatusException e) {
    return error(e.getStatusCode().value(), e.getReason(), Map.of());
  }

  @ExceptionHandler({
    org.springframework.http.converter.HttpMessageNotReadableException.class,
    org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class,
    org.springframework.web.bind.MissingServletRequestParameterException.class
  })
  public ResponseEntity<?> bad(Exception e) {
    return error(400, "Некорректный формат запроса или параметров", Map.of());
  }

  @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<?> method(Exception e) {
    return error(405, "Метод не поддерживается", Map.of());
  }

  @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
  public ResponseEntity<?> resource(Exception e) {
    return error(404, "Ресурс не найден", Map.of());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> unexpected(Exception e) {
    org.slf4j.LoggerFactory.getLogger(getClass()).error("Request failed", e);
    return error(500, "Внутренняя ошибка сервера", Map.of());
  }
}
