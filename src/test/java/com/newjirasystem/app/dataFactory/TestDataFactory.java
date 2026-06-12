package com.newjirasystem.app.dataFactory;

import com.newjirasystem.app.proyectos.Proyecto;
import com.newjirasystem.app.tickets.PrioridadTicket;
import com.newjirasystem.app.tickets.Ticket;
import com.newjirasystem.app.tickets.TipoTicket;
import com.newjirasystem.app.usuarios.Rol;
import com.newjirasystem.app.usuarios.Usuario;

public class TestDataFactory {

    public static Ticket crearTicket() {
        Proyecto proyecto = TestDataFactory.crearProyecto();
        Usuario usuario = TestDataFactory.crearUsuario();

        return new Ticket(
                "TEST-1",
                TipoTicket.TAREA,
                PrioridadTicket.LOW,
                proyecto,
                usuario,
                "prueba",
                "descripcion"
        );
    }

    public static Usuario crearUsuario() {
        return new Usuario("test", Rol.USER, "12345");
    }

    public static Proyecto crearProyecto() {
        return new Proyecto("Test de proyecto", "TEST");
    }
}
