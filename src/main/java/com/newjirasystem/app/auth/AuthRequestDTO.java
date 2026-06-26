package com.newjirasystem.app.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequestDTO(@NotBlank(message = "El username es obligatorio")
                             @Size(min = 4, max = 100, message = "El username debe tener entre 4 y 100 caracteres")
                             String username,

                             @NotBlank(message = "La contraseña es obligatoria")
                             @Size(min = 8, message = "La contraseña debe tener mínimo 8 caracteres")
                             String password) {}
