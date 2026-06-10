package com.newjirasystem.app.exception;

public class ProyectoNoEncontradoException extends RuntimeException {
    public ProyectoNoEncontradoException(Long id) {

        super("Proyecto con ID");
    }
}
