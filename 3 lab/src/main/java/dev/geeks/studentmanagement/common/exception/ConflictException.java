package dev.geeks.studentmanagement.common.exception;

// Исключение для конфликта данных, например когда студент с таким ISU уже существует.
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
