package com.newjirasystem.app.entity;

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

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Usuario autor;

    // Constructores vacio y full
    protected Comentario() {}
    // fecha es generada automáticamente
    public Comentario(Long id, String texto, Ticket ticket, Usuario autor) {
        this.id = id;
        this.texto = texto;
        this.fechaCreacion = LocalDateTime.now();
        this.ticket = ticket;
        this.autor = autor;
    }


    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public Ticket getTicket() {
        return ticket;
    }

    // debería poder moverse el comentario a otro ticket?
    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Usuario getAutor() {
        return autor;
    }

    // debería poder cambiar el autor?
    public void setAutor(Usuario autor) {
        this.autor = autor;
    }
}
