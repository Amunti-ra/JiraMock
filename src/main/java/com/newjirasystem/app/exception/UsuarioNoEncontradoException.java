package com.newjirasystem.app.exception;

public class UsuarioNoEncontradoException extends RuntimeException {
    public UsuarioNoEncontradoException(Object id) {
        super("Usuario con ID " + id + " no encontrado");
    }

    public UsuarioNoEncontradoException(String nombre) {
        super("Usuario con username " + nombre + " no encontrado");
    }
}
