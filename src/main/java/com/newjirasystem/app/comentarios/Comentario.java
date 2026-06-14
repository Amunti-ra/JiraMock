package com.newjirasystem.app.comentarios;

import com.newjirasystem.app.tickets.Ticket;
import com.newjirasystem.app.usuarios.Usuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Comentario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String texto;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaEditado;

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Usuario autor;

    private boolean activo;

    // Constructores vacio y full
    protected Comentario() {}

    // fecha es generada automáticamente
    public Comentario(String texto, Ticket ticket, Usuario autor) {
        this.texto = texto;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaEditado = null;
        this.ticket = ticket;
        this.autor = autor;
        this.activo = true;
    }


    public void borrarComentario() {
        this.activo = false;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public String getTexto() {
        return texto;
    }

    // para evitar cambiar el texto sin modificar la fecha de edición, incluye cambio en fecha de editado
    // sustituye setTexto
    public void editarTexto(String texto) {
        this.texto = texto;
        this.fechaEditado = LocalDateTime.now();
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaEditado() {
        return fechaEditado;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public Usuario getAutor() {
        return autor;
    }

    public boolean isActivo() {
        return activo;
    }
}
