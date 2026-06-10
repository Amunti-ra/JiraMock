package com.newjirasystem.app.tickets;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CrearTicketDTO(@NotBlank(message = "El título es obligatorio")
                             @Size(max = 150, message = "El título no puede superar los 150 carateres")
                             String titulo,

                             String descripcion,

                             @NotNull(message = "El ID del creador es obligatorio")
                             Long idCreador,

                             @NotNull(message = "El ID del proyecto es obligatorio")
                             Long idPoryecto,

                             PrioridadTicket prioridad,

                             TipoTicket tipo) {
}
