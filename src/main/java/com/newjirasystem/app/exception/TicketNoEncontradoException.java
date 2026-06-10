package com.newjirasystem.app.exception;

public class TicketNoEncontradoException extends RuntimeException {
    public TicketNoEncontradoException(Long id) {

        super("Usuario con ID " + id + " no encontrado");
    }
}
