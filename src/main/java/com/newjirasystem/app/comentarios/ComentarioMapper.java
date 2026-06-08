package com.newjirasystem.app.comentarios;

import com.newjirasystem.app.tickets.Ticket;
import org.springframework.stereotype.Service;

@Service
public class ComentarioMapper {
    public ComentarioDTO toTicketDTO(Comentario comentario) {
        if (comentario == null) {
            return null;
        }

        return new ComentarioDTO(
                comentario.getId(),
                comentario.getTexto(),
                comentario.getTicket().getId(),
                comentario.getAutor().getNombre(),
                comentario.getFechaCreacion(),
                comentario.getFechaEditado()
        );
    }
}
