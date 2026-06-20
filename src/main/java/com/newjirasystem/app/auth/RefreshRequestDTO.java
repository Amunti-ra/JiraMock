package com.newjirasystem.app.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequestDTO(@NotBlank String refreshToken) {
}
