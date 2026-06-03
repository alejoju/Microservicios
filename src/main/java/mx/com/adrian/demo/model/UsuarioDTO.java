package mx.com.adrian.demo.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioDTO(
    Integer id,
    @NotBlank String username,
    @NotBlank String password,
    @Email @NotBlank String email,
    String nombreCompleto,
    @NotNull mx.com.adrian.demo.entity.UsuarioEntity.RolUsuario rol,
    Boolean activo
) {}