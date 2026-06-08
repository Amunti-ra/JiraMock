package com.newjirasystem.app.tickets;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ActualizarTicketDTO(@Size(max = 150, message = "El título no puede superar los 150 carateres")
                                  String titulo,

                                  String descripcion,

                                  Long idAsignado, // id del usuario al que se ha asignado el ticket

                                  @Pattern(regexp = "POR_HACER|EN_PROGRESO|RESUELTO", message = "El estado debe ser POR_HACER, EN_PROGRESO, RESUELTO")
                                  String estado,

                                  @Pattern(regexp = "LOW|MEDIUM|HIGH|BLOCKER", message = "La prioridad debe ser LOW, MEDIUM, HIGH o BLOCKER")
                                  String prioridad,

                                  @Pattern(regexp = "EPICO|TAREA|BUG|SUBTAREA", message = "El tipo debe ser SUBTAREA, TAREA, BUG o EPICO")
                                  String tipo) {
}
