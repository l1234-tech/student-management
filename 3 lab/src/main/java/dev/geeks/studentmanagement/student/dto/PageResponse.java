package dev.geeks.studentmanagement.student.dto;

import java.util.List;

// Универсальный DTO ответа для пагинации.
public record PageResponse<T>(
        List<T> data,
        Integer page,
        Integer size,
        Long totalElements,
        Integer totalPages
) {
}
