package com.newjirasystem.app.comentarios;

import java.time.LocalDateTime;

public record ComentarioDTO(Long id,
                            String texto,
                            Long ticket,
                            String nombreAutor,
                            LocalDateTime fechaCreacion,
                            LocalDateTime fechaEditado) {
}
