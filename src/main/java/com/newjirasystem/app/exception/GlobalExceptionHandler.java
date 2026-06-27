package com.newjirasystem.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioNoEncontrado(UsuarioNoEncontradoException ex) {

        ErrorResponse error = new ErrorResponse(
                "USER_NOT_FOUND",
                ex.getMessage(),
                404,
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(TicketNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleTicketNoEncontrado(TicketNoEncontradoException ex) {

        ErrorResponse error = new ErrorResponse(
                "TICKET_NOT_FOUND",
                ex.getMessage(),
                404,
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(ProyectoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleProyectoNoEncontrado(ProyectoNoEncontradoException ex) {

        ErrorResponse error = new ErrorResponse(
                "PROYECTO_NOT_FOUND",
                ex.getMessage(),
                404,
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(ComentarioNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleComentarioNoEncontrado(ComentarioNoEncontradoException ex) {

        ErrorResponse error = new ErrorResponse(
                "COMENTARIO_NOT_FOUND",
                ex.getMessage(),
                404,
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleInputIncorrecto(IllegalArgumentException ex) {

        ErrorResponse error = new ErrorResponse(
                "BAD_REQUEST",
                ex.getMessage(),
                400,
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(
                        "UNAUTHORIZED",
                        "No se ha encontrado un usuario con las credenciales proporcionadas",
                        401,
                        Instant.now()
                ));
    }

    @ExceptionHandler(UsuarioDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioDuplicadoException(UsuarioDuplicadoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        "CONFLICT",
                        ex.getMessage(),
                        409,
                        Instant.now()
                ));
    }

    @ExceptionHandler(SesionExpiradaException.class)
    public ResponseEntity<ErrorResponse> handleSesionExpiradaException(SesionExpiradaException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(
                        "SESSION_EXPIRED",
                        ex.getMessage(),
                        401,
                        Instant.now()
                ));
    }

    @ExceptionHandler(TokenInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleTokenInvalidoException(TokenInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(
                        "TOKEN_INVALIDO",
                        ex.getMessage(),
                        401,
                        Instant.now()
                ));
    }
}
