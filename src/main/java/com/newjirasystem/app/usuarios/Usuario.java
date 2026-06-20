package com.newjirasystem.app.usuarios;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 60)
    private String password;

    @Enumerated(EnumType.STRING)
    private Rol rol;


    // Constructores vacio y full
    protected Usuario() {}

    public Usuario(String nombre, Rol rol, String password) {
        this.nombre = nombre;
        this.rol = rol;
        this.password = password;
    }


    @Override
    public String getUsername() {
        return this.nombre;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }


    // Getters y setters
    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    protected void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void actualizarNombre(String nombre) {
        if (nombre == null || nombre.length() < 4) {
            throw new IllegalArgumentException("El username debe tener al menos 4 caracteres.");
        }
        this.nombre = nombre;
    }

    public Rol getRol() {
        return rol;
    }

    protected void setRol(Rol rol) {
        this.rol = rol;
    }

    @Override
    public boolean isAccountNonExpired() {return true;}

    @Override
    public boolean isAccountNonLocked() {return true;}

    @Override
    public boolean isCredentialsNonExpired() {return true;}

    @Override
    public boolean isEnabled() {return true;}
}
