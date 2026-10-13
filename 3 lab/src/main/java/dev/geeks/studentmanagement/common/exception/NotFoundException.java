package dev.geeks.studentmanagement.common.exception;

// Исключение для ситуации, когда ресурс не найден.
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
