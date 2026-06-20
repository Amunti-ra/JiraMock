package com.newjirasystem.app.exception;

public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException() {
        super("No se ha encontrado un usuario con las credenciales proporcionadas");
    }
}
