// единый формат ошибки API, все exception handler должны возвращать объект такого вида

package dev.geeks.studentmanagement.common.error;

import java.time.Instant;
import java.util.Map;

public record ApiError(
    String code, String message, int status, Instant timestamp, Map<String, String> details) {}
