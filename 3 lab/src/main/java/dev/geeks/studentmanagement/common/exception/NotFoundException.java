// исключение для ситуации, когда ресурс не найден

package dev.geeks.studentmanagement.common.exception;

public class NotFoundException extends RuntimeException {
  public NotFoundException(String message) {
    super(message);
  }
}
