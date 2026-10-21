// исключение для конфликта данных, допустим,
// когда студент с таким ИСУ уже существует

package dev.geeks.studentmanagement.common.exception;

public class ConflictException extends RuntimeException {
  public ConflictException(String message) {
    super(message);
  }
}
