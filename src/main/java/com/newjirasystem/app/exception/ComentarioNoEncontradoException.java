package com.newjirasystem.app.exception;

public class ComentarioNoEncontradoException extends RuntimeException {
    public ComentarioNoEncontradoException(Long id) {

        super("Comentario con ID " + id + " no encontrado");
    }
}
