package mx.com.adrian.demo.model;

import jakarta.validation.Valid;

public record UsuarioCreateRequest(@Valid UsuarioDTO usuario) {}