package com.newjirasystem.app.tickets;

import java.time.LocalDateTime;

public record TicketDTO(Long id,
                        String titulo,
                        String descripcion,
                        String clave,
                        String proyecto,
                        Long tipo,
                        Long estado,
                        Long prioridad,
                        String creador,
                        String asignadoA,
                        LocalDateTime fechaCreacion) {
}
