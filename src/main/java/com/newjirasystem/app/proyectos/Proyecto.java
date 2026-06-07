package com.newjirasystem.app.proyectos;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
public class Proyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String codigoProyecto;
    private int contadorTickets = 0;

    public Proyecto(String nombre, String codigoProyecto) {
        this.nombre = nombre;
        this.codigoProyecto = codigoProyecto;
    }

    protected Proyecto() {}

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String name) {
        this.nombre = name;
    }

    public String getCodigoProyecto() {
        return codigoProyecto;
    }

    public void setCodigoProyecto(String codigoProyecto) {
        this.codigoProyecto = codigoProyecto;
    }

    public int getContadorTickets() {
        return contadorTickets;
    }

    public void setContadorTickets(int contadorTickets) {
        this.contadorTickets = contadorTickets;
    }
}
