package com.newjirasystem.app.tickets;

import org.springframework.stereotype.Service;

@Service
public class TicketMapper {
    /**
     * Recibe un ticket y lo transforma en un TicketDTO para poder pasarlo al TicketController con
     * los datos que se quieren hacer visibles al front end.
     *
     * Revisa si está asignado a alguien, evitar NullPointerException.
     *
     * @param ticket entidad Ticket de la base de datos.
     * @return TicketDTO con datos que se pasarán al front-end.
     */
    public TicketDTO toTicketDTO(Ticket ticket) {
        if (ticket == null) {
            return null;
        }

        String asignadoA = null;
        if (ticket.getAsignado() != null) {
            asignadoA = ticket.getAsignado().getNombre();
        }

        return new TicketDTO(
                ticket.getId(),
                ticket.getTitulo(),
                ticket.getDescripcion(),
                ticket.getClave(),
                ticket.getProyecto().getNombre(),
                ticket.getTipo(),
                ticket.getEstado(),
                ticket.getPrioridad(),
                ticket.getCreador().getNombre(),
                asignadoA,
                ticket.getFechaCreacion()
        );
    }
}
