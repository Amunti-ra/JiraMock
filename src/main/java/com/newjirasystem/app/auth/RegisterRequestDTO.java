package com.newjirasystem.app.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
        @NotBlank(message = "El usuario es necesario")
        @Size(min = 4, max = 100, message = "El usuario debe tener entre 4 y 100 caracteres")
        String username,
        @NotBlank(message = "La contraseña es necesaria")
        @Size(min = 8, message = "La contraseña requiere mínimo 8 caracteres")
        String password
        ) {
}
