package com.newjirasystem.app.tickets;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ActualizarTicketDTO(@Size(max = 150, message = "El título no puede superar los 150 carateres")
                                  String titulo,

                                  String descripcion,

                                  Long idAsignado, // id del usuario al que se ha asignado el ticket

                                  String estado, String prioridad, String tipo) {
}
