package mx.com.adrian.demo.model;

public record AuthResponse(String accessToken, String refreshToken, String type, Long expiresIn) {}