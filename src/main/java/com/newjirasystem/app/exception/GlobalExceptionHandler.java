package com.newjirasystem.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
                "USER_NOT_FOUND",
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
                "USER_NOT_FOUND",
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
                "USER_NOT_FOUND",
                ex.getMessage(),
                404,
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleInputIncorrecto(String mensaje) {

        ErrorResponse error = new ErrorResponse(
                "BAD_REQUEST",
                mensaje,
                400,
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
}
