// Универсальный DTO ответа для пагинации

package dev.geeks.studentmanagement.student.dto;

import java.util.List;

public record PageResponse<T>(
    List<T> data, Integer page, Integer size, Long totalElements, Integer totalPages) {}
