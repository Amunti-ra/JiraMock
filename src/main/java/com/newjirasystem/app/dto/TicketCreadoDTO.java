package com.newjirasystem.app.dto;

import java.time.LocalDateTime;

public record TicketCreadoDTO(Long id,
                              String titulo,
                              String descripcion,
                              String clave,
                              String proyecto,
                              Long tipo,
                              Long estado,
                              Long prioridad,
                              String creador,
                              LocalDateTime fechaCreacion) {
}
