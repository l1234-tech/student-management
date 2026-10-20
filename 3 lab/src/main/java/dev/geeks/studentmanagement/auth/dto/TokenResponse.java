// DTO ответа после login/register/refresh: accessToken и refreshToken
// + именно описывает ответ сервера 

package dev.geeks.studentmanagement.auth.dto;

public record TokenResponse(String accessToken, String refreshToken) {}
