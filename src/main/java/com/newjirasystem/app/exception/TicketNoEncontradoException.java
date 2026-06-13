package com.newjirasystem.app.exception;

public class TicketNoEncontradoException extends RuntimeException {
    public TicketNoEncontradoException(Long id) {

        super("Ticket con ID " + id + " no encontrado");
    }
}
