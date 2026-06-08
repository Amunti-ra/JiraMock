package com.newjirasystem.app.comentarios;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CrearComentarioDTO(@NotBlank(message = "No se puede publicar un comentario vacío")
                                 @Size(max = 500, message = "El texto no puede superar los 500 caracteres")
                                 String texto,
                                 @NotNull(message = "El ID del autor es obligatorio")
                                 Long idAutor) {
}
