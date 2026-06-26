package com.newjirasystem.app.exception;

public class UsuarioDuplicadoException extends RuntimeException {
    public UsuarioDuplicadoException() {
        super("El nombre de usuario ya existe");
    }
}
