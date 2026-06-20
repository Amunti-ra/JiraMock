package com.newjirasystem.app.exception;

public class SesionExpiradaException extends RuntimeException {
    public SesionExpiradaException() {
        super("Tu sesión ha expirado, vuelve a hacer login");
    }
}
