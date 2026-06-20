package com.newjirasystem.app.auth;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDTO(
        @NotBlank(message = "El usuario es necesario")
        String username,
        @NotBlank(message = "La contraseña es necesaria")
        String password
        ) {
}
