package com.newjirasystem.app.auth;

import jakarta.validation.constraints.NotBlank;

public record AuthRequestDTO(@NotBlank(message = "El username es obligatorio")
                             String username,

                             @NotBlank(message = "La contraseña es obligatoria")
                             String password) {}
