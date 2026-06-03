package mx.com.adrian.demo.model;

import jakarta.validation.constraints.NotBlank;

public record PasswordChangeRequest(@NotBlank String newPassword) {}