package com.newjirasystem.app.comentarios;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ActualizarComentarioDTO(@NotBlank(message = "No se puede publicar un comentario vacío")
                                      @Size(max = 500, message = "El texto no puede superar los 500 caracteres")
                                      String texto) {
}
