package com.newjirasystem.app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String clave;

    @Enumerated(EnumType.STRING)
    private TipoTicket tipo;

    @Enumerated(EnumType.STRING)
    private EstadoTicket estado;

    @Enumerated(EnumType.STRING)
    private PrioridadTicket prioridad;

    @ManyToOne()
    @JoinColumn(name = "id_creador", nullable = false)
    private Proyecto proyecto;

    @ManyToOne
    private Usuario creador;

    @ManyToOne
    private Usuario asignado;

    private String titulo;
    private String descripcion;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaCompletado;

    // constructor vacio y con datos
    protected Ticket() {}

    public Ticket(String clave, TipoTicket tipo, PrioridadTicket prioridad, Proyecto proyecto, Usuario creador, String titulo, String descripcion) {
        this.clave = clave;
        this.tipo = tipo;
        this.estado = EstadoTicket.POR_HACER;
        this.prioridad = prioridad;
        this.proyecto = proyecto;
        this.creador = creador;
        this.asignado = null;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaCompletado = null;
        this.fechaCreacion = LocalDateTime.now();
    }


    // getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public TipoTicket getTipo() {
        return tipo;
    }

    public void setTipo(TipoTicket tipo) {
        this.tipo = tipo;
    }

    public EstadoTicket getEstado() {
        return estado;
    }

    public void setEstado(EstadoTicket estado) {
        this.estado = estado;
    }

    public PrioridadTicket getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(PrioridadTicket prioridad) {
        this.prioridad = prioridad;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    public Usuario getCreador() {
        return creador;
    }

    public void setCreador(Usuario duenio) {
        this.creador = duenio;
    }

    public Usuario getAsignado() {
        return asignado;
    }

    public void setAsignado(Usuario asignado) {
        this.asignado = asignado;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    // no debería poder cambiarse la fecha de creacion.
//    public void setFechaCreacion(LocalDateTime fechaCreacion) {
//        this.fechaCreacion = fechaCreacion;
//    }

    public LocalDateTime getFechaCompletado() {
        return fechaCompletado;
    }

    public void setFechaCompletado(LocalDateTime fechaCompletado) {
        this.fechaCompletado = fechaCompletado;
    }
}
