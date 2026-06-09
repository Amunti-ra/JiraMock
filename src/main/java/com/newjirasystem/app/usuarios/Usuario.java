package com.newjirasystem.app.usuarios;

import jakarta.persistence.*;

@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    @Column(length = 15)
    private String telefono;


    // Constructores vacio y full
    protected Usuario() {}

    public Usuario(String nombre, Rol rol, String telefono) {
        this.nombre = nombre;
        this.rol = rol;
        this.telefono = telefono;
    }


    // Getters y setters
    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
