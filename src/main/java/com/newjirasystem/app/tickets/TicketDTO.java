package com.newjirasystem.app.tickets;

import java.time.LocalDateTime;

public record TicketDTO(Long id,
                        String titulo,
                        String descripcion,
                        String clave,
                        String proyecto,
                        String tipo,
                        String estado,
                        String prioridad,
                        String creador,
                        String asignadoA,
                        LocalDateTime fechaCreacion) {
}
