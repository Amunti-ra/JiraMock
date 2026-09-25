package com.newjirasystem.app.auth;

import com.newjirasystem.app.comentarios.ComentarioRepository;
import com.newjirasystem.app.tickets.TicketsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomSecCheck {

    private final ComentarioRepository comentarioRepository;
    private final TicketsRepository ticketsRepository;

    public CustomSecCheck(ComentarioRepository comentarioRepository, TicketsRepository ticketsRepository) {
        this.comentarioRepository = comentarioRepository;
        this.ticketsRepository = ticketsRepository;
    }

    public boolean esCreadorComentario(Long id, String nombreAuth) {
        return comentarioRepository.findByIdAndActivoTrue(id)
                .map(coment -> coment.getAutor().getNombre().equals(nombreAuth))
                .orElse(false);
    }

    public boolean esCreadorTicket(Long id, String nombreAuth) {
        return ticketsRepository.findByIdAndActivoTrue(id)
                .map(ticket -> ticket.getCreador().getNombre().equals(nombreAuth))
                .orElse(false);
    }
}
